plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
    alias(libs.plugins.dokka)
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

tasks.dokkaGfm {
    outputDirectory.set(layout.buildDirectory.get().asFile.resolve("dokka"))

    dokkaSourceSets {
        configureEach {
            displayName = ARTIFACT_ID
            includeNonPublic.set(false)
            skipEmptyPackages.set(true)
            reportUndocumented.set(true)
            jdkVersion.set(19)

            // Optional: Customize documentation for specific source sets
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(uri("https://github.com/mattshoe/autorepo/tree/main/AutoRepo.Runtime/src/main/kotlin").toURL())
                remoteLineSuffix.set("#L")
            }
        }
    }
}