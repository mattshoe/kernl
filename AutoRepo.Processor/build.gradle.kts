plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
}

ext {
    set("ARTIFACT_ID", "AutoRepo.Processor")
    set("PUBLICATION_NAME", "autoRepoProcessor")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":AutoRepo.Annotations"))
    implementation(project(":AutoRepo.Runtime"))

    implementation("io.github.mattshoe.shoebox:Stratify:1.1.0-beta15")
    implementation(libs.kotlin.poet)
    implementation(libs.kotlin.poet.ksp)
    implementation(libs.kotlinx.coroutines)

    testImplementation(kotlin("test"))
    testImplementation(libs.compile.testing.ksp)
    testImplementation(libs.compile.testing)
    testImplementation(libs.truth)
    testImplementation(libs.junit)
}