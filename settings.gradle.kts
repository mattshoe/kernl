plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "AutoRepo"
include("AutoRepo.Annotations")
include("AutoRepo.Processor")
include("AutoRepo.Test")
include("AutoRepo.Runtime")
