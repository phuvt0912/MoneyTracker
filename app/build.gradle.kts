plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Thêm plugin KSP
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.accountingapp"
    compileSdk = 36 // Bạn đã có sẵn, chỉ để tham khảo

    defaultConfig {
        applicationId = "com.example.accountingapp"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // === CÁC DEPENDENCY CỦA ROOM ===
    // Thư viện runtime chính của Room
    implementation(libs.androidx.room.runtime)
    // Hỗ trợ Kotlin Extensions và Coroutines cho Room. [2]
    implementation(libs.androidx.room.ktx)
    // Annotation processor cho Room, sử dụng ksp. [2, 9]
    ksp(libs.androidx.room.compiler)
    // ================================
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3") // Thêm dòng này

    implementation("com.github.bumptech.glide:glide:4.16.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
