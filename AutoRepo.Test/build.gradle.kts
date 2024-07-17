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

    kspTest(project(":AutoRepo.Processor"))
    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}