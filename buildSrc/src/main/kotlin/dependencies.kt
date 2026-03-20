object Configs {

    const val psysuitepythonnamespace   = "org.albaspazio.psysuite.python"
    const val versionCode       = 66
    const val versionName       = "2.5.3.${versionCode}"

    const val compileSdkVersion = 34
    const val minSdkVersion     = 26
    const val targetSdkVersion  = 26
}

object Plugins {

    const val androidApplication    = "com.android.application"

    // org.albaspazio.psysuite.core & org.albaspazio.core
    const val androidLibrary        = "com.android.library"
    const val kotlinAndroid         = "org.jetbrains.kotlin.android"
    const val kotlinParcelize       = "org.jetbrains.kotlin.plugin.parcelize"

    const val chaquopy              = "com.chaquo.python"
}

object Versions {

    const val androidApplication = "7.4.2"
    const val gradlePlugin = "4.1.1"
    const val navVersion = "2.3.5"
    const val navSafeArgsGradlePlugin = "1.0.0"
    const val fragment = "1.4.0"

    const val permissions = "2.0.54"
    const val legacy_support = "1.0.0"


    // org.albaspazio.psysuite.core
    const val chaquopy          = "17.0.0"
    const val legacySupport     = "1.0.0"
    const val preference        = "1.2.0"
    const val recycleView       = "1.2.1"
    const val coroutines        = "1.6.4"


    // org.albaspazio.core
    const val kotlin = "1.9.22"
    const val ktxCore = "1.13.1"
    const val appCompat = "1.6.1"
    const val androidLibrary = "8.2.2"
    const val kparcelablePlugin = "1.7.0"
    const val constraintLayout = "2.1.4"
    const val material = "1.12.0"
    const val lifecycle = "2.6.1"
    const val localbroadcastmanager = "1.1.0"
    const val navFragment = "2.3.5"
    const val moshi = "1.12.0"
    const val rxkotlin = "2.4.0"
    const val rxandroid = "2.1.1"
//    const val sunmail = "1.6.7"

    const val junit = "4.13.2"
    const val testRunner = "1.5.2"
    const val testEspressoCore = "3.6.1"
}

object Dependencies {

    const val permissions       = "io.github.nishkarsh:android-permissions:${Versions.permissions}"

    object AndroidX {

        const val localbroadcastmanager   = "androidx.localbroadcastmanager:localbroadcastmanager:${Versions.localbroadcastmanager}"
        const val legacy_support    = "androidx.legacy:legacy-support-v4:${Versions.legacy_support}"
        const val fragment          = "androidx.fragment:fragment:${Versions.fragment}"

        // org.albaspazio.psysuite.core
        const val preference        = "androidx.preference:preference-ktx:${Versions.preference}"
        const val recycleView       = "androidx.recyclerview:recyclerview:${Versions.recycleView}"
        const val legacySupport     = "androidx.legacy:legacy-support-v4:${Versions.legacySupport}"

        // org.albaspazio.core
        const val lifecycleviewmodel= "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
        const val navFragment       = "androidx.navigation:navigation-fragment-ktx:${Versions.navFragment}"
        const val navUi             = "androidx.navigation:navigation-ui-ktx:${Versions.navFragment}"
        const val ktxCore           = "androidx.core:core-ktx:${Versions.ktxCore}"
        const val appCompat         = "androidx.appcompat:appcompat:${Versions.appCompat}"
        const val constraintLayout  = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val material          = "com.google.android.material:material:${Versions.material}"

        const val lifecycledataKtx  = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
        const val lifecyclecommon   = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"

        const val testRunner        = "androidx.test:runner:${Versions.testRunner}"
        const val testEspressoCore  = "androidx.test.espresso:espresso-core:${Versions.testEspressoCore}"
    }

    object Kotlin {
        const val stdLib    = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
        const val reflect   = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    }

    object Moshi {
        const val moshi     = "com.squareup.moshi:moshi:${Versions.moshi}"
        const val moshiKt   = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    }

    object rx {
        const val rxandroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxandroid}"
        const val rxrelay   = "com.jakewharton.rxrelay2:rxrelay:${Versions.rxandroid}"
        const val rxkotlin  = "io.reactivex.rxjava2:rxkotlin:${Versions.rxkotlin}"
    }

//    object sunmail {
//        const val mail          = "com.sun.mail:android-mail:${Versions.sunmail}"
//        const val activation    = "com.sun.mail:android-activation:${Versions.sunmail}"
//    }


    const val junit         = "junit:junit:${Versions.junit}"
}

object ProGuards {
    const val androidDefault = "proguard-rules.pro"
    const val proguardTxt = "proguard-android.txt"
}