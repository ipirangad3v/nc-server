val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val koinVersion: String by project
val exposedVersion: String by project
val hikariVersion: String by project
val postgresVersion: String by project
val bcryptVersion: String by project
val commonsEmailVersion: String by project
val kotestVersion: String by project
val testContainerVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.21"
}

group = "digital.thon"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

tasks {
    create("stage").dependsOn("installDist")
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // Koin for Kotlin
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    // Exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    // Hikari
    implementation("com.zaxxer:HikariCP:$hikariVersion")

    // PostgreSQL
    implementation("org.postgresql:postgresql:$postgresVersion")

    // Password encryption
    implementation("org.mindrot:jbcrypt:$bcryptVersion")

    // For sending reset-password-mail
    implementation("org.apache.commons:commons-email:$commonsEmailVersion")
}
