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

    implementation("com.squareup:kotlinpoet:1.17.0")
    implementation("com.squareup:kotlinpoet-ksp:1.17.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.0-1.0.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")

    testImplementation(kotlin("test"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.6.0")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.5.0")
    testImplementation("com.google.truth:truth:1.4.3")
    testImplementation("junit:junit:4.13.2")
}