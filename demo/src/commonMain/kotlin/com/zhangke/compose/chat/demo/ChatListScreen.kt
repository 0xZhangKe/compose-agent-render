package com.zhangke.compose.chat.demo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.chat.AgentChatList
import com.zhangke.compose.agent.render.model.AgentChatMessage
import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.model.ToolStatus
import com.zhangke.compose.agent.render.theme.AgentRenderTheme
import kotlin.time.Instant

@Composable
fun ChatListScreen() {
    val messageList = remember { mockMessageList() }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AgentRenderTheme {
                AgentChatList(
                    modifier = Modifier.fillMaxSize(),
                    messageList = messageList,
                    contentPadding = PaddingValues(20.dp),
                )
            }
        }
    }
}

private fun mockMessageList(): List<AgentChatMessage> {
    return listOf(
        AgentChatMessage.HumanInputMessage(
            text = "帮我检查一下当前项目里的聊天列表组件，顺便展示几种 agent 输出状态。",
            createAt = timestamp(minutes = 0),
        ),
        AgentChatMessage.AgentOutputMessage(
            outputList = listOf(
                AgentOutput.Reasoning(
                    id = "reasoning-1",
                    content = "需要先确认消息模型，然后准备一组包含用户输入、普通文本、推理内容和工具调用的 mock 数据。",
                    createAt = timestamp(minutes = 1),
                ),
                AgentOutput.ToolCall(
                    id = "tool-1",
                    name = "rg",
                    arguments = "rg -n \"AgentChatList|AgentChatMessage\" .",
                    output = "agent-render/src/commonMain/kotlin/.../AgentChatList.kt\nagent-render/src/commonMain/kotlin/.../AgentChatMessage.kt",
                    status = ToolStatus.Success,
                    createAt = timestamp(minutes = 1),
                ),
                AgentOutput.AssistantText(
                    id = "assistant-1",
                    content = "已经找到 `AgentChatList` 和消息模型。下面我会用 mock 数据把聊天列表完整渲染出来。",
                    createAt = timestamp(minutes = 2),
                ),
            ),
        ),
        AgentChatMessage.HumanInputMessage(
            text = "再加一个包含工具调用中的状态，看看 UI 是否稳定。",
            createAt = timestamp(minutes = 3),
        ),
        AgentChatMessage.AgentOutputMessage(
            outputList = listOf(
                AgentOutput.ToolCall(
                    id = "tool-2",
                    name = "gradle",
                    arguments = "./gradlew :agent-render:compileKotlinDesktop",
                    output = "",
                    status = ToolStatus.Running,
                    createAt = timestamp(minutes = 4),
                ),
                AgentOutput.AssistantText(
                    id = "assistant-2",
                    content = "编译任务已开始，当前工具调用仍在运行中。列表会继续保持可滚动，并按消息顺序显示内容。",
                    createAt = timestamp(minutes = 4),
                ),
            ),
        ),
        AgentChatMessage.HumanInputMessage(
            text = "如果失败状态也有样例就更好了。",
            createAt = timestamp(minutes = 5),
        ),
        AgentChatMessage.AgentOutputMessage(
            outputList = listOf(
                AgentOutput.ToolCall(
                    id = "tool-3",
                    name = "cat",
                    arguments = "cat missing-file.txt",
                    output = "cat: missing-file.txt: No such file or directory",
                    status = ToolStatus.Error,
                    createAt = timestamp(minutes = 6),
                ),
                AgentOutput.AssistantText(
                    id = "assistant-3",
                    content = "失败状态也已覆盖。这个示例包含多个 HumanInputMessage 和 AgentOutputMessage，可用于检查聊天列表布局。",
                    createAt = timestamp(minutes = 7),
                ),
            ),
        ),
    )
}

private fun timestamp(minutes: Long): Instant {
    return Instant.fromEpochMilliseconds(1_719_820_800_000L + minutes * 60_000L)
}
