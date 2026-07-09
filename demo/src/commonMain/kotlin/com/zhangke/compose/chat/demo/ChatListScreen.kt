package com.zhangke.compose.chat.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.chat.AgentChatList
import com.zhangke.compose.agent.render.chat.InputBar
import com.zhangke.compose.agent.render.model.AgentChatMessage
import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.model.AgentOutputMessageState
import com.zhangke.compose.agent.render.model.HumanInputMessageState
import com.zhangke.compose.agent.render.model.ToolStatus
import com.zhangke.compose.agent.render.theme.AgentRenderTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Instant

@Composable
fun ChatListScreen() {
    var messageList by remember { mutableStateOf(emptyList<AgentChatMessage>()) }
    val inputBarProcessing = messageList.lastOrNull().isProcessing()
    LaunchedEffect(Unit) {
        mockMessageFlow().collect { messageList = it }
    }

    DemoTheme {
        AgentRenderTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
            ) {
                val density = LocalDensity.current
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    var inputBarHeight by remember { mutableStateOf(46.dp) }
                    AgentChatList(
                        modifier = Modifier.fillMaxSize(),
                        messageList = messageList,
                        contentPadding = PaddingValues(top = 24.dp, bottom = inputBarHeight + 16.dp),
                    )
                    InputBar(
                        modifier = Modifier
                            .onSizeChanged { size -> inputBarHeight = with(density) { size.height.toDp() } }
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .align(Alignment.BottomCenter),
                        processing = inputBarProcessing,
                        onSendClick = {

                        },
                    )
                }
            }
        }
    }
}

private fun AgentChatMessage?.isProcessing(): Boolean {
    return when (this) {
        is AgentChatMessage.AgentOutputMessage -> state is AgentOutputMessageState.Processing
        is AgentChatMessage.HumanInputMessage -> state is HumanInputMessageState.Sending
        null -> false
    }
}

private fun mockMessageFlow(): Flow<List<AgentChatMessage>> {
    return flow {
        val messages = mutableListOf<AgentChatMessage>()

        suspend fun emitMessages(delayMillis: Long = 700L) {
            emit(messages.toList())
            delay(delayMillis)
        }

        fun append(message: AgentChatMessage) {
            messages += message
        }

        fun replaceLast(message: AgentChatMessage) {
            messages[messages.lastIndex] = message
        }

        val firstHuman = AgentChatMessage.HumanInputMessage(
            text = "Please inspect the chat list component in this project and show several agent output states plus Markdown rendering.",
            createAt = timestamp(minutes = 0),
            state = HumanInputMessageState.Sending,
        )
        append(firstHuman)
        emitMessages(500L)
        replaceLast(firstHuman.copy(state = HumanInputMessageState.Sent))
        emitMessages()

        val firstAgentReasoning = AgentOutput.Reasoning<Any>(
            id = "reasoning-1",
            content = "First inspect the message model, output components, and theme settings, then prepare mock data covering plain text, reasoning, tool calls, error output, and Markdown.",
            createAt = timestamp(minutes = 1),
        )
        append(
            AgentChatMessage.AgentOutputMessage(
                outputList = listOf(firstAgentReasoning),
                state = AgentOutputMessageState.Processing,
            ),
        )
        emitMessages()
        replaceLast(
            AgentChatMessage.AgentOutputMessage(
                outputList = listOf(
                    firstAgentReasoning,
                    AgentOutput.ToolCall(
                        id = "tool-1",
                        name = "rg",
                        arguments = "rg -n \"AgentChatList|AgentOutput|AgentAssistantText|AgentToolCall\" agent-render demo",
                        output = """
                            agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/chat/AgentChatList.kt:18:fun AgentChatList(
                            agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/AgentOutput.kt:25:fun <T> AgentOutput(
                            agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/AgentAssistantText.kt:13:fun <T> AgentAssistantText(
                            agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/AgentToolCall.kt:29:fun <T> AgentToolCall(
                        """.trimIndent(),
                        status = ToolStatus.Success,
                        createAt = timestamp(minutes = 1),
                    ),
                ),
                state = AgentOutputMessageState.Processing,
            ),
        )
        emitMessages()
        replaceLast(
            AgentChatMessage.AgentOutputMessage(
                outputList = listOf(
                    firstAgentReasoning,
                    AgentOutput.ToolCall(
                        id = "tool-1",
                        name = "rg",
                        arguments = "rg -n \"AgentChatList|AgentOutput|AgentAssistantText|AgentToolCall\" agent-render demo",
                        output = """
                            agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/chat/AgentChatList.kt:18:fun AgentChatList(
                            agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/AgentOutput.kt:25:fun <T> AgentOutput(
                            agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/AgentAssistantText.kt:13:fun <T> AgentAssistantText(
                            agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/AgentToolCall.kt:29:fun <T> AgentToolCall(
                        """.trimIndent(),
                        status = ToolStatus.Success,
                        createAt = timestamp(minutes = 1),
                    ),
                    AgentOutput.AssistantText(
                        id = "assistant-1",
                        content = """
                            Found the core rendering path:

                            1. `AgentChatList` lays out multi-turn messages.
                            2. `AgentOutput` collapses intermediate work and displays the final answer.
                            3. `AgentAssistantText`, `AgentReasoning`, and `AgentToolCall` render the different output types.

                            Next I will add sample data that looks closer to a real agent run.
                        """.trimIndent(),
                        createAt = timestamp(minutes = 2),
                        completed = true,
                    ),
                ),
                state = AgentOutputMessageState.Completed,
            ),
        )
        emitMessages()

        val secondHuman = AgentChatMessage.HumanInputMessage(
            text = "Simulate another code analysis pass with reasoning, commands, a table, and a final conclusion.",
            createAt = timestamp(minutes = 3),
            state = HumanInputMessageState.Sent,
        )
        append(secondHuman)
        emitMessages()
        replaceLast(secondHuman.copy(state = HumanInputMessageState.Sent))
        append(
            AgentChatMessage.AgentOutputMessage(
                outputList = listOf(
                    AgentOutput.Reasoning(
                        id = "reasoning-2",
                        content = """
                            This pass focuses on two checks:

                            - whether spacing between output blocks stays stable;
                            - whether the collapsed area and body remain readable when the final text is long.
                        """.trimIndent(),
                        createAt = timestamp(minutes = 4),
                    ),
                    AgentOutput.ToolCall(
                        id = "tool-2",
                        name = "sed",
                        arguments = "sed -n '1,140p' agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/AgentOutput.kt",
                        output = """
                            @Composable
                            fun <T> AgentOutput(
                                modifier: Modifier = Modifier,
                                outputList: List<AgentOutput<T>>,
                                completed: Boolean,
                                custom: @Composable ((data: T) -> Unit)? = null,
                            ) {
                                // Renders reasoning, tool calls, custom blocks and final text.
                            }
                        """.trimIndent(),
                        status = ToolStatus.Success,
                        createAt = timestamp(minutes = 4),
                    ),
                ),
                state = AgentOutputMessageState.Processing,
            ),
        )
        emitMessages()
        replaceLast(
            AgentChatMessage.AgentOutputMessage(
                outputList = listOf(
                    AgentOutput.Reasoning(
                        id = "reasoning-2",
                        content = """
                            This pass focuses on two checks:

                            - whether spacing between output blocks stays stable;
                            - whether the collapsed area and body remain readable when the final text is long.
                        """.trimIndent(),
                        createAt = timestamp(minutes = 4),
                    ),
                    AgentOutput.ToolCall(
                        id = "tool-2",
                        name = "sed",
                        arguments = "sed -n '1,140p' agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/AgentOutput.kt",
                        output = """
                            @Composable
                            fun <T> AgentOutput(
                                modifier: Modifier = Modifier,
                                outputList: List<AgentOutput<T>>,
                                completed: Boolean,
                                custom: @Composable ((data: T) -> Unit)? = null,
                            ) {
                                // Renders reasoning, tool calls, custom blocks and final text.
                            }
                        """.trimIndent(),
                        status = ToolStatus.Success,
                        createAt = timestamp(minutes = 4),
                    ),
                    AgentOutput.AssistantText(
                        id = "assistant-2",
                        content = """
                            ## Component Check Results

                            | Area | Coverage | Notes |
                            | --- | --- | --- |
                            | Human input | Covered | right-aligned bubble layout |
                            | Reasoning | Covered | separated with a light background |
                            | Tool call | Covered | supports collapsing and scrollable logs |
                            | Final result | Covered | identified by completed assistant text |

                            Keep **success, running, failure, and long text** examples in the demo so visual regressions are easier to spot when adjusting theme, type, or spacing.
                        """.trimIndent(),
                        createAt = timestamp(minutes = 5),
                        completed = true,
                    ),
                ),
                state = AgentOutputMessageState.Completed,
            ),
        )
        emitMessages()

        append(
            AgentChatMessage.HumanInputMessage(
                text = "Add a running tool state so the loading scenario can be inspected.",
                createAt = timestamp(minutes = 6),
                state = HumanInputMessageState.Sent,
            ),
        )
        emitMessages()
        append(
            AgentChatMessage.AgentOutputMessage(
                outputList = listOf(
                    AgentOutput.ToolCall(
                        id = "tool-3",
                        name = "gradle",
                        arguments = "./gradlew :demo:desktopRun",
                        output = """
                            > Configure project :demo
                            > Task :agent-render:compileKotlinMetadata UP-TO-DATE
                            > Task :demo:compileKotlinDesktop

                            Build is still running...
                        """.trimIndent(),
                        status = ToolStatus.Running,
                        createAt = timestamp(minutes = 7),
                    ),
                    AgentOutput.AssistantText(
                        id = "assistant-3",
                        content = "The demo process is still starting. This text simulates a non-final streaming response, so `completed = false`.",
                        createAt = timestamp(minutes = 7),
                        completed = false,
                    ),
                ),
                state = AgentOutputMessageState.Processing,
            ),
        )
        emitMessages(1200L)

        append(
            AgentChatMessage.HumanInputMessage(
                text = "This message is still sending, which checks the user-message Sending state.",
                createAt = timestamp(minutes = 8),
                state = HumanInputMessageState.Sending,
            ),
        )
        emitMessages()
        replaceLast(
            AgentChatMessage.HumanInputMessage(
                text = "This message failed to send, so an error should appear at the bottom of the bubble.",
                createAt = timestamp(minutes = 9),
                state = HumanInputMessageState.Error(IllegalStateException("Network unavailable")),
            ),
        )
        emitMessages()

        append(
            AgentChatMessage.HumanInputMessage(
                text = "Add a failed tool call and include a code block in the final answer.",
                createAt = timestamp(minutes = 10),
                state = HumanInputMessageState.Sent,
            ),
        )
        emitMessages()
        append(
            AgentChatMessage.AgentOutputMessage(
                outputList = listOf(
                    AgentOutput.Reasoning(
                        id = "reasoning-4",
                        content = "Show error log formatting and verify that the final Markdown code block does not break the list width.",
                        createAt = timestamp(minutes = 11),
                    ),
                    AgentOutput.ToolCall(
                        id = "tool-4",
                        name = "cat",
                        arguments = "cat missing-file.txt",
                        output = "cat: missing-file.txt: No such file or directory",
                        status = ToolStatus.Error,
                        createAt = timestamp(minutes = 11),
                    ),
                    AgentOutput.AssistantText(
                        id = "assistant-4",
                        content = """
                            ### Failure State Example

                            The tool call returned a missing-file error. The UI should clearly show the failed command and stderr content.

                            ```kotlin
                            val message = AgentOutput.ToolCall(
                                id = "tool-4",
                                name = "cat",
                                arguments = "cat missing-file.txt",
                                status = ToolStatus.Error,
                            )
                            ```

                            This kind of output usually does not mean the whole task failed. The final answer can still provide alternatives or next steps.
                        """.trimIndent(),
                        createAt = timestamp(minutes = 12),
                        completed = true,
                    ),
                ),
                state = AgentOutputMessageState.Error(IllegalStateException("Tool exited with code 1")),
            ),
        )
        emitMessages()

        append(
            AgentChatMessage.HumanInputMessage(
                text = "Finally, provide a longer summary to test scrolling and final-result collapsing.",
                createAt = timestamp(minutes = 13),
                state = HumanInputMessageState.Sent,
            ),
        )
        emitMessages()
        append(
            AgentChatMessage.AgentOutputMessage(
                outputList = listOf(
                    AgentOutput.Reasoning(
                        id = "reasoning-5",
                        content = "The summary should cover the demo visual checkpoints while mixing paragraphs, lists, and inline code.",
                        createAt = timestamp(minutes = 14),
                    ),
                    AgentOutput.ToolCall(
                        id = "tool-5",
                        name = "git",
                        arguments = "git status --short",
                        output = """
                            M agent-render/src/commonMain/kotlin/com/zhangke/compose/agent/render/theme/AgentTypography.kt
                            M demo/src/commonMain/kotlin/com/zhangke/compose/chat/demo/ChatListScreen.kt
                        """.trimIndent(),
                        status = ToolStatus.Success,
                        createAt = timestamp(minutes = 14),
                    ),
                    AgentOutput.AssistantText(
                        id = "assistant-5",
                        content = """
                            ## Final Summary

                            The demo data now spans multiple turns and covers these display scenarios:

                            - alternating user input and agent output;
                            - Markdown rendering inside reasoning blocks;
                            - success, running, and failure states for tool calls;
                            - headings, lists, tables, inline code, and code blocks in final answers;
                            - scrolling, spacing, and collapse behavior with longer content.

                            This data set is useful for checking whether typography, color, or shape changes affect the overall stability of the chat list. To verify streaming later, keep the last `AssistantText.completed` as `false` until the end event arrives, then switch it to `true`.
                        """.trimIndent(),
                        createAt = timestamp(minutes = 15),
                        completed = true,
                    ),
                ),
                state = AgentOutputMessageState.Completed,
            ),
        )

        repeat(10) { index ->
            val step = index + 1
            val createAt = timestamp(minutes = 16L + index * 2L)
            append(
                AgentChatMessage.HumanInputMessage(
                    text = "Continuous conversation round $step: please send another mock response to increase the number of messages.",
                    createAt = createAt,
                    state = HumanInputMessageState.Sending,
                ),
            )
            emitMessages(1_000L)
            replaceLast(
                AgentChatMessage.HumanInputMessage(
                    text = "Continuous conversation round $step: please send another mock response to increase the number of messages.",
                    createAt = createAt,
                    state = HumanInputMessageState.Sent,
                ),
            )
            emitMessages(1_000L)

            val reasoning = AgentOutput.Reasoning<Any>(
                id = "reasoning-long-$step",
                content = "Mock request round $step/10: read the latest user input and prepare a new agent message.",
                createAt = createAt,
            )
            val runningTool = AgentOutput.ToolCall<Any>(
                id = "tool-long-$step",
                name = "simulate_step",
                arguments = "simulate_step --round=$step --total=10",
                output = """
                    round=$step
                    status=running
                    message_count=${messages.size + 1}
                """.trimIndent(),
                status = ToolStatus.Running,
                createAt = createAt,
            )
            append(
                AgentChatMessage.AgentOutputMessage(
                    outputList = listOf(reasoning, runningTool),
                    state = AgentOutputMessageState.Processing,
                ),
            )
            emitMessages(2_000L)
            replaceLast(
                AgentChatMessage.AgentOutputMessage(
                    outputList = listOf(
                        reasoning,
                        runningTool.copy(
                            output = """
                                round=$step
                                status=success
                                message_count=${messages.size}
                                latency=${180 + step * 17}ms
                            """.trimIndent(),
                            status = ToolStatus.Success,
                        ),
                        AgentOutput.AssistantText(
                            id = "assistant-long-$step",
                            content = """
                                Round $step returned a new message.

                                - This is an independent agent output message;
                                - the Flow will keep appending more user and agent messages;
                                - this helps verify list scrolling and bottom detection while the message count keeps increasing.
                            """.trimIndent(),
                            createAt = createAt,
                            completed = true,
                        ),
                    ),
                    state = AgentOutputMessageState.Completed,
                ),
            )
            emitMessages(2_000L)
        }

        emit(messages.toList())
    }
}

private fun timestamp(minutes: Long): Instant {
    return Instant.fromEpochMilliseconds(1_719_820_800_000L + minutes * 60_000L)
}
