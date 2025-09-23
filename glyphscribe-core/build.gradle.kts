plugins {
    java
    `maven-publish`
}

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.36")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
