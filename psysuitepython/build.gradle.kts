plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    id(libs.plugins.chaquopy.get().pluginId)
}

android {
    compileSdk = Configs.compileSdkVersion
    namespace = Configs.psysuitepythonnamespace

    defaultConfig {
        minSdk = Configs.minSdkVersion
        targetSdk = Configs.targetSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile(ProGuards.proguardTxt), ProGuards.androidDefault)
        }
    }

    compileOptions {
        val javaVer = JavaVersion.toVersion(rootProject.ext["javaVersion"] as String)
        sourceCompatibility = javaVer
        targetCompatibility = javaVer
    }

    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = rootProject.ext["javaVersion"] as String
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
}

dependencies {
    implementation(libs.kotlin.reflect.legacy)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
