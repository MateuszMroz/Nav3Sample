plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Pure Kotlin - tylko Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Serialization
    implementation(libs.kotlinx.serialization.core)
}
