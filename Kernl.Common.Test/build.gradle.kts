plugins {
    kotlin("jvm")
}

group = "org.mattshoe.shoebox"
version = "0.0.1-beta"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Kernl.Common"))
    implementation(libs.kotlinx.coroutines.test)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}