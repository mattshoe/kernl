plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
}

ext {
    set("ARTIFACT_ID", "Kernl.Processor")
    set("PUBLICATION_NAME", "kernlProcessor")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":Kernl.Annotations"))
    implementation(project(":Kernl.Runtime"))

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