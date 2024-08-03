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
    implementation(project(":Kernl.Common"))
    implementation(libs.kotlinx.coroutines)
    testImplementation(kotlin("test"))
}