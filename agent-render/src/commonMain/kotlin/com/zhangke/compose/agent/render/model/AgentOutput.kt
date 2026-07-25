package com.zhangke.compose.agent.render.model

import kotlin.time.Instant

interface AgentOutput {

    data class ToolCall(
        val id: String,
        val name: String,
        val arguments: String,
        val output: String,
        val status: ToolStatus,
        val createAt: Instant,
    ) : AgentOutput

    data class Reasoning(
        val id: String,
        val content: String,
        val createAt: Instant,
    ) : AgentOutput

    data class AssistantText(
        val id: String,
        val content: String,
        val createAt: Instant,
        val completed: Boolean,
    ) : AgentOutput
}

enum class ToolStatus {
    Running,
    Success,
    Error,
}
