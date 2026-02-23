/*
 * Copyright (c) 2015 Neopsis GmbH. All Rights Reserved.
 */


/*
 * Copyright 2026 Neopsis. All Rights Reserved.
 */

import com.tridium.gradle.plugins.bajadoc.task.Bajadoc
import com.tridium.gradle.plugins.grunt.task.GruntBuildTask
import com.tridium.gradle.plugins.niagara.NiagaraProjectLayout
import com.tridium.gradle.plugins.niagara.service.niagaraEnvironmentService
import jdk.jfr.internal.JVM.exclude
import jdk.jfr.internal.JVM.include
import org.gradle.internal.classpath.Instrumented.systemProperty

repositories {
    maven(url = "file:///C:/Program%20Files/Niagara/5.0.0.1/etc/m2/repository")
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

plugins {
    // The Niagara Module plugin configures the "moduleManifest" extension and the
    // "jar" and "moduleTestJar" tasks.
    id("com.tridium.n-module")

    // The signing plugin configures the correct signing of modules. It requires
    // that the plugin also be applied to the root project.
    id("com.tridium.sign")

    // The bajadoc plugin configures the generation of Bajadoc for a module.
    id("com.tridium.bajadoc")

    // Configures JaCoCo for the "niagaraTest" task of this module.
    id("com.tridium.jacoco")

    // The Annotation processors plugin adds default dependencies on ":nre"
    // for the "annotationProcessor" and "moduleTestAnnotationProcessor"
    // configurations by creating a single "niagaraAnnotationProcessor"
    // configuration they extend from. This value can be overridden by explicitly
    // declaring a dependency for the "niagaraAnnotationProcessor" configuration.
    id("com.tridium.nap")

    // The niagara_home repositories convention plugin configures !bin/ext and
    // !modules as flat-file Maven repositories so that projects in this build can
    // depend on already-installed Niagara modules.
    id("com.tridium.conv.n-repo")

    id("com.tridium.grunt")

    id("java-library")
}

description = "Envas - Commons for Niagara5"

niagaraProject {
    niagaraProjectLayout.set(NiagaraProjectLayout.NIAGARA_MAVEN)
}

moduleManifest {
    moduleName.set("envasCommons")
    // NOTE: Temporarily ignore rt module part checks for module conversion exercise
    ignoreRuntimeProfileCheck.set("true")
    checkModuleName.set(false)
}

// See documentation at module://docDeveloper/doc/build.html#dependencies for the supported dependency types

dependencies {

    nre(":nre")
    api(":baja")

    uberjar("net.engio:mbassador:1.3.2")
    uberjar("com.alibaba.fastjson2:fastjson2:2.0.61")

    compileOnly("org.apache.xmlgraphics:batik-awt-util:1.19")
    moduleTestCompileOnly("org.apache.xmlgraphics:batik-awt-util:1.19")

    moduleTestImplementation(":test")
    testImplementation("org.testng:testng:7.1.0")

}

tasks.named<GruntBuildTask>("gruntBuild") {
    tasks("babel:dist", "copy:dist", "requirejs")
}

tasks.named<Bajadoc>("bajadoc") {
    // Each of the packages you wish to include in your module's API documentation must be
    // enumerated below
    includePackage("com.neopsis.n5demo")
}

tasks.withType<JavaCompile>().configureEach {
    val niagaraEnvironment = project.niagaraEnvironmentService
    options.compilerArgs.addAll(
        listOf(
            "-Xlint:deprecation",
            "-Xlint:overrides",
            "-Xlint:unchecked",
            "-Werror",
            "--module-path", classpath.asPath + File.pathSeparator
                    + niagaraEnvironment.niagaraHome.dir("bin/ext").get().asFile.absolutePath + File.pathSeparator
                    + niagaraEnvironment.niagaraConfigHome.dir("modules").get().asFile.absolutePath
        )
    )
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

tasks.test {
    // discover and execute TestNG-based tests
    useTestNG()

    // explicitly include or exclude tests
    include( "test//**")

    // show standard out and standard error of the test JVM(s) on the console
    testLogging.showStandardStreams = true

    // set heap size for the test JVM(s)
    minHeapSize = "128m"
    maxHeapSize = "512m"

    // set JVM arguments for the test JVM(s)
    // jvmArgs("-XX:MaxPermSize=256M")

    // fail the 'test' task on the first test failure
    failFast = true

    // skip an actual test execution
    dryRun = false

}
