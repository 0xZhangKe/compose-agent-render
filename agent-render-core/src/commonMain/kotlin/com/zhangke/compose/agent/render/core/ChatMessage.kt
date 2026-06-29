package com.zhangke.compose.agent.render.core

data class ChatMessage(
    val id: String,
    val content: String,
    val sender: ChatSender,
)

enum class ChatSender {
    User,
    Assistant,
}
