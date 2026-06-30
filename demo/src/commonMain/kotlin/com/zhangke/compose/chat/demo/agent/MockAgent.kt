package com.zhangke.compose.chat.demo.agent

import ai.koog.prompt.streaming.StreamFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockAgent(
    private val frameDelayMillis: Long = 300L,
) {

    fun stream(message: String): Flow<StreamFrame> {
        return streamAllFrames(message)
    }

    fun streamAllFrames(message: String = "Show a mock Koog stream."): Flow<StreamFrame> {
        val shellCommand = """
            set -e
            backup=${'$'}(mktemp)
            if [ -f local.properties ]; then cp local.properties "${'$'}backup"; else : > "${'$'}backup"; fi
            trap 'cp "${'$'}backup" local.properties; rm -f "${'$'}backup"' EXIT
            ./gradlew :agent-render:compileKotlinDesktop :agent-render:compileDebugKotlinAndroid :demo:compileKotlinDesktop :demo:compileDebugKotlinAndroid --stacktrace
        """.trimIndent()
        val searchArguments = """
            {
              "query": ${message.jsonLiteral()},
              "paths": [
                "agent-render/src/commonMain/kotlin",
                "agent-render-koog/src/commonMain/kotlin",
                "demo/src/commonMain/kotlin"
              ],
              "include": ["*.kt", "*.kts"],
              "contextLines": 4,
              "caseSensitive": false
            }
        """.trimIndent()
        val readArguments = """
            {
              "files": [
                "agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/AgentToolCall.kt",
                "agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/theme/AgentColorScheme.kt",
                "agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/theme/AgentTypography.kt",
                "agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/theme/AgentIcons.kt"
              ],
              "reason": "Inspect theme-driven tool call rendering before changing UI behavior."
            }
        """.trimIndent()
        val shellOutput = """
            > Task :agent-render:compileKotlinDesktop
            > Task :agent-render:compileDebugKotlinAndroid
            > Task :agent-render-koog:compileKotlinDesktop
            > Task :agent-render-koog:compileDebugKotlinAndroid
            > Task :demo:compileKotlinDesktop
            > Task :demo:compileDebugKotlinAndroid

            BUILD SUCCESSFUL in 7s
            61 actionable tasks: 36 executed, 4 from cache, 21 up-to-date
        """.trimIndent()
        val allText = "Mock Koog stream finished. I inspected the renderer, searched the theme and icon providers, read the relevant files, and ran a long verification command. The tool calls below are intentionally long so the collapsed row truncates while the expanded log remains scrollable."
        return streamFrames(
            listOf(
                StreamFrame.ReasoningDelta(
                    id = "reasoning-1",
                    text = "Inspect the user request and identify the renderer component that owns tool call layout. ",
                    index = 0,
                ),
                StreamFrame.ReasoningDelta(
                    id = "reasoning-1",
                    text = "Prefer theme-provided colors, typography, and icons so the UI can be customized by consumers. ",
                    index = 0,
                ),
                StreamFrame.ReasoningComplete(
                    id = "reasoning-1",
                    text = listOf(
                        "Inspect the user request and identify the renderer component that owns tool call layout. ",
                        "Prefer theme-provided colors, typography, and icons so the UI can be customized by consumers. ",
                    ),
                    summary = listOf(
                        "The mock stream should include long, repeated, and varied tool calls to exercise collapsed and expanded rendering.",
                    ),
                    index = 0,
                ),
                StreamFrame.ToolCallDelta(
                    id = "tool-1",
                    name = "search_project",
                    content = searchArguments.take(80),
                    index = 1,
                ),
                StreamFrame.ToolCallDelta(
                    id = "tool-1",
                    name = "search_project",
                    content = searchArguments,
                    index = 1,
                ),
                StreamFrame.ToolCallComplete(
                    id = "tool-1",
                    name = "search_project",
                    content = searchArguments,
                    index = 1,
                ),
                StreamFrame.ToolCallDelta(
                    id = "tool-2",
                    name = "read_files",
                    content = readArguments.take(160),
                    index = 2,
                ),
                StreamFrame.ToolCallDelta(
                    id = "tool-2",
                    name = "read_files",
                    content = readArguments,
                    index = 2,
                ),
                StreamFrame.ToolCallComplete(
                    id = "tool-2",
                    name = "read_files",
                    content = readArguments,
                    index = 2,
                ),
                StreamFrame.ToolCallDelta(
                    id = "tool-3",
                    name = "shell",
                    content = shellCommand.take(120),
                    index = 3,
                ),
                StreamFrame.ToolCallDelta(
                    id = "tool-3",
                    name = "shell",
                    content = shellCommand.take(260),
                    index = 3,
                ),
                StreamFrame.ToolCallDelta(
                    id = "tool-3",
                    name = "shell",
                    content = shellCommand,
                    index = 3,
                ),
                StreamFrame.ToolCallComplete(
                    id = "tool-3",
                    name = "shell",
                    content = shellCommand,
                    index = 3,
                ),
                StreamFrame.ToolCallDelta(
                    id = "tool-4",
                    name = "summarize_logs",
                    content = shellOutput.take(180),
                    index = 4,
                ),
                StreamFrame.ToolCallComplete(
                    id = "tool-4",
                    name = "summarize_logs",
                    content = shellOutput,
                    index = 4,
                ),
                StreamFrame.TextDelta(
                    text = allText.take(70),
                    index = 5,
                ),
                StreamFrame.TextDelta(
                    text = allText.drop(70).take(95),
                    index = 5,
                ),
                StreamFrame.TextDelta(
                    text = allText.drop(165),
                    index = 5,
                ),
                StreamFrame.TextComplete(
                    text = allText,
                    index = 5,
                ),
                StreamFrame.End(finishReason = "stop"),
            ),
        )
    }

    fun streamText(
        text: String = "This is a mock assistant response.",
        chunkSize: Int = 8,
    ): Flow<StreamFrame> {
        val frames = text.chunked(chunkSize.coerceAtLeast(1))
            .map { chunk -> StreamFrame.TextDelta(text = chunk, index = 0) } +
            StreamFrame.TextComplete(text = text, index = 0) +
            StreamFrame.End(finishReason = "stop")
        return streamFrames(frames)
    }

    fun streamReasoning(
        id: String = "reasoning-1",
        text: List<String> = listOf("Plan the response. ", "Keep the mock deterministic."),
        summary: List<String> = listOf("Mock reasoning finished."),
    ): Flow<StreamFrame> {
        val frames = buildList {
            text.forEach { part ->
                add(StreamFrame.ReasoningDelta(id = id, text = part, index = 0))
            }
            summary.forEach { part ->
                add(StreamFrame.ReasoningDelta(id = id, summary = part, index = 0))
            }
            add(
                StreamFrame.ReasoningComplete(
                    id = id,
                    text = text,
                    summary = summary,
                    index = 0,
                ),
            )
            add(StreamFrame.End(finishReason = "stop"))
        }
        return streamFrames(frames)
    }

    fun streamToolCall(
        id: String = "tool-1",
        name: String = "mock_tool",
        content: String = """{"input":"demo"}""",
    ): Flow<StreamFrame> {
        return streamFrames(
            listOf(
                StreamFrame.ToolCallDelta(
                    id = id,
                    name = name,
                    content = content.take(content.length / 2),
                    index = 0,
                ),
                StreamFrame.ToolCallDelta(
                    id = id,
                    name = name,
                    content = content,
                    index = 0,
                ),
                StreamFrame.ToolCallComplete(
                    id = id,
                    name = name,
                    content = content,
                    index = 0,
                ),
                StreamFrame.End(finishReason = "tool_calls"),
            ),
        )
    }

    fun streamFrames(frames: List<StreamFrame>): Flow<StreamFrame> {
        return flow {
            frames.forEachIndexed { index, frame ->
                if (index > 0 && frameDelayMillis > 0) {
                    delay(frameDelayMillis)
                }
                emit(frame)
            }
        }
    }
}

private fun String.jsonLiteral(): String {
    return buildString {
        append('"')
        for (char in this@jsonLiteral) {
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
