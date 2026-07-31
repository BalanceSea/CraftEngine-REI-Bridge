plugins {
    base
    id("net.fabricmc.fabric-loom") version "1.17.13" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.17.13" apply false
}

group = "net.mountainseal.cereibridge"
version = "1.0.0"

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.shedaniel.me/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

tasks.register("buildFabricAll") {
    group = "build"
    description = "Builds all supported Fabric client artifacts."
    dependsOn(
        ":fabric-1.21.11:build",
        ":fabric-26.1.2:build",
        ":fabric-26.2:build"
    )
}
