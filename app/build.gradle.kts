plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.assignment.empower"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.assignment.empower"
        minSdk = 24
        targetSdk = 33
        versionCode = 7
        versionName = "7.0"

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
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Picasso
    implementation("com.squareup.picasso:picasso:2.71828")
    // Page View
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta01")
    // Glide
    implementation("com.github.bumptech.glide:glide:4.14.2")
    // Progress Bar
    implementation("com.mikhaellopez:circularprogressbar:3.1.0")
    // Lottie
    implementation("com.airbnb.android:lottie:6.4.0")
}