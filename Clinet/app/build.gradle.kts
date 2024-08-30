plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.clinet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.clinet"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.google.android.material:material:1.3.0")
    implementation(libs.games.activity)
    implementation(libs.mediarouter)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Image picker
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("androidx.activity:activity:1.7.0")

    // Bottom navigation bar
    implementation("com.google.android.material:material:1.4.0")

    // OkHttp for WebSocket
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
}



