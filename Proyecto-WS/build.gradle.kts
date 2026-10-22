plugins {
    java
    application
}

group = "cl.peluditos"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories { mavenCentral() }

val jersey = "3.1.9"

dependencies {
    implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:$jersey")
    implementation("org.glassfish.jersey.inject:jersey-hk2:$jersey")
    implementation("org.glassfish.jersey.media:jersey-media-json-binding:$jersey")
}

application { mainClass.set("cl.peluditos.ws.Main") }
