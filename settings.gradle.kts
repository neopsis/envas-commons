/*
 * Copyright 2024 Neopsis GmbH. All Rights Reserved.
 *
 * Check:
 *    https://stackoverflow.com/questions/69149466/gradle-7-2-how-to-apply-a-custom-gradle-settings-plugin
 *    https://discuss.gradle.org/t/how-to-configure-a-settings-plugin/45613
 *
 */



// apply (from = providers.gradleProperty("niagaraToolsHome").get() + "/default_settings.gradle.kts")


import com.tridium.gradle.plugins.settings.LocalSettingsExtension
import com.tridium.gradle.plugins.settings.MultiProjectExtension

pluginManagement {

    val gradlePluginVersion: String by System.getProperties()
    val settingsPluginVersion: String by System.getProperties()
    val neopsisPluginVersion: String by System.getProperties()

    repositories {
        mavenCentral()
        maven(url = providers.gradleProperty("niagaraToolsHome").get() + "/gradlePlugins")
        gradlePluginPortal()
    }

    plugins {

        id("neopsis-project-plugin")   version (neopsisPluginVersion)
        id("neopsis-module-plugin")     version (neopsisPluginVersion)
        id("neopsis-settings-plugin")   version (neopsisPluginVersion)

        id("com.tridium.settings.multi-project") version (settingsPluginVersion)
        id("com.tridium.settings.local-settings-convention") version (settingsPluginVersion)

        id("com.tridium.niagara")         version (gradlePluginVersion)
        id("com.tridium.vendor")          version (gradlePluginVersion)
        id("com.tridium.niagara-module")  version (gradlePluginVersion)
        id("com.tridium.niagara-signing") version (gradlePluginVersion)

        id("com.tridium.convention.niagara-home-repositories") version (gradlePluginVersion)

    }
}

plugins {
    // Discover all subprojects in this build
    id("com.tridium.settings.multi-project")

    // Apply local settings from my-settings.gradle(.kts) if they are present
    id("com.tridium.settings.local-settings-convention")
}

configure<LocalSettingsExtension> {
    loadLocalSettings()
}

configure<MultiProjectExtension> {
    // Note: If you have specific subfolder(s) to include, you can pass their relative
    // path as an argument:
    //
    //   findProjects("folder1")
    //   findProjects("folder2")
    //
    // Otherwise, this will find all projects under the root directory with the following layout:
    //
    //    project-rt/project-rt.gradle.kts
    //    project-rt/project-rt.gradle
    //    project-rt/build.gradle.kts
    //    project-rt/build.gradle
    findProjects()
}

rootProject.name = "envasCommons"