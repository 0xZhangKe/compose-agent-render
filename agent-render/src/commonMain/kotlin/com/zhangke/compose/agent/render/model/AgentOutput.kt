package com.zhangke.compose.agent.render.model

import kotlin.time.Instant

interface AgentOutput {

    val isFinalResult: Boolean

    data class ToolCall(
        val id: String,
        val name: String,
        val arguments: String,
        val output: String,
        val status: ToolStatus,
        val createAt: Instant,
    ) : AgentOutput {

        override val isFinalResult: Boolean = false
    }

    data class Reasoning(
        val id: String,
        val content: String,
        val createAt: Instant,
    ) : AgentOutput {

        override val isFinalResult: Boolean = false
    }

    data class AssistantText(
        val id: String,
        val content: String,
        val createAt: Instant,
        val completed: Boolean,
        override val isFinalResult: Boolean
    ) : AgentOutput
}

enum class ToolStatus {
    Running,
    Success,
    Error,
}
