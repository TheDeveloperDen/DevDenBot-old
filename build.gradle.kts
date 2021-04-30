plugins {
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.serialization") version "1.4.30-M1"
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "me.bristermitten"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
    jcenter()
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io")
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.4.3")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")


    implementation("com.jagrosh:jda-utilities:3.0.5")
    implementation("net.dv8tion:JDA:4.2.1_262")
    implementation("com.github.MinnDevelopment:jda-reactor:b3968f8e4e")

    implementation("org.apache.commons:commons-text:1.9")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha1")
    implementation("io.github.microutils:kotlin-logging:1.8.3")

    implementation("io.github.classgraph:classgraph:4.8.90")
    implementation("com.google.inject:guice:5.0.1")
    implementation("dev.misfitlabs.kotlinguice4:kotlin-guice:1.4.1")
}


tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    application {
        // Define the main class for the application.
        mainClass.set("me.bristermitten.devdenbot.AppKt")
        applicationName = "DevDenBot"
    }

}