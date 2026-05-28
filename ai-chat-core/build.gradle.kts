plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    androidTarget()
    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(project(":framework"))
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

android {
    namespace = "com.zhangke.compose.chat.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
    }
}
