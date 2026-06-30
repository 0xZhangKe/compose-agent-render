package com.zhangke.compose.chat.demo.agent

import ai.koog.prompt.streaming.StreamFrame
import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.model.ToolStatus
import kotlin.time.Clock

fun StreamFrame.toAgentOutput(): AgentOutput? {
    val createAt = Clock.System.now()
    return when (this) {
        is StreamFrame.TextDelta -> AgentOutput.AssistantText(
            id = "assistant-${index ?: 0}",
            content = text,
            createAt = createAt,
        )

        is StreamFrame.ReasoningDelta -> AgentOutput.Reasoning(
            id = id ?: "reasoning-${index ?: 0}",
            content = summary ?: text.orEmpty(),
            createAt = createAt,
        )

        is StreamFrame.ToolCallDelta -> AgentOutput.ToolCall(
            id = id ?: "tool-${index ?: 0}",
            name = name.orEmpty(),
            arguments = content.orEmpty(),
            output = "",
            status = ToolStatus.Running,
            createAt = createAt,
        )

        is StreamFrame.ToolCallComplete -> AgentOutput.ToolCall(
            id = id ?: "tool-${index ?: 0}",
            name = name,
            arguments = content,
            output = "",
            status = ToolStatus.Success,
            createAt = createAt,
        )

        is StreamFrame.TextComplete,
        is StreamFrame.ReasoningComplete,
        is StreamFrame.End,
            -> null
    }
}
