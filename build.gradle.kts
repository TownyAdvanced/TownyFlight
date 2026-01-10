plugins {
    java
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.gmail.llmdlio"
version = "1.14.2"
description = "A flight plugin for Towny servers."

val cloudVersion = "2.0.0"
val cloudMinecraftVersion = "2.0.0-beta.14"
val townyVersion = "0.101.2.5"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.glaremasters.me/repository/towny/")
}

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    
    // Cloud Commands v2
    implementation("org.incendo:cloud-paper:$cloudMinecraftVersion")
    implementation("org.incendo:cloud-annotations:$cloudVersion")
    
    // Towny
    compileOnly("com.palmergames.bukkit.towny:towny:$townyVersion")
    
    // SiegeWar - exclude transitive dependencies since it's compileOnly
    compileOnly("com.github.TownyAdvanced:SiegeWar:2.0.0") {
        isTransitive = false
    }
    
    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.6")
    
    // Annotations
    compileOnly("org.jetbrains:annotations:24.0.1")
}

tasks {
    processResources {
        val props = mapOf(
            "version" to version,
            "bukkitAPIVersion" to "1.21"
        )
        inputs.properties(props)
        filesMatching(listOf("plugin.yml", "paper-plugin.yml")) {
            expand(props)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        
        relocate("org.incendo.cloud", "com.gmail.llmdlio.townyflight.cloud")
        relocate("io.leangen.geantyref", "com.gmail.llmdlio.townyflight.geantyref")
        
        minimize {
            exclude(dependency("org.incendo:.*"))
        }
    }

    build {
        dependsOn(shadowJar)
    }

    // Disable the default jar task - only use shadowJar
    jar {
        enabled = false
    }

    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}
