plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
    alias(libs.plugins.dokka)
}

ext {
    set("ARTIFACT_ID", "AutoRepo.Processor")
    set("PUBLICATION_NAME", "autoRepoProcessor")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":AutoRepo.Annotations"))
    implementation(project(":AutoRepo.Runtime"))

    implementation(libs.kotlin.poet)
    implementation(libs.kotlin.poet.ksp)
    implementation(libs.ksp.symbol.processing.api)
    implementation(libs.kotlinx.coroutines)

    testImplementation(kotlin("test"))
    testImplementation(libs.compile.testing.ksp)
    testImplementation(libs.compile.testing)
    testImplementation(libs.truth)
    testImplementation(libs.junit)
}