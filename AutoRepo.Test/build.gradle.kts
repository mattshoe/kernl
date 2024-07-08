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
//    ksp(project(":AutoRepo.Processor"))
    compileOnly(project(":AutoRepo.Annotations"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}