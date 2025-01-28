/*
 * Copyright 2024 Neopsis GmbH. All Rights Reserved.
 *
 */

pluginManagement {

    val neopsisPluginVersion: String by System.getProperties()

    repositories {

        maven(url = providers.gradleProperty("niagaraToolsHome").get() + "/gradlePlugins")
        maven(url = uri("https://repo.repsy.io/mvn/neopsis/niagara"))
        mavenCentral()
        // mavenLocal()
        gradlePluginPortal()

    }

    plugins {
        id("com.neopsis.niagara-settings-plugin") version (neopsisPluginVersion)
        id("com.neopsis.niagara-project-plugin")   version (neopsisPluginVersion)
        id("com.neopsis.niagara-module-plugin")   version (neopsisPluginVersion)
    }
}

plugins {
    id("com.neopsis.niagara-settings-plugin")
}

rootProject.name = "envasCommons"