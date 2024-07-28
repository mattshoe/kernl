plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
}

ext {
    set("ARTIFACT_ID", "Kernl.Annotations")
    set("PUBLICATION_NAME", "kernlAnnotations")
}

dependencies {
    testImplementation(kotlin("test"))
}