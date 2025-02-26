pluginManagement {
    repositories {
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "Kernl"
include("Kernl.Annotations")
include("Kernl.Processor")
include("Kernl.Consumer")
include("Kernl.Runtime")
include("Kernl.Common")
include("Kernl.Common.Test")
include(":Kernl.TestApp")
