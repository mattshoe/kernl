plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
}

val GROUP_ID: String = project.properties["group.id"].toString()
val VERSION: String = project.properties["version"].toString()

group = GROUP_ID
version = VERSION

repositories {
    mavenCentral()
    google()
}

dependencies {
    ksp(project(":AutoRepo.Processor"))
    compileOnly(project(":AutoRepo.Annotations"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}