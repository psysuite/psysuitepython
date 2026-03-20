import math
import numpy as np
from scipy.stats import norm
from scipy.optimize import curve_fit
import matplotlib.pyplot as plt

from adopy.tasks.psi import Task2AFC, ModelLogistic
from adopy.tasks.psi import EnginePsi
from scipy.stats import bernoulli

# can use model-derived and externally-defined latencies
# try (10 times) to avoid presenting stimuli within an exclusion_width large window
class BISAdopyWrapper:

    def __init__(self, adoparams=None, taskparams=None):

        self.range      = taskparams.get("range", 700)
        self.ntrials    = taskparams.get("ntrials", 50)
        self.offset     = taskparams.get("offset", 0)
        self.stim_min   = taskparams["min"]
        self.exclusion_width = taskparams.get("exclusion_width", 40)  # Exclude ms around offset

        self.guess_rate = adoparams["guess_rate"]
        self.lapse_rate = adoparams["lapse_rate"]
        self.noise_perc = adoparams["noise_perc"]

        self.model_stim = []  # [{"stimulus":0} for i in range(self.ntrials)]
        self.stimuli_q  = []  # [0 for i in range(self.ntrials)]
        self.responses  = []
        self.stimuli_ms = []
        self.successes  = []

        self.pre_offset_nstim  = 0
        self.post_offset_nstim = 0

        self.task   = Task2AFC()
        self.model  = ModelLogistic()

        # Create continuous stimulus design space (no exclusion zone)
        self.designs = {
            'stimulus': np.linspace(
                self.offset - self.range/2,
                self.offset + self.range/2,
                200
            )
        }
        self.params = {
            'guess_rate': [self.guess_rate],
            'lapse_rate': [self.lapse_rate],
            'threshold' : np.linspace(
                                    self.offset - self.range/2,  # Keep wide: encourages exploration
                                    self.offset + self.range/2,  # ADOpy must explore to find true threshold
                                    200),
            'slope'     : np.logspace(-2, 0, 200)  # 0.01 to 1.0, emphasizes shallow slopes
        }

        self.engine = EnginePsi(self.model, self.designs, self.params)

    # get stimulus value in milliseconds
    def get(self, addNoise=False):
        """
        continuous space + random + post-random exclusion of stimuli around offset latency.
        """
        model = self.engine.get_design("optimal")
        stim_ms = model["stimulus"]

        max_attempts = 10
        for attempt in range(max_attempts):

            if addNoise:
                stim_ms += int((np.random.rand() - 0.5) * self.noise_perc * self.range)
                
            # Check if result falls in exclusion zone
            if abs(stim_ms - self.offset) > self.exclusion_width/2:
                break
            # Otherwise, try again with different random noise
        else:
            # If all attempts failed, send it to the excluded limit of the least represented side
            stim_ms = self.offset - self.exclusion_width/2 if self.post_offset_nstim > self.pre_offset_nstim else self.offset + self.exclusion_width/2

            print(f"Warning: Could not avoid exclusion zone after {max_attempts} attempts, set {stim_ms}")
        
        stim_ms = np.clip(stim_ms, self.offset - self.range/2, self.offset + self.range/2)
        
        model["stimulus"] = stim_ms

        if stim_ms > self.offset:
            self.post_offset_nstim += 1
        else:
            self.pre_offset_nstim += 1

        self.stimuli_ms.append(stim_ms)
        self.model_stim.append(model)
        
        return stim_ms

    # accept 0/1 responses
    # The 2AFC model will learn P(response=1) as a function of stimulus
    # This should be monotonically increasing, with threshold at offset
    def set(self, response, q_value=None, index=-1):

        if response not in (0, 1):
            print("WARNING, response_or_success value not valid")
            return

        if q_value is not None:
            model = {"stimulus": q_value}
            self.stimuli_ms.append(q_value)
            self.model_stim.append(model)
        else:
            if not self.model_stim:
                print("ERROR: No stimulus model available. Call get() first or provide q_value.")
                return
            model = self.model_stim[index]

        self.engine.update(model, response)

        # Calculate success based on stimulus position relative to offset
        if (self.stimuli_ms[-1] > self.offset and response == 1) or (self.stimuli_ms[-1] < self.offset and response == 0):
            success = 1
        else:
            success = 0

        self.responses.append(response)
        self.successes.append(success)

    def gausFit(self, binSize):
        """ Fit cumulative Gaussian to response data """

        # Use stimuli_ms and successes for absolute mode
        if len(self.stimuli_ms) == 0:
            raise ValueError("No data collected yet. Run experiment first.")

        SR          = sorted(list(zip(self.stimuli_ms, self.responses)))
        bins        = [i * binSize for i in range(math.floor(SR[0][0] / binSize), math.ceil(SR[-1][0] / binSize) + 1)]
        x           = [s[0] for s in SR]
        i_binned    = np.digitize(x, bins)
        x_binned    = np.asarray(bins)[i_binned - 1]
        r           = np.asarray([sr[1] for sr in SR])
        f           = [sum(r[x_binned == b]) / len(r[x_binned == b]) if len(r[x_binned == b]) > 0 else math.nan for b in bins]
        goodx       = [not math.isnan(y) for y in f]
        f           = np.asarray(f)[goodx]
        bins        = np.asarray(bins)[goodx]
        
        # Fit cumulative Gaussian
        try:
            # Better initial guess: threshold at offset, sigma as reasonable fraction of range
            p0 = [self.offset, self.range / 10]
            # Add bounds to prevent unreasonable fits
            bounds = ([self.offset - self.range, 1], [self.offset + self.range, self.range])
            mu1, sigma1 = curve_fit(norm.cdf, bins, f, p0=p0, bounds=bounds, maxfev=10000)[0]
        except Exception as e:
            print(f"Fit failed: {e}")
            # If fit fails, return mean and std of stimuli
            mu1 = np.mean(self.stimuli_ms)
            sigma1 = np.std(self.stimuli_ms)
        
        return mu1, abs(sigma1)  # Ensure sigma is positive

    def plot_psychometric(self, outfile_name, binSize=10):
        """ Plot psychometric function with fitted curve """

        if len(self.stimuli_ms) == 0:
            print("No data to plot")
            return None

        # Compute binned data
        SR = sorted(list(zip(self.stimuli_ms, self.responses)))
        bins = [i * binSize for i in range(math.floor(SR[0][0] / binSize), math.ceil(SR[-1][0] / binSize) + 1)]
        x = [s[0] for s in SR]
        i_binned = np.digitize(x, bins)
        x_binned = np.asarray(bins)[i_binned - 1]
        r = np.asarray([sr[1] for sr in SR])
        f = [sum(r[x_binned == b]) / len(r[x_binned == b]) if len(r[x_binned == b]) > 0 else math.nan for b in bins]
        goodx = [not math.isnan(y) for y in f]
        f = np.asarray(f)[goodx]
        bins = np.asarray(bins)[goodx]

        # Get fitted parameters
        mu1, sigma1 = self.gausFit(binSize)

        # Plot
        plt.figure(figsize=(10, 6))
        x_fit = np.linspace(min(self.stimuli_ms) - 50, max(self.stimuli_ms) + 50, 200)
        plt.plot(x_fit, norm.cdf(x_fit, mu1, sigma1), 'b-', alpha=0.7, label=f'Fit: μ={mu1:.1f}, σ={sigma1:.1f}')
        plt.plot(bins, f, 'ro', markersize=8, alpha=0.7, label='Data')
        plt.axvline(self.offset, color='g', linestyle='--', label=f'True threshold ({self.offset}ms)')
        plt.axhline(0.5, color='gray', linestyle=':', alpha=0.5)
        plt.xlabel('Stimulus time (ms)')
        plt.ylabel('P(response = 2)')
        plt.title(outfile_name)
        plt.legend()
        plt.grid(True, alpha=0.3)

        plt.savefig(outfile_name, bbox_inches='tight')
        plt.show()

        return mu1, sigma1
    
    def print_statistics(self):
        """ Print statistics about collected data """
        if len(self.stimuli_ms) == 0:
            print("No data collected yet")
            return
        
        stimuli = np.array(self.stimuli_ms)
        below_offset = stimuli < self.offset
        above_offset = stimuli > self.offset
        
        print("\n" + "="*50)
        print("EXPERIMENT STATISTICS")
        print("="*50)
        print(f"Total trials: {len(stimuli)}")
        print(f"Offset (true threshold): {self.offset} ms")
        print(f"\nStimulus distribution:")
        print(f"  Below {self.offset}ms: {np.sum(below_offset)} trials ({100*np.sum(below_offset)/len(stimuli):.1f}%)")
        print(f"  Above {self.offset}ms: {np.sum(above_offset)} trials ({100*np.sum(above_offset)/len(stimuli):.1f}%)")
        print(f"\nStimulus range: {stimuli.min():.1f} - {stimuli.max():.1f} ms")
        print(f"Mean stimulus: {stimuli.mean():.1f} ms")
        print(f"Median stimulus: {np.median(stimuli):.1f} ms")
        print(f"Std deviation: {stimuli.std():.1f} ms")
        
        if len(self.successes) > 0:
            successes = np.array(self.successes)
            print(f"\nResponse accuracy: {100*successes.mean():.1f}%")
        
        print("="*50 + "\n")

    def get_simulated_response(self, model, design):
        # Compute a probability to respond positively.
        p_obs = model.compute(choice=[[[0, 1]]], stimulus=design["stimulus"],guess_rate=0.5, lapse_rate=0.04,threshold=20, slope=1.5)
        # Compute a random binary choice
        return bernoulli.rvs(p_obs)