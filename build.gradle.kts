/*
 * Copyright 2024 Neopsis GmbH. All Rights Reserved.
 */

repositories {
    maven(url = providers.gradleProperty("niagaraToolsHome").get() + "/gradlePlugins")
    mavenCentral()
    gradlePluginPortal()
}

plugins {
    id("neopsis-project-plugin")
}

neopsisParts {
    description   = "Envas Commons"
    moduleVersion = "0.5"
}


niagaraSigning {
    aliases.set(listOf(providers.gradleProperty("signingCertificateAlias").get()))
    signingProfileFile.set(file(providers.gradleProperty("signingProfileFile").get()))
}


////////////////////////////////////////////////////////////////
// Dependencies and configurations... configuration
////////////////////////////////////////////////////////////////

subprojects {
    repositories {
        mavenCentral()
    }
}
