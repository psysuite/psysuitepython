from adopy.tasks.psi import Task2AFC, ModelWeibull, ModelLogistic
from adopy.tasks.psi import EnginePsi
from adopy import Model
from scipy.stats import bernoulli
from typing import NamedTuple

import numpy as np

class Stimulus(NamedTuple):
    q: float
    ms: int


class AdopyWrapper:

    def __init__(self, adoparams=None, taskparams=None):

        self.range      = taskparams["range"]
        self.ntrials    = taskparams["ntrials"]
        self.stim_min   = taskparams["min"]


        self.guess_rate = adoparams["guess_rate"]
        self.lapse_rate = adoparams["lapse_rate"]
        self.noise_perc = adoparams["noise_perc"]

        self.model_stim = [] #[{"stimulus":0} for i in range(self.ntrials)]
        self.stimuli_q  = [] #[0 for i in range(self.ntrials)]
        self.responses  = []
        self.successes  = []

        self.task   = Task2AFC()
        self.model  = ModelLogistic()

        self.designs = {    'stimulus': np.linspace(self.stim_min, self.range, 200)       }

        self.params = {
            'guess_rate': [self.guess_rate],
            'lapse_rate': [self.lapse_rate],
            'threshold': np.linspace(self.stim_min, self.range, 200),
            'slope': np.linspace(0, 200, 200)
        }

        self.engine = EnginePsi(self.model, self.designs, self.params)

    # get stim q-value, add noise, update model
    def get(self, addNoise=True, append=True):
        model   = self.engine.get_design("optimal")
        stim_q  = model["stimulus"]

        if addNoise:
            stim_q += int((np.random.rand(1)[0]-0.5) * self.noise_perc * self.range)

        model["stimulus"] = stim_q

        if append:
            self.stimuli_q.append(stim_q)
            self.model_stim.append(model)

        return stim_q

    # response 0:first, 1:third
    def set(self, success, q_value=None, index=-1, append=True):

        # if len(self.model_stim) == 0:
        #     self.model_stim[0] = {"stimulus": 0}
            
        model = self.model_stim[index]

        if q_value is not None:
            model["stimulus"] = q_value

        self.model_stim[index] = model
        self.engine.update(model, success)

        if append:
            self.successes.append(success)

#     def gausFit(self, binSize):
#         """ fitting gaussiana cumulativa COME IN GAUSSBOOTFIT """
#         import matplotlib.pyplot as plt
#         from scipy.stats import norm
#         from scipy.optimize import curve_fit
#         import math
#
#         SR = sorted(list(zip(self.stimuli_ms, self.responses)))
#         bins = [i * binSize for i in range(math.floor(SR[0][0] / binSize), math.ceil(SR[-1][0] / binSize))]
#         x = [s[0] for s in SR]
#         i_binned = np.digitize(x, bins)
#         x_binned = np.asarray(bins)[i_binned - 1]
#         r = np.asarray([sr[1] for sr in SR])
#         f = [sum(r[x_binned == b]) / len(r[x_binned == b]) if len(r[x_binned == b]) > 0 else math.nan for b in bins]
#         goodx = [not math.isnan(y) for y in f]
#         f = np.asarray(f)[goodx]
#         bins = np.asarray(bins)[goodx]

        #mu1, sigma1 = curve_fit(norm.cdf, bins, f, p0=[0, 1])[0]
        #plt.plot(bins, norm.cdf(bins, mu1, sigma1), alpha=.5)
        #plt.plot(bins, f, 'o', alpha=.5)
#         return mu1, sigma1

    # def get_simulated_response(self, model, design):
    #     # Compute a probability to respond positively.
    #     p_obs = model.compute(choice=[[[0, 1]]], stimulus=design["stimulus"],guess_rate=0.5, lapse_rate=0.04,threshold=20, slope=1.5)
    #     # Compute a random binary choice
    #     return bernoulli.rvs(p_obs)

