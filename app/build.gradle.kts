import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val properties = Properties().apply { load(FileInputStream(rootProject.file("local.properties"))) }

android {
    namespace = "com.example.myimgsearch"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myimgsearch"
        minSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "KAKAOAPI", properties.getProperty("api_key"))
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.viewpager2:viewpager2:1.0.0")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson Converter
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OKHttp for 통신 로그
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    //뷰모델사용시필수
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    //viewModels사용할 경우
    implementation ("androidx.activity:activity-ktx:1.8.2")
    //activityVeiwModels사용할 경우
    implementation ("androidx.fragment:fragment-ktx:1.6.2")
}