plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
}

val GROUP_ID: String = project.properties["group.id"].toString()
val VERSION: String = project.properties["version"].toString()
val ARTIFACT_ID = "AutoRepo.Processor"
val PUBLICATION_NAME = "autoRepoProcessor"

group = GROUP_ID
version = VERSION

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":AutoRepo.Annotations"))

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

tasks.test {
    useJUnit()
}

kotlin {
    jvmToolchain(19)
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
                sign(publishing.publications[PUBLICATION_NAME])
            }
        }
    }
}

tasks.register<Zip>("generateZip") {
    val publishTask = tasks.named(
        "publish${PUBLICATION_NAME.replaceFirstChar { it.uppercaseChar() }}PublicationToMavenLocalRepository",
        PublishToMavenRepository::class.java
    )
    from(publishTask.map { it.repository.url })
    archiveFileName.set("autorepo-processor_${VERSION}.zip")
}