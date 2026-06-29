package com.zhangke.compose.chat.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.core.ChatMessage
import com.zhangke.compose.agent.render.core.ChatSender
import com.zhangke.compose.agent.render.AIChatView

@Composable
fun DemoApp() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Text(
                    text = "Compose AI Chat",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(24.dp))
                AIChatView(messages = demoMessages)
            }
        }
    }
}

private val demoMessages = listOf(
    ChatMessage(
        id = "hello",
        content = "Create a minimal Compose Multiplatform chat UI SDK.",
        sender = ChatSender.User,
    ),
    ChatMessage(
        id = "answer",
        content = "The SDK modules stay lightweight. Material3 is used only by the demo shell.",
        sender = ChatSender.Assistant,
    ),
)
