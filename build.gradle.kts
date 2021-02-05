plugins {
    java
    id("com.github.johnrengelman.shadow").version("6.1.0")
}

group = "me.moros"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("me.moros", "atlas-core", "1.0.0-SNAPSHOT")
    compileOnly("me.moros", "bending", "1.0.0-SNAPSHOT")
    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.5-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set(rootProject.name)
        dependencies {
            relocate("me.moros.example", "me.moros.ares.example")
        }
    }
    build {
        dependsOn(shadowJar)
    }
    withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    named<Copy>("processResources") {
        filesMatching("plugin.yml") {
            expand("pluginVersion" to project.version)
        }
    }
}
