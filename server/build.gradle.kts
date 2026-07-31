plugins {
    java
}

val modVersion = project.property("modVersion").toString()
val mavenGroup = project.property("mavenGroup").toString()

group = mavenGroup
version = modVersion

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

val craftEngineJars = fileTree("libs") {
    include("craft-engine-paper-plugin-*.jar")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly(craftEngineJars)
}

val verifyCraftEngineJar by tasks.registering {
    doLast {
        check(!craftEngineJars.isEmpty) {
            "Missing licensed CraftEngine jar. Put craft-engine-paper-plugin-*.jar in server/libs/."
        }
    }
}

tasks {
    compileJava {
        dependsOn(verifyCraftEngineJar)
        options.encoding = "UTF-8"
        options.release.set(21)
    }
    processResources {
        filteringCharset = "UTF-8"
        inputs.property("version", project.version)
        filesMatching("paper-plugin.yml") {
            expand("version" to project.version)
        }
    }
    jar {
        archiveBaseName.set("craftengine-rei-bridge-server")
    }
}
