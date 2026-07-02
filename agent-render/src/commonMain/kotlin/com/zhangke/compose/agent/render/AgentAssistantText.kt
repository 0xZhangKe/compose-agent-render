package com.zhangke.compose.agent.render

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.theme.AgentRenderTheme

@Composable
fun <T> AgentAssistantText(
    modifier: Modifier = Modifier,
    agentToolCall: AgentOutput.AssistantText<T>,
) {
    BasicText(
        text = agentToolCall.content,
        modifier = modifier.padding(vertical = 6.dp),
        style = AgentRenderTheme.typography.content.copy(
            color = AgentRenderTheme.colorScheme.content,
        ),
    )
}
