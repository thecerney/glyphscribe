plugins {
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    java
    application
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":glyphscribe-core"))
    implementation(project(":glyphscribe-bridge-mybatis-spring"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
    runtimeOnly("com.h2database:h2")

    // MDC 로그 패턴용
    implementation("org.springframework.boot:spring-boot-starter-logging")
}

application {
    mainClass.set("kr.cerney.hobby.glyphscribe.examples.Main")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}