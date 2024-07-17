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
    implementation(libs.kotlinx.coroutines)

    testImplementation(kotlin("test"))
}