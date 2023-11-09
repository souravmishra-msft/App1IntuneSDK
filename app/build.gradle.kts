plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("C:\\Users\\Sourav\\SigningKeys\\app1_intuneSDK_app.jks")
            storePassword = "123456789"
            keyAlias = "mamKey"
            keyPassword = "123456789"
        }
    }
    namespace = "com.example.app1intunesdk"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app1intunesdk"
        minSdk = 29
        targetSdk = 33
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

    buildFeatures {
        viewBinding = true
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
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(files("E:\\Intune_Training\\Session_4\\ms-intune-app-sdk-android-master\\Microsoft.Intune.MAM.SDK.aar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.android.volley:volley:1.2.1")
    implementation("com.microsoft.identity.client:msal:4.7.0") {
        exclude(group = "com.microsoft.device.display")
    }
    implementation ("com.google.code.gson:gson:2.8.8")
}