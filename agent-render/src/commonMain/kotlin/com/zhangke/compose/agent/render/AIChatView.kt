package com.zhangke.compose.agent.render

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.compose.agent.render.core.ChatMessage
import com.zhangke.compose.agent.render.core.ChatSender

@Composable
fun AIChatView(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        messages.forEach { message ->
            ChatMessageBubble(message = message)
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
) {
    val isUser = message.sender == ChatSender.User
    val bubbleColor = if (isUser) Color(0xFF0F6B57) else Color(0xFFE8ECEF)
    val contentColor = if (isUser) Color.White else Color(0xFF202427)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.82F)
                .clip(RoundedCornerShape(8.dp))
                .background(bubbleColor)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicText(
                text = message.content,
                style = TextStyle(
                    color = contentColor,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                ),
            )
        }
    }
}
