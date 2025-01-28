/*
 * Copyright (c) 2015 Neopsis GmbH. All Rights Reserved.
 */

import com.tridium.gradle.plugins.module.util.ModulePart.RuntimeProfile.rt
import com.tridium.gradle.plugins.niagara.NiagaraProjectLayout
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude
import java.time.LocalDate

plugins {
    id("com.neopsis.niagara-module-plugin")
}

description = "Envas - Commons for Niagara4"

moduleManifest {
    preferredSymbol.set("env")
    moduleName.set("envasCommons")
    runtimeProfile.set(rt)
}

niagaraProject {
    niagaraProjectLayout.set(NiagaraProjectLayout.NIAGARA_MAVEN)
}

dependencies {

    nre(":nre")
    api(":baja")

    uberjar("net.engio:mbassador:1.3.2")
    uberjar("com.alibaba:fastjson:1.2.73")

    testImplementation("junit:junit:4.12") {
        exclude(group="org.hamcrest")
    }
    testImplementation("org.hamcrest:hamcrest-library:1.3")
}

tasks.clean {
    delete += listOf("build", "out")
}

tasks.jar {

    includeEmptyDirs = true

    exclude("maven/")
    exclude("services/")
    exclude("NOTICE.txt")
    exclude("META-INF/services/**")
    exclude("META-INF/maven/**")
    exclude("META-INF/NOTICE.txt")

}
