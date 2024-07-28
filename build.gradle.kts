import org.apache.tools.ant.taskdefs.Java

plugins {
    kotlin("jvm") version "2.0.0"
    id("maven-publish")
    signing
}

val GROUP_ID: String = project.properties["group.id"].toString()
val VERSION: String = project.properties["version"].toString()

group = GROUP_ID
version = VERSION

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }

    group = GROUP_ID
    version = VERSION

    plugins.withId("org.jetbrains.kotlin.jvm") {
        kotlin {
            jvmToolchain(19)
        }
    }

    tasks.withType<Test> {
        useJUnit()
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }

    plugins.withId("java") {
        java {
            withJavadocJar()
            withSourcesJar()
        }
    }

    afterEvaluate {
        (findProperty("PUBLICATION_NAME") as? String)?.let { publicationName ->
            val subArtifactId = findProperty("ARTIFACT_ID") as String
            plugins.withId("maven-publish") {
                publishing {
                    publications {
                        repositories {
                            mavenLocal()
                        }

                        create<MavenPublication>(publicationName) {
                            from(components["java"])
                            groupId = GROUP_ID
                            artifactId = subArtifactId
                            version = VERSION
                            pom {
                                name = "AutoRepo.Processor"
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
                                sign(publishing.publications[publicationName])
                            }
                        }
                    }
                }
            }

            tasks.register<Zip>("generateZip") {
                val publishTask = tasks.named(
                    "publish${publicationName.replaceFirstChar { it.uppercaseChar() }}PublicationToMavenLocalRepository",
                    PublishToMavenRepository::class.java
                )
                from(publishTask.map { it.repository.url })
                archiveFileName.set("${subArtifactId}_${VERSION}.zip")
            }
        }
    }
}