// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(libs.plugins.android.library.get().pluginId) version libs.versions.androidGradlePlugin.get() apply(false)
    id(libs.plugins.kotlin.android.get().pluginId) version libs.versions.kotlinPlugin.get() apply(false)
    id(libs.plugins.chaquopy.get().pluginId) version libs.versions.chaquopyPlugin.get() apply(false)
}

ext["javaVersion"] = libs.versions.javaVersion.get()
ext["ndkVersion"] = libs.versions.ndkVersion.get()

tasks.register("clean", Delete::class){
    description = "Deletes the build directory"
    delete(rootProject.layout.buildDirectory)
}

