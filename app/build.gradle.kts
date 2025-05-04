plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.tvbrowser"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tvbrowser"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.androidx.leanback)
    implementation(libs.glide)
    implementation ("com.google.android.material:material:1.12.0") // ✅ Correct
    }
