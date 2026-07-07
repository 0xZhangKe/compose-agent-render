package com.zhangke.compose.agent.render.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.model.AgentChatMessage
import com.zhangke.compose.agent.render.theme.AgentRenderTheme

@Composable
fun AgentHumanInput(
    modifier: Modifier,
    input: AgentChatMessage.HumanInputMessage,
) {
    val colors = AgentRenderTheme.colorScheme
    val typography = AgentRenderTheme.typography
    Box(
        modifier = modifier
            .clip(AgentRenderTheme.shape.medium)
            .background(colors.humanInputContainer)
            .padding(16.dp),
    ) {
        BasicText(
            modifier = Modifier,
            text = input.text,
            style = typography.content.copy(
                color = colors.content,
                textAlign = TextAlign.Start,
            ),
        )
    }
}
