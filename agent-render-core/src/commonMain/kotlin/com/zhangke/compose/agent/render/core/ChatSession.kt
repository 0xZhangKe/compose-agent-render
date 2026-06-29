package com.zhangke.compose.agent.render.core

class ChatSession(
    initialMessages: List<ChatMessage> = emptyList(),
) {
    val messages: List<ChatMessage> = initialMessages
}
