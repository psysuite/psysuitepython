plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.chaquopy)
}

android {

    namespace = Configs.psysuitepythonnamespace
    compileSdk = Configs.compileSdkVersion

    lint {  }

    defaultConfig {
        minSdk = Configs.minSdkVersion
        targetSdk = Configs.targetSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile(ProGuards.proguardTxt), ProGuards.androidDefault)

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

chaquopy {
    defaultConfig {
        version = "3.10"
        pip {
            install("matplotlib")
            install("numpy==1.23.3")
            install("scipy==1.8.1")
            install("adopy")
        }
    }
    //productFlavors { }    sourceSets { }
}

dependencies {

    implementation(Dependencies.Kotlin.reflect)
    implementation(Dependencies.AndroidX.ktxCore)
    implementation(Dependencies.AndroidX.appCompat)
    implementation(Dependencies.AndroidX.material)
    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.AndroidX.junitExt)
    androidTestImplementation(Dependencies.AndroidX.testEspressoCore)
}