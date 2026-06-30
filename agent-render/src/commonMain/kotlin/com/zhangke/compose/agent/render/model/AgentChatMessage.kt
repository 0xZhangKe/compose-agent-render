package com.zhangke.compose.agent.render.model

import kotlin.time.Instant

sealed interface AgentChatMessage {

    data class AgentOutputMessage(
        val outputList: List<AgentOutput>,
    ) : AgentChatMessage

    data class HumanInputMessage(
        val text: String,
        val createAt: Instant,
    ) : AgentChatMessage
}
