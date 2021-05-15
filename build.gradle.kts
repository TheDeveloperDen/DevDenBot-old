plugins {
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "me.bristermitten"
version = "1.5.3"


repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io")
    maven("https://maven.scijava.org/content/groups/public/")
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.4.3")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")


    implementation("com.jagrosh:jda-utilities:3.0.5")
    implementation("net.dv8tion:JDA:4.2.1_262")
    implementation("com.github.MinnDevelopment:jda-reactor:b3968f8e4e")

    implementation("org.apache.commons:commons-text:1.9")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    implementation("io.ktor:ktor-client-core:1.5.4")
    implementation("io.ktor:ktor-client-cio:1.5.4")
    implementation("io.ktor:ktor-client-serialization:1.5.4")

    implementation("ch.qos.logback:logback-core:1.3.0-alpha5")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")

    implementation("io.github.microutils:kotlin-logging:1.8.3")

    implementation("io.github.classgraph:classgraph:4.8.90")
    implementation("com.google.inject:guice:5.0.1")
    implementation("dev.misfitlabs.kotlinguice4:kotlin-guice:1.4.1")

    implementation("info.debatty:java-string-similarity:2.0.0")

    implementation("io.sentry:sentry:4.3.0")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")

    testImplementation("org.mockito.kotlin:mockito-kotlin:3.1.0")
}


tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    test {
        useJUnit()
    }

    application {
        // Define the main class for the application.
        mainClass.set("me.bristermitten.devdenbot.AppKt")
        applicationName = "DevDenBot"
    }

    shadowJar {
        isZip64 = true
    }

    jar {
        enabled = false
    }

    test {
        useJUnitPlatform()
    }

    processResources {
        from("${project.rootDir}/src/main/resources/version.txt") {
            expand("version" to project.version)
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}
