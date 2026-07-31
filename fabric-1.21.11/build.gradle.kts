plugins {
    java
    id("net.fabricmc.fabric-loom-remap")
}

val modVersion = project.property("modVersion").toString()
val mavenGroup = project.property("mavenGroup").toString()
val modId = project.property("modId").toString()

group = mavenGroup
version = modVersion

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}

sourceSets.main {
    java.srcDir("../fabric-common/src/main/java")
    resources.srcDir("../fabric-common/src/main/resources")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.11")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.18.4")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.141.6+1.21.11")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:21.11.816")
    modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:21.11.151")
    modCompileOnly("dev.architectury:architectury-fabric:19.0.1")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
    processResources {
        filteringCharset = "UTF-8"
        val props = mapOf(
            "mod_id" to modId,
            "mod_version" to modVersion,
            "minecraft_dependency" to "=1.21.11",
            "loader_dependency" to ">=0.18.4",
            "java_dependency" to ">=21",
            "rei_dependency" to ">=21.11.816"
        )
        inputs.properties(props)
        filesMatching("fabric.mod.json") { expand(props) }
    }
    remapJar {
        archiveBaseName.set("craftengine-rei-bridge-fabric-1.21.11")
    }
}
