package com.zhangke.compose.agent.render.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.model.AgentChatMessage
import com.zhangke.compose.agent.render.model.HumanInputMessageState
import com.zhangke.compose.agent.render.theme.AgentRenderTheme

@Composable
fun AgentHumanInput(
    modifier: Modifier,
    input: AgentChatMessage.HumanInputMessage,
    state: HumanInputMessageState,
) {
    val colors = AgentRenderTheme.colorScheme
    val typography = AgentRenderTheme.typography
    Column(
        modifier = modifier,
    ) {
        BasicText(
            modifier = Modifier
                .clip(AgentRenderTheme.shape.medium)
                .background(colors.humanInputContainer)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            text = input.text,
            style = typography.content.copy(
                color = colors.content,
                textAlign = TextAlign.Start,
            ),
        )
        when (state) {
            is HumanInputMessageState.Sending -> {
                BasicText(
                    modifier = Modifier.align(Alignment.End)
                        .padding(top = 4.dp),
                    text = "Sending...",
                    style = typography.content.copy(
                        color = colors.contentVariant,
                    ),
                )
            }

            is HumanInputMessageState.Error -> {
                BasicText(
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
                    text = "Error: ${state.throwable.message}",
                    maxLines = 1,
                    style = typography.content.copy(
                        color = colors.error,
                    ),
                )
            }

            is HumanInputMessageState.Sent -> {}
        }
    }
}
