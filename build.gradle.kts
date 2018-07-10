import groovy.lang.GroovyObject
import org.gradle.api.tasks.util.PatternSet
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.include
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.contracts.model.structure.UNKNOWN_COMPUTATION.type
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.dsl.SpringBootExtension
import org.springframework.cloud.contract.verifier.config.TestMode
import org.springframework.cloud.contract.verifier.plugin.ContractVerifierExtension
import org.gradle.internal.os.OperatingSystem
import org.springframework.boot.gradle.tasks.run.BootRun

val springBootVersion = "2.0.3.RELEASE"
val reactorVersion = "Bismuth-SR10"
val springCloudVersion by extra { "Finchley.RELEASE" }

group = "com.github.jntakpe"
version = "0.0.1-SNAPSHOT"

buildscript {
    repositories {
        jcenter()
        maven("https://repo.spring.io/snapshot")
        maven("https://repo.spring.io/milestone")
    }
    dependencies {
        val springBootVersion = "2.0.3.RELEASE"
        val springCloudContractVersion = "2.0.0.RELEASE"
        classpath("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
        classpath("org.springframework.cloud:spring-cloud-contract-gradle-plugin:$springCloudContractVersion")
    }
}

plugins {
    val kotlinVersion = "1.2.51"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("io.spring.dependency-management") version "1.0.5.RELEASE"
}

apply {
    plugin("org.springframework.boot")
    plugin("spring-cloud-contract")
}

repositories {
    jcenter()
    maven("https://repo.spring.io/snapshot")
    maven("https://repo.spring.io/milestone")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
        mavenBom("io.projectreactor:reactor-bom:${reactorVersion}")
    }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))
    compile("org.springframework.cloud:spring-cloud-starter-sleuth")
    compile("io.opentracing.brave:brave-opentracing")
    compile("org.springframework.boot:spring-boot-starter-webflux")
    compile("org.springframework.kafka:spring-kafka")
    compile("io.projectreactor.kafka:reactor-kafka")
    testCompile("org.springframework.cloud:spring-cloud-starter-contract-stub-runner") {
        exclude(module = "spring-cloud-stream")
        exclude(module = "spring-cloud-stream-test-support")
    }
    testCompile("io.projectreactor:reactor-test")
    testCompile("org.junit.jupiter:junit-jupiter-api")
    testCompile("org.springframework.boot:spring-boot-starter-test") { exclude(module = "junit") }
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testCompile("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks {
    val build by tasks
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=compatibility")
        }
    }
    val test = withType<Test> { useJUnitPlatform() }
}

configure<SpringBootExtension> { buildInfo { properties { time = null } } }