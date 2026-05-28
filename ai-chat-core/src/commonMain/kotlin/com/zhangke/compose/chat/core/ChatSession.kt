package com.zhangke.compose.chat.core

class ChatSession(
    initialMessages: List<ChatMessage> = emptyList(),
) {
    val messages: List<ChatMessage> = initialMessages
}
