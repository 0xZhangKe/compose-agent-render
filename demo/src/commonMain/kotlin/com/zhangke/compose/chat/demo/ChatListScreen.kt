package com.zhangke.compose.chat.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.zhangke.compose.agent.render.model.ToolStatus
import com.zhangke.compose.agent.render.theme.AgentRenderTheme
import kotlin.time.Instant

@Composable
fun ChatListScreen() {
    val messageList = remember { mockMessageList() }
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
                        completed = true,
                        contentPadding = PaddingValues(top = 24.dp, bottom = inputBarHeight + 16.dp),
                    )
                    InputBar(
                        modifier = Modifier
                            .onSizeChanged { size -> inputBarHeight = with(density) { size.height.toDp() } }
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .align(Alignment.BottomCenter),
                        onSendClick = {

                        },
                    )
                }
            }
        }
    }
}

private fun mockMessageList(): List<AgentChatMessage> {
    return listOf(
        AgentChatMessage.HumanInputMessage(
            text = "帮我检查一下当前项目里的聊天列表组件，顺便展示几种 agent 输出状态和 Markdown 渲染效果。",
            createAt = timestamp(minutes = 0),
        ),
        AgentChatMessage.AgentOutputMessage(
            outputList = listOf(
                AgentOutput.Reasoning(
                    id = "reasoning-1",
                    content = "先确认消息模型、输出组件和主题设置，再准备一组能覆盖普通文本、推理内容、工具调用、错误输出和 Markdown 的 mock 数据。",
                    createAt = timestamp(minutes = 1),
                ),
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
                        已经找到核心渲染链路：

                        1. `AgentChatList` 负责排列多轮消息。
                        2. `AgentOutput` 负责折叠中间过程并展示最终回答。
                        3. `AgentAssistantText`、`AgentReasoning` 和 `AgentToolCall` 分别处理不同类型输出。

                        下面我会继续补一组更接近真实 agent 过程的示例数据。
                    """.trimIndent(),
                    createAt = timestamp(minutes = 2),
                    completed = true,
                ),
            ),
        ),
        AgentChatMessage.HumanInputMessage(
            text = "再模拟一次代码分析过程：包含推理、命令、表格和最终结论。",
            createAt = timestamp(minutes = 3),
        ),
        AgentChatMessage.AgentOutputMessage(
            outputList = listOf(
                AgentOutput.Reasoning(
                    id = "reasoning-2",
                    content = """
                        这次重点观察两点：

                        - 多个输出块之间的间距是否稳定；
                        - 最终文本较长时，折叠区域和正文是否仍然可读。
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
                        ## 组件检查结果

                        | 区域 | 覆盖情况 | 备注 |
                        | --- | --- | --- |
                        | Human input | 已覆盖 | 右侧气泡布局 |
                        | Reasoning | 已覆盖 | 使用浅色背景区分 |
                        | Tool call | 已覆盖 | 支持折叠和日志滚动 |
                        | Final result | 已覆盖 | 通过 `completed` 文本识别 |

                        建议在 demo 中保留 **成功、运行中、失败、长文本** 四类样例，这样调整主题、字号或间距时更容易发现视觉回归。
                    """.trimIndent(),
                    createAt = timestamp(minutes = 5),
                    completed = true,
                ),
            ),
        ),
        AgentChatMessage.HumanInputMessage(
            text = "加一个工具还在运行中的状态，用来观察 loading 场景。",
            createAt = timestamp(minutes = 6),
        ),
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
                    content = "当前 demo 进程仍在启动中，这条文本模拟 streaming 过程中的非最终回答，因此 `completed = false`。",
                    createAt = timestamp(minutes = 7),
                    completed = false,
                ),
            ),
        ),
        AgentChatMessage.HumanInputMessage(
            text = "再补一个失败工具调用，并让最终回答包含代码块。",
            createAt = timestamp(minutes = 8),
        ),
        AgentChatMessage.AgentOutputMessage(
            outputList = listOf(
                AgentOutput.Reasoning(
                    id = "reasoning-4",
                    content = "需要展示错误日志的排版，同时验证最终 Markdown 代码块不会撑乱列表宽度。",
                    createAt = timestamp(minutes = 9),
                ),
                AgentOutput.ToolCall(
                    id = "tool-4",
                    name = "cat",
                    arguments = "cat missing-file.txt",
                    output = "cat: missing-file.txt: No such file or directory",
                    status = ToolStatus.Error,
                    createAt = timestamp(minutes = 9),
                ),
                AgentOutput.AssistantText(
                    id = "assistant-4",
                    content = """
                        ### 失败状态样例

                        工具调用返回了文件不存在错误，UI 应当清晰展示失败命令和 stderr 内容。

                        ```kotlin
                        val message = AgentOutput.ToolCall(
                            id = "tool-4",
                            name = "cat",
                            arguments = "cat missing-file.txt",
                            status = ToolStatus.Error,
                        )
                        ```

                        这类输出通常不代表整个任务失败，最终回答仍然可以继续给出替代方案或下一步建议。
                    """.trimIndent(),
                    createAt = timestamp(minutes = 10),
                    completed = true,
                ),
            ),
        ),
        AgentChatMessage.HumanInputMessage(
            text = "最后给一个完整总结，内容长一点，用来测试滚动和最终结果折叠。",
            createAt = timestamp(minutes = 11),
        ),
        AgentChatMessage.AgentOutputMessage(
            outputList = listOf(
                AgentOutput.Reasoning(
                    id = "reasoning-5",
                    content = "总结需要覆盖当前 demo 的视觉检查点，并保持段落、列表、行内代码的混排。",
                    createAt = timestamp(minutes = 12),
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
                    createAt = timestamp(minutes = 12),
                ),
                AgentOutput.AssistantText(
                    id = "assistant-5",
                    content = """
                        ## 最终总结

                        我已经把 demo 数据扩展成多轮对话，当前可以覆盖这些展示场景：

                        - 普通用户输入和 agent 输出交替排列；
                        - reasoning 区块的 Markdown 渲染；
                        - tool call 的成功、运行中和失败状态；
                        - 最终回答中的标题、列表、表格、行内代码和代码块；
                        - 较长内容下的滚动、间距和折叠表现。

                        这组数据适合用来检查主题中的字体、颜色和 shape 调整是否影响聊天列表的整体稳定性。后续如果要验证 streaming，可以把最后一条 `AssistantText` 的 `completed` 在收尾前设为 `false`，收到结束事件后再切到 `true`。
                    """.trimIndent(),
                    createAt = timestamp(minutes = 13),
                    completed = true,
                ),
            ),
        ),
    )
}

private fun timestamp(minutes: Long): Instant {
    return Instant.fromEpochMilliseconds(1_719_820_800_000L + minutes * 60_000L)
}
