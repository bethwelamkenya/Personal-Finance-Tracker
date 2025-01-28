plugins {
    kotlin("jvm") version "1.9.21"
    application // Add this line to apply the application plugin
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(20) // Set your desired JDK version
}

// Configure the application plugin
application {
    mainClass.set("MainKt") // Entry point for the application
}

// Configure the JAR task
tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.example.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}