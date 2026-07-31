pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.shedaniel.me/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "CraftEngine-REI-Bridge"

include("fabric-1.21.11")
include("fabric-26.1.2")
include("fabric-26.2")
include("server")
