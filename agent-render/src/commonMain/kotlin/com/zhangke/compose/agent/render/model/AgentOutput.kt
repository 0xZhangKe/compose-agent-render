package com.zhangke.compose.agent.render.model

import kotlin.time.Instant

sealed interface AgentOutput {

    val id: String

    data class ToolCall(
        override val id: String,
        val name: String,
        val arguments: String,
        val output: String,
        val status: ToolStatus,
        val createAt: Instant,
    ) : AgentOutput

    data class Reasoning(
        override val id: String,
        val content: String,
        val createAt: Instant,
    ) : AgentOutput

    data class AssistantText(
        override val id: String,
        val content: String,
        val createAt: Instant,
    ) : AgentOutput
}

enum class ToolStatus {
    Running,
    Success,
    Error,
}
