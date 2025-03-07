plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "xyz.ssupii.palmnotes.watch"
    compileSdk = 34

    defaultConfig {
        applicationId = "xyz.ssupii.palmnotes.watch"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            //TODO enable
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v6", "armeabi-v7a", "arm64-v8a", "x86")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.play.services.wearable)
    implementation(libs.wear.tooling.preview)
    implementation(libs.core.splashscreen)
    implementation(libs.appcompat)
    implementation(libs.wear)
    implementation(libs.core.ktx)
}