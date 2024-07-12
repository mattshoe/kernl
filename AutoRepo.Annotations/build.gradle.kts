plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
}

ext {
    set("ARTIFACT_ID", "AutoRepo.Annotations")
    set("PUBLICATION_NAME", "autoRepoAnnotations")
}

dependencies {
    testImplementation(kotlin("test"))
}