plugins {
    java
    id("net.fabricmc.fabric-loom")
}

val modVersion = project.property("modVersion").toString()
val mavenGroup = project.property("mavenGroup").toString()
val modId = project.property("modId").toString()

group = mavenGroup
version = modVersion

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
}

sourceSets.main {
    java.srcDir("../fabric-common/src/main/java")
    resources.srcDir("../fabric-common/src/main/resources")
}

dependencies {
    minecraft("com.mojang:minecraft:26.1.2")
    implementation("net.fabricmc:fabric-loader:0.19.3")
    implementation("net.fabricmc.fabric-api:fabric-api:0.155.2+26.1.2")
    compileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:26.1.819")
    compileOnly("me.shedaniel.cloth:cloth-config-fabric:26.1.154")
    compileOnly("dev.architectury:architectury-fabric:20.0.6")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(25)
    }
    processResources {
        filteringCharset = "UTF-8"
        val props = mapOf(
            "mod_id" to modId,
            "mod_version" to modVersion,
            "minecraft_dependency" to "=26.1.2",
            "loader_dependency" to ">=0.19.3",
            "java_dependency" to ">=25",
            "rei_dependency" to ">=26.1.819"
        )
        inputs.properties(props)
        filesMatching("fabric.mod.json") { expand(props) }
    }
    jar {
        archiveBaseName.set("craftengine-rei-bridge-fabric-26.1.2")
    }
}
