// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(Plugins.androidApplication) version Versions.androidApplication apply(false)
    id(Plugins.kotlinAndroid) version Versions.kotlin apply(false)
    id(Plugins.chaquopy) version Versions.chaquopy apply(false)
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}
