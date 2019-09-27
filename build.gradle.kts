plugins {
    application
    kotlin("jvm") version "1.3.50"
}

group = "com.capybala"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.50")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.50")
//    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.postgresql:postgresql:42.2.8")
    // sql2o
    implementation("org.sql2o:sql2o:1.6.0")
    implementation("org.sql2o.extensions:sql2o-postgres:1.6.0")
    // JDBI
    implementation(platform("org.jdbi:jdbi3-bom:3.10.1"))
    implementation("org.jdbi:jdbi3-core")
    implementation("org.jdbi:jdbi3-kotlin")
    implementation("org.jdbi:jdbi3-postgres")
    // Apache Commons DbUtils
    implementation("commons-dbutils:commons-dbutils:1.7")
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

application {
    mainClassName = "com.capybala.ApplicationKt"
}
