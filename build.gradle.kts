plugins {
    // Usamos una versión de Google Services probada como estable
    id("com.google.gms.google-services") version "4.3.15" apply false

    // Usamos una versión de Android Application y Kotlin estables
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
}

// Bloque vacío (ya que los repositorios se definen en settings.gradle.kts)
buildscript {
    repositories {
        google()
        mavenCentral()
    }
}