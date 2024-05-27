/*
 * Copyright 2024 Neopsis GmbH. All Rights Reserved.
 *
 */


pluginManagement {

    val neopsisPluginVersion: String by System.getProperties()

    repositories {
        maven(url = providers.gradleProperty("niagaraToolsHome").get() + "/gradlePlugins")
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("neopsis-project-plugin")    version (neopsisPluginVersion)
        id("neopsis-module-plugin")     version (neopsisPluginVersion)
        id("neopsis-settings-plugin")   version (neopsisPluginVersion)
    }
}

plugins {
    id("neopsis-settings-plugin")
}

rootProject.name = "envasCommons"