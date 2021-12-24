plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    application
}

group = "net.developerden"


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

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.2-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.2-native-mt")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.255-SNAPSHOT")


    implementation("com.jagrosh:jda-utilities:3.0.5")
    implementation("net.dv8tion:JDA:4.4.0_351")
    implementation("com.github.MinnDevelopment:jda-reactor:1.5.0")

    implementation("org.apache.commons:commons-text:1.9")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha5")
    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-cio:1.6.7")
    implementation("io.ktor:ktor-client-serialization:1.6.7")

    implementation("ch.qos.logback:logback-core:1.3.0-alpha10")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha10")

    implementation("io.github.microutils:kotlin-logging:2.1.20")

    implementation("io.github.classgraph:classgraph:4.8.137")
    implementation("com.google.inject:guice:5.0.1")
    implementation("dev.misfitlabs.kotlinguice4:kotlin-guice:1.5.0")

    implementation("info.debatty:java-string-similarity:2.0.0")

    implementation("io.sentry:sentry:5.4.3")

    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("org.jetbrains.exposed", "exposed-core", "0.31.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.31.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.31.1")
    implementation("org.jetbrains.exposed", "exposed-java-time", "0.31.1")
    implementation("mysql:mysql-connector-java:8.0.27")
    implementation("com.h2database:h2:2.0.202")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.6.255-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
}

application {
    // Define the main class for the application.
    mainClass.set("net.developerden.devdenbot.AppKt")
    applicationName = "DevDenBot"
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "16"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "16"
    }

    processResources {
        inputs.property("version", project.version)
        filesMatching("version.txt") { expand("version" to project.version) }
    }

    test {
        useJUnitPlatform()
    }

    // gradle shut up thank you
    compileJava { options.release.set(16) }
    compileTestJava { options.release.set(16) }
}
