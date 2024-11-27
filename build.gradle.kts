plugins {
    kotlin("jvm") version "2.1.0-RC2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.multipaper"
version = "1.0-SNAPSHOT"
description = "Extra commands for MultiPaper"

repositories {
    mavenLocal()
    maven {
        url = uri("https://maven.pkg.github.com/bystepii/MultiPaper")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USER_REF")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN_REF")
        }
    }
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://hub.spigotmc.org/nexus/content/groups/public/") {
        name = "spigotmc-repo"
    }
    maven("https://repo.aikar.co/content/groups/aikar/") {
        name = "aikar"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    mavenCentral()
}

dependencies {
    compileOnly("puregero.multipaper:multipaper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("dev.cubxity.plugins:unifiedmetrics-api:0.3.8")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    //implementation("com.github.puregero:multilib:1.2.4")
}

val targetJavaVersion = 17
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.jar {
    enabled = false
}

tasks.assemble {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf(
        "version" to version,
        "name" to project.name,
        "description" to project.description
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("**/plugin.yml") {
        expand(props)
    }
}

tasks.register("printProjectName") {
    doLast {
        println(project.name)
    }
}

tasks.register("release") {
    dependsOn("build")

    doLast() {
        if (!version.toString().endsWith("-SNAPSHOT")) {
            // Rename final JAR to trim off version information
            tasks.shadowJar {
                archiveFile.get().asFile
                    .renameTo(
                        File(layout.buildDirectory.get().toString() + File.separator + "libs" + File.separator
                                + rootProject.name + ".jar")
                    )
            }
        }
    }
}
