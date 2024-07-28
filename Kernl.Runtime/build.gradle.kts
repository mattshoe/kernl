plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
}

private val ARTIFACT_ID = "Kernl.Runtime"

ext {
    set("ARTIFACT_ID", ARTIFACT_ID)
    set("PUBLICATION_NAME", "kernlRuntime")
}

dependencies {
    implementation(libs.kotlinx.coroutines)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.truth)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}