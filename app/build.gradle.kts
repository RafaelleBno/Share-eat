plugins {
    alias(libs.plugins.android.application) // ton plugin Android
    id("com.google.gms.google-services") // plugin Firebase
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 35
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
}

dependencies {
    // 🔥 Librairies Android de base
    implementation("androidx.appcompat:appcompat:1.6.1") // Pour AppCompatActivity
    implementation("androidx.fragment:fragment:1.5.7") // Pour Fragment + getSupportFragmentManager()
    implementation("com.google.android.material:material:1.10.0") // Pour BottomNavigationView
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Pour ConstraintLayout
    implementation("androidx.activity:activity:1.7.2") // Pour ActivityCompat, etc.

    // 🔐 Firebase Auth
    implementation("com.google.firebase:firebase-auth:22.3.0")

    // ✅ Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
