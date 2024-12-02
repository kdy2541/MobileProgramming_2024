plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mobile"
        minSdk = 28
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

    // Room 디비
    implementation("androidx.room:room-runtime:2.5.0")


    annotationProcessor("androidx.room:room-compiler:2.5.0")
    androidTestImplementation("androidx.room:room-testing:2.5.0") // Room 테스트용

    // MVVM (LiveData 및 ViewModel)
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.1")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    // MPAndroidChart (차트)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.8.0")

    // 테스트 라이브러리
    implementation(libs.ext.junit)
    implementation(libs.junit.junit)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}