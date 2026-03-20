plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id("com.chaquo.python")

}

android {
    namespace = Configs.psysuitepythonnamespace
    compileSdkVersion(Configs.compileSdkVersion)

    defaultConfig {

        minSdkVersion(Configs.minSdkVersion)
        targetSdkVersion(Configs.targetSdkVersion)
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

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}