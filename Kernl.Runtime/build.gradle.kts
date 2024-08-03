plugins {
    kotlin("jvm")
    id("maven-publish")
    signing

    kotlin("plugin.serialization") version "2.0.0" // TODO remove this
}

private val ARTIFACT_ID = "Kernl.Runtime"

ext {
    set("ARTIFACT_ID", ARTIFACT_ID)
    set("PUBLICATION_NAME", "kernlRuntime")
}

dependencies {
    implementation(libs.kotlinx.coroutines)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.truth)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}