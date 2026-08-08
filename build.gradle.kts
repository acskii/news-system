plugins {
	java
	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7" apply true
}

group = "com.acskii"
version = "0.0.1-SNAPSHOT"

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	environment = System.getenv()
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}