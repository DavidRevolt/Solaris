import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.davidrevolt.core.network"
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 29
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // API KEYS FILE
        val apiKeys = Properties().apply {
            val localPropertiesFile = rootProject.file("apikeys.properties")
            if (!localPropertiesFile.exists()) { // Generate api keys file in not exists
                localPropertiesFile.createNewFile()
                setProperty("METEOSOURCE_API_KEY", "PUT_YOUR_API_KEY_HERE") // Add placeholder
                localPropertiesFile.outputStream().use { store(it, "API Keys") }
            }
            load(localPropertiesFile.inputStream())
        }

        val meteosourceApiKey = apiKeys.getProperty("METEOSOURCE_API_KEY") ?: ""
        buildConfigField(
            type = "String",
            name = "METEOSOURCE_API_KEY",
            value = "\"$meteosourceApiKey\""
        )

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
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.timber)

    implementation(libs.logging.interceptor)

    implementation(libs.retrofit)
    implementation(libs.retrofitGsonConverter)

    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.svg)
    implementation(libs.coil.compose.http)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}