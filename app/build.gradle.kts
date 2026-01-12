import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.coffeecafe"
    compileSdk = 36
    
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.coffeecafe"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Load credentials from local.properties
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            FileInputStream(localPropertiesFile).use { properties.load(it) }
        }
        
        // Set BuildConfig fields
        buildConfigField("String", "SUPABASE_URL", "\"${properties.getProperty("supabase.url") ?: ""}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${properties.getProperty("supabase.key") ?: ""}\"")
        buildConfigField("String", "PAYSTACK_KEY", "\"${properties.getProperty("paystack.key") ?: ""}\"")
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
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    
    // Room Database
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    
    // Gson for JSON
    implementation(libs.gson)
    
    // No Paystack SDK - using direct API integration instead
    
    // OkHttp for networking
    implementation(libs.okhttp)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}