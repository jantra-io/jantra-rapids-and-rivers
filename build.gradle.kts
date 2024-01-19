import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


val jvmTarget = "17"

val ktorVersion = "2.3.0"
val kafkaVersion = "3.4.0"
val micrometerRegistryPrometheusVersion = "1.10.6"
val junitJupiterVersion = "5.9.3"
val jacksonVersion = "2.15.0"
val logbackClassicVersion = "1.4.7"
val logbackEncoderVersion = "7.3"
val awaitilityVersion = "4.2.0"
val kafkaTestcontainerVersion = "1.18.0"
val lettuceVersion = "6.2.4.RELEASE"
val testcontainersRedisJunitVersion = "1.6.4"
val testcontainersVersion = "1.18.0"
val h2Version = "2.2.224"

group = "com.github.navikt"
version = properties["version"] ?: "local-build"

plugins {
    kotlin("jvm") version "1.8.21"
    id("java")
    id("maven-publish")
}

dependencies {
    api("ch.qos.logback:logback-classic:$logbackClassicVersion")
    api("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

    api("io.ktor:ktor-server-cio:$ktorVersion")

    api("org.apache.kafka:kafka-clients:$kafkaVersion")

    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    api("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    api("io.micrometer:micrometer-registry-prometheus:$micrometerRegistryPrometheusVersion")
    api("io.lettuce:lettuce-core:$lettuceVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")

    testImplementation("org.testcontainers:kafka:$kafkaTestcontainerVersion")
    testImplementation("org.awaitility:awaitility:$awaitilityVersion")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit:$testcontainersRedisJunitVersion")
    testImplementation("org.testcontainers:kafka:$testcontainersVersion")
    testImplementation("com.h2database:h2:$h2Version")

    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("org.jetbrains.exposed:exposed-core:0.43.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.43.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.43.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    implementation("org.jetbrains.exposed:exposed-json:0.43.0")
    implementation("org.flywaydb:flyway-core:9.16.3")
    implementation("org.testcontainers:postgresql:1.18.0")
    runtimeOnly("org.postgresql:postgresql:42.6.0")
}

java {
    sourceCompatibility = JavaVersion.toVersion(jvmTarget)
    targetCompatibility = JavaVersion.toVersion(jvmTarget)

    withSourcesJar()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = jvmTarget
}

tasks.named<KotlinCompile>("compileTestKotlin") {
    kotlinOptions.jvmTarget = jvmTarget
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("skipped", "failed")
        showExceptions = true
        showStackTraces = true
        showCauses = true
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "8.1.1"
}

repositories {
    maven("https://packages.confluent.io/maven/")
    mavenCentral()
}

val githubUser: String? by project
val githubPassword: String? by project

publishing {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/navikt/rapids-and-rivers")
            credentials {
                username = githubUser
                password = githubPassword
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {

            pom {
                name.set("rapids-rivers")
                description.set("Rapids and Rivers")
                url.set("https://github.com/navikt/rapids-and-rivers")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/navikt/rapids-and-rivers.git")
                    developerConnection.set("scm:git:https://github.com/navikt/rapids-and-rivers.git")
                    url.set("https://github.com/navikt/rapids-and-rivers")
                }
            }
            from(components["java"])
        }
    }
}
