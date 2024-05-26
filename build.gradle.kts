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
    description   = "Koster Components"
    moduleVersion = "0.5"
}

vendor {
    // vendor is taken from neopsisParts
    defaultVendor(neopsisParts.vendor)

    // Module full version is
    //    <majorNiagaraVersion.minorNiagaraVersion.moduleVersion>
    // 'moduleVersion' is defined in the 'neopsisParts' block.The module version numbering
    // is fully open and can contain any number of parts
    //
    // if the property 'neopsisParts.followNiagaraNumbering' is true (default), <majorNiagaraVersion> and
    // <minorNiagaraVersion> numbers are derived from the Niagara version we are compiling again.
    //
    // if the property 'neopsisParts.followNiagaraNumbering' is false, the property
    // 'neopsisParts.moduleVersion' is the full module version
    //
    defaultModuleVersion(neopsisParts.moduleFullVersion)
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
