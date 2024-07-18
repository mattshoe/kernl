plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
}

private val ARTIFACT_ID = "AutoRepo.Runtime"

ext {
    set("ARTIFACT_ID", ARTIFACT_ID)
    set("PUBLICATION_NAME", "autoRepoRuntime")
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