plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.mudita.compose.order"
    compileSdk = 36

    defaultConfig {
        minSdk = 28
    }

    kotlin {
        jvmToolchain(17)
    }
    
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":features:order"))
    implementation(project(":core:domain"))
    implementation(project(":compose:navigation"))
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Koin - updated to 4.x
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    
    debugImplementation(libs.androidx.compose.ui.tooling)
}
