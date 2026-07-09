package com.zhangke.compose.agent.render.model

import kotlin.time.Instant

sealed interface AgentChatMessage<T> {

    data class AgentOutputMessage<T>(
        val outputList: List<AgentOutput<T>>,
        val state: AgentOutputMessageState,
    ) : AgentChatMessage<T>

    data class HumanInputMessage<T>(
        val text: String,
        val createAt: Instant,
        val state: HumanInputMessageState,
    ) : AgentChatMessage<T>
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
