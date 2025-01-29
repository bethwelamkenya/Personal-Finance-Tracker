plugins {
    kotlin("jvm") version "1.9.21"
    application // Add this line to apply the application plugin
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.openjfx:javafx-controls:20") // JavaFX controls
    implementation("org.openjfx:javafx-fxml:20")
    implementation("mysql:mysql-connector-java:8.0.30")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(20) // Set your desired JDK version
}

// Configure the application plugin
application {
    mainClass.set("MainAppKt") // Entry point for the application
//    mainClass.set("MainKt") // Entry point for the application
}

javafx {
    version = "20"
    modules("javafx.controls", "javafx.fxml")
}

// Configure the JAR task
tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.example.MainAppKt"
//        attributes["Main-Class"] = "org.example.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaExec> {
    jvmArgs = listOf(
        "--module-path", System.getProperty("javafx.sdk.path") ?: "",
        "--add-modules", "javafx.controls,javafx.fxml"
    )
}