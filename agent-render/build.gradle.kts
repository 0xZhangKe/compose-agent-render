plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.compose.compiler)
}

kotlin {
    androidTarget()
    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(project(":agent-render-core"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
        }
    }
}

android {
    namespace = "com.zhangke.compose.agent.render"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
    }
}
