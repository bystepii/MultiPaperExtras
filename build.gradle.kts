plugins {
    kotlin("jvm") version "2.1.0-RC2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.multipaper"
version = "1.0-SNAPSHOT"
description = "Extra commands for MultiPaper"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.clojars.org/") {
        name = "clojars"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.github.puregero:multilib:1.2.4")
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
