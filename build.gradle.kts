plugins {
    id("java-library")
    id("com.vanniktech.maven.publish") version "0.36.0"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(platform("org.springframework.boot:spring-boot-dependencies:4.0+"))
    annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:4.0+"))

    api("org.jspecify:jspecify:1.0.0")

    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework.boot:spring-boot-starter-restclient")

    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation(platform("org.springframework.boot:spring-boot-dependencies:4.0+"))
    testImplementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Testcontainers 2.0.2
    testImplementation(platform("org.testcontainers:testcontainers-bom:2.0.2"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
}

tasks.withType<JavaCompile> {
    inputs.files(tasks.named("processResources"))
    options.compilerArgs.add("-parameters")
}

tasks.test {
    useJUnitPlatform()
}
