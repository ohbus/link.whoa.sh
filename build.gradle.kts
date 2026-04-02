plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.2.21"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    id("jacoco")
    id("org.sonarqube") version "7.2.1.6560"
    id("com.github.node-gradle.node") version "7.0.1"
}

node {
    version.set("22.14.0") // LTS version or your preferred node version
    npmVersion.set("10.2.4")
    download.set(true)
    workDir.set(file("${project.projectDir}/.gradle/nodejs"))
    nodeProjectDir.set(file("${project.projectDir}/ui"))
}

val buildFrontend by tasks.registering(com.github.gradle.node.npm.task.NpmTask::class) {
    dependsOn(tasks.npmInstall)
    args.set(listOf("run", "build"))
    inputs.dir(file("${project.projectDir}/ui/src"))
    inputs.file(file("${project.projectDir}/ui/package.json"))
    inputs.file(file("${project.projectDir}/ui/package-lock.json"))
    outputs.dir(file("${project.projectDir}/ui/dist/ui/browser"))
}

tasks.processResources {
    dependsOn(buildFrontend)
    from("${project.projectDir}/ui/dist/ui/browser") {
        into("static")
    }
}

group = "com.subhrodip.oss"
version = "0.0.1-SNAPSHOT"
description = "URL shortner for whoa.sh"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.5")

    implementation("tools.jackson.module:jackson-module-kotlin")

    compileOnly("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")

    // Enterprise E2E Testing
    testImplementation("org.testcontainers:postgresql:1.19.7")
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")
    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    testImplementation("io.rest-assured:kotlin-extensions:5.4.0")
    testImplementation("io.rest-assured:spring-mock-mvc:5.4.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

ktlint {
    version.set("1.8.0")
    debug.set(true)
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    coloredOutput.set(true)
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        csv.required = false
        html.required = false
    }
}

sonar {
    properties {
        property("sonar.projectKey", "ohbus_link.whoa.sh")
        property("sonar.organization", "ohbus")
    }
}
