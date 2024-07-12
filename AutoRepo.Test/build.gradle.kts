plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
}

group = project.properties["group.id"].toString()
version = project.properties["version"].toString()

dependencies {
    ksp(project(":AutoRepo.Processor"))
    compileOnly(project(":AutoRepo.Annotations"))
    implementation(project(":AutoRepo.Runtime"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")

    testImplementation(kotlin("test"))
}