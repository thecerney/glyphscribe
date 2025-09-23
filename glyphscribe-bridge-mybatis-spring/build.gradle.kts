plugins {
    java
    `maven-publish`
}

dependencies {
    implementation(project(":glyphscribe-core"))
    implementation("org.springframework:spring-context:6.1.5")
    implementation("org.mybatis:mybatis:3.5.15")
//    implementation("org.mybatis:mybatis-spring-boot-starter:3.0.3")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-autoconfigure:2.3.1")
    implementation("org.slf4j:slf4j-api:1.7.36")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
