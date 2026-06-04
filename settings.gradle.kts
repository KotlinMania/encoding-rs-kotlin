pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins { kotlin("multiplatform") version "2.4.0" }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "encoding-rs-kotlin"

val projectKotlinHome = rootDir.resolve(".kotlin").absolutePath
val projectKonanDataDir = rootDir.resolve("tmp/konan").absolutePath

System.setProperty("kotlin.user.home", projectKotlinHome)
System.setProperty("konan.data.dir", projectKonanDataDir)

gradle.beforeProject {
    extensions.extraProperties["kotlin.user.home"] = projectKotlinHome
    extensions.extraProperties["konan.data.dir"] = projectKonanDataDir
}
