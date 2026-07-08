package com.zhangke.compose.agent.render

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.rememberStreamingMarkdownState
import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.theme.AgentRenderTheme

@Composable
fun <T> AgentReasoning(
    modifier: Modifier = Modifier,
    agentToolCall: AgentOutput.Reasoning<T>,
) {
    val streamingMarkdownState = rememberStreamingMarkdownState()
    LaunchedEffect(agentToolCall) {
        streamingMarkdownState.append(agentToolCall.content)
    }
    SelectionContainer {
        Markdown(
            typography = AgentRenderTheme.typography.markdownTypography,
            modifier = modifier
                .padding(vertical = 6.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AgentRenderTheme.colorScheme.contentVariant.copy(alpha = 0.08F))
                .padding(12.dp),
            streamingMarkdownState = streamingMarkdownState,
            colors = AgentRenderTheme.colorScheme.markdownColors,
        )
    }
}
