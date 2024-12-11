// Top-level build file where you can add configuration options common to all sub-projects/modules.
/*
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}*/
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.google.services)
    }
}
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}