plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
}

ext {
    set("ARTIFACT_ID", "AutoRepo.Runtime")
    set("PUBLICATION_NAME", "autoRepoRuntime")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation("com.google.truth:truth:1.4.3")
    testImplementation("junit:junit:4.13.2")
}