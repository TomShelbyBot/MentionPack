import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    maven
}

group = "me.theseems.tomshelby"
version = "0.1D"

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    implementation("com.github.TomShelbyBot:TomShelbyBot:0.8D")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.withType<Jar> {
    // Otherwise you'll get a "No main manifest attribute" error
    manifest {
        attributes["Main-Class"] = "com.example.MainKt"
    }

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
