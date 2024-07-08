plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
}

val GROUP_ID: String = project.properties["group.id"].toString()
val VERSION: String = project.properties["version"].toString()
val ARTIFACT_ID = "AutoRepo.Annotations"
val PUBLICATION_NAME = "autoRepoAnnotations"

group = GROUP_ID
version = VERSION

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")

    testImplementation(kotlin("test"))
}

publishing {
    publications {
        repositories {
            mavenLocal()
        }

        create<MavenPublication>(PUBLICATION_NAME) {
            from(components["java"])
            groupId = GROUP_ID
            artifactId = ARTIFACT_ID
            version = VERSION
            pom {
                name = "AutoRepo.Annotations"
                description = "AutoRepo: A Kotlin Symbol Processing (KSP) library for automatic repository generation."
                url = "https://github.com/mattshoe/autorepo"
                properties = mapOf(
                    "myProp" to "value"
                )
                packaging = "aar"
                inceptionYear = "2024"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "mattshoe"
                        name = "Matthew Shoemaker"
                        email = "mattshoe81@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git@github.com:mattshoe/autorepo.git"
                    developerConnection = "scm:git:git@github.com:mattshoe/autorepo.git"
                    url = "https://github.com/mattshoe/autorepo"
                }
            }
        }


        signing {
            val signingKey = providers
                .environmentVariable("GPG_SIGNING_KEY")
                .forUseAtConfigurationTime()
            val signingPassphrase = providers
                .environmentVariable("GPG_SIGNING_PASSPHRASE")
                .forUseAtConfigurationTime()
            if (signingKey.isPresent && signingPassphrase.isPresent) {
                useInMemoryPgpKeys(signingKey.get(), signingPassphrase.get())
                sign(publishing.publications[PUBLICATION_NAME])
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Zip>("generateZip") {
    val publishTask = tasks.named(
        "publish${PUBLICATION_NAME.replaceFirstChar { it.uppercaseChar() }}PublicationToMavenLocalRepository",
        PublishToMavenRepository::class.java
    )
    from(publishTask.map { it.repository.url })
    archiveFileName.set("AutoRepo-annotations_${VERSION}.zip")
}