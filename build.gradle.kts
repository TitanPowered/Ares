plugins {
    java
    id("com.github.johnrengelman.shadow").version("7.1.2")
}

group = "me.moros"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    if (!isSnapshot()) {
        withJavadocJar()
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("me.moros", "bending-api", "2.0.0-SNAPSHOT")
    compileOnly("me.moros", "gaia-core", "1.8.0-SNAPSHOT")
    compileOnly("com.sk89q.worldedit", "worldedit-core", "7.2.6")
    implementation("cloud.commandframework","cloud-paper", "1.7.0")
    implementation("cloud.commandframework","cloud-minecraft-extras", "1.7.0") {
        exclude(group = "net.kyori")
    }
    implementation("org.spongepowered", "configurate-hocon", "4.1.2")
    compileOnly("org.checkerframework", "checker-qual", "3.21.3")
    compileOnly("io.papermc.paper", "paper-api", "1.18.2-R0.1-SNAPSHOT")
}

configurations.implementation {
    exclude(module = "error_prone_annotations")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set(project.name)
        dependencies {
            relocate("cloud.commandframework", "me.moros.ares.internal.cf")
            relocate("com.typesafe", "me.moros.ares.internal.typesafe")
            relocate("io.leangen", "me.moros.ares.internal.leangen")
            relocate("org.spongepowered.configurate", "me.moros.ares.internal.configurate")
        }
        //minimize()
    }
    build {
        dependsOn(shadowJar)
    }
    withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
        options.encoding = "UTF-8"
    }
    named<Copy>("processResources") {
        filesMatching("plugin.yml") {
            expand("pluginVersion" to project.version)
        }
        from("LICENSE") {
            rename { "${project.name.toUpperCase()}_${it}"}
        }
    }
}

fun isSnapshot() = project.version.toString().endsWith("-SNAPSHOT")
