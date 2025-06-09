plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
}

jacoco {
	toolVersion = "0.8.10"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

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
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.fasterxml.jackson.core:jackson-databind")
	testImplementation("org.mockito:mockito-core")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("jakarta.validation:jakarta.validation-api:3.0.2")
	implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}

tasks.test {
	testLogging {
		showStandardStreams = true
		events("passed", "skipped", "failed")
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT
	}
}