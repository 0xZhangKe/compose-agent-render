package com.zhangke.compose.agent.render.model

import kotlin.time.Instant

sealed interface AgentChatMessage {

    data class AgentOutputMessage(
        val outputList: List<AgentOutput<Any>>,
        val state: AgentOutputMessageState,
    ) : AgentChatMessage

    data class HumanInputMessage(
        val text: String,
        val createAt: Instant,
        val state: HumanInputMessageState,
    ) : AgentChatMessage
}

sealed interface AgentOutputMessageState {

    data object Processing : AgentOutputMessageState
    data object Completed : AgentOutputMessageState
    data class Error(val throwable: Throwable) : AgentOutputMessageState
}

sealed interface HumanInputMessageState {

    data object Sending : HumanInputMessageState
    data object Sent : HumanInputMessageState
    data class Error(val throwable: Throwable) : HumanInputMessageState
}