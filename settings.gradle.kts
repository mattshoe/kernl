plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "Kernl"
include("Kernl.Annotations")
include("Kernl.Processor")
include("Kernl.Consumer")
include("Kernl.Runtime")
include("Kernl.Common")
include("Kernl.Common.Test")
