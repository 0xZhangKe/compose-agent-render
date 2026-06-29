import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.compose.compiler)
}

val generatedLlmConfigsDir = layout.buildDirectory.dir("generated/source/llmConfigs/commonMain/kotlin")

val generateLLMConfigs by tasks.registering {
    val localPropertiesFile = rootProject.layout.projectDirectory.file("local.properties")

    inputs.file(localPropertiesFile)
        .withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.dir(generatedLlmConfigsDir)

    doLast {
        val properties = Properties()
        localPropertiesFile.asFile.inputStream().use(properties::load)
        val openRouter = properties.getProperty("OPEN_ROUTER")?.trim().orEmpty()
        require(openRouter.isNotEmpty()) {
            "OPEN_ROUTER is missing in local.properties."
        }

        val outputFile = generatedLlmConfigsDir.get()
            .file("com/zhangke/compose/chat/demo/agent/LLMConfigs.kt")
            .asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
            package com.zhangke.compose.chat.demo.agent

            internal object LLMConfigs {
                const val openRouter: String = ${openRouter.kotlinLiteral()}
            }
            """.trimIndent() + "\n",
        )
    }
}

kotlin {
    androidTarget()
    jvm("desktop")

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeAIChatDemo"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(generateLLMConfigs)
            dependencies {
                implementation(project(":agent-render-core"))
                implementation(project(":agent-render"))
                implementation(libs.compose.runtime)
                implementation(libs.compose.ui)
                implementation(libs.compose.foundation)
                implementation(libs.jetbrains.material3)
                implementation(libs.koog.agents)
                implementation(libs.koog.openai.client)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
        getByName("desktopMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

android {
    namespace = "com.zhangke.compose.chat.demo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.zhangke.compose.chat.demo"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }
}

compose.desktop {
    application {
        mainClass = "com.zhangke.compose.chat.demo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ComposeAIChat"
            packageVersion = "1.0.0"
        }
    }
}

val desktopMainCompilation = kotlin.targets.getByName("desktop").compilations.getByName("main")

tasks.register<JavaExec>("runDemoAgent") {
    group = "application"
    description = "Runs the minimal Koog demo agent with DEMO_AGENT_PROMPT as optional input."
    dependsOn("desktopMainClasses")
    mainClass.set("com.zhangke.compose.chat.demo.agent.DemoAgentRunnerKt")
    classpath = desktopMainCompilation.output.allOutputs + (desktopMainCompilation.runtimeDependencyFiles ?: files())
}

fun String.kotlinLiteral(): String {
    return buildString {
        append('"')
        for (char in this@kotlinLiteral) {
            append(
                when (char) {
                    '\\' -> "\\\\"
                    '"' -> "\\\""
                    '\n' -> "\\n"
                    '\r' -> "\\r"
                    '\t' -> "\\t"
                    else -> char.toString()
                },
            )
        }
        append('"')
    }
}
