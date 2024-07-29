import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
}

group = project.properties["group.id"].toString()
version = project.properties["version"].toString()

dependencies {
    ksp(project(":Kernl.Processor"))
    compileOnly(project(":Kernl.Annotations"))
    implementation(project(":Kernl.Runtime"))
    implementation(libs.kotlinx.coroutines)

    kspTest(project(":Kernl.Processor"))
    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}