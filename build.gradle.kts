plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.suddenpeak.lab.postsplitter"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.opencsv:opencsv:5.7.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}
