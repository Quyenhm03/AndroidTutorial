plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-parcelize")
}

android {
    namespace = "com.eco.musicplayer.audioplayer.music"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.eco.musicplayer.audioplayer.music"
        minSdk = 24
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

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.airbnb.android:lottie:6.4.0")
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.android.billingclient:billing-ktx:8.0.0")
    implementation(project(":billing"))
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation("androidx.fragment:fragment-ktx:1.8.0")
    implementation("androidx.media:media:1.7.0")
}
