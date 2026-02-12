plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
}

group = "dev.httpmarco.polocloud"
version = "3.0.0-pre.8-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.log4j.api)
    implementation(libs.gson)
}

kotlin {
    jvmToolchain(21)
}