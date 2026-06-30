package com.zhangke.compose.agent.render.model

sealed interface AgentOutput {

    val id: String

    data class ToolCall(
        override val id: String,
        val name: String,
        val arguments: String,
        val output: String,
        val status: ToolStatus,
    ) : AgentOutput

    data class Reasoning(
        override val id: String,
        val content: String,
    ) : AgentOutput

    data class AssistantText(
        override val id: String,
        val content: String,
    ) : AgentOutput
}

enum class ToolStatus {
    Running,
    Success,
    Error,
}
