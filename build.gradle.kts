plugins {
    kotlin("jvm") version "1.4.20"
    kotlin("plugin.serialization") version "1.4.20"
    id("idea")
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "me.bristermitten"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.4.3")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")


    implementation("com.jagrosh:jda-utilities:3.0.5")
    implementation("net.dv8tion:JDA:4.2.1_262")
    implementation("club.minnced:jda-reactor:1.1.1")

    implementation("org.apache.commons:commons-text:1.9")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha1")
    implementation("io.github.microutils:kotlin-logging:1.8.3")

    implementation("io.github.classgraph:classgraph:4.8.90")
    implementation("com.google.inject:guice:4.2.3")
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
    }

}
