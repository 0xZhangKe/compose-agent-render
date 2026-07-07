package com.zhangke.compose.agent.render.model

import kotlin.time.Instant

sealed interface AgentOutput<T> {

    val id: String

    data class ToolCall<T>(
        override val id: String,
        val name: String,
        val arguments: String,
        val output: String,
        val status: ToolStatus,
        val createAt: Instant,
    ) : AgentOutput<T>

    data class Reasoning<T>(
        override val id: String,
        val content: String,
        val createAt: Instant,
    ) : AgentOutput<T>

    data class AssistantText<T>(
        override val id: String,
        val content: String,
        val createAt: Instant,
        val completed: Boolean,
    ) : AgentOutput<T>

    data class Custom<T>(val data: T, override val id: String) : AgentOutput<T>
}

enum class ToolStatus {
    Running,
    Success,
    Error,
}
