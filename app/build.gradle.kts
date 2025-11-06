plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // CRÍTICO: Aplica el plugin de Google Services para Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.evaluacion2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.evaluacion2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // DEPENDENCIAS DE FIREBASE PARA AUTENTICACIÓN Y FIRESTORE
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    // CRÍTICO: Nueva dependencia para la base de datos Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Dependencias de interfaz y AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}