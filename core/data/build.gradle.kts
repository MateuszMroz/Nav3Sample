plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.mudita.core.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 28
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(libs.androidx.core.ktx)
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)
}
