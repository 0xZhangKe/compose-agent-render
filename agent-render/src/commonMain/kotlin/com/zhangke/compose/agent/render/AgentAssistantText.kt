package com.zhangke.compose.agent.render

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.rememberStreamingMarkdownState
import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.theme.AgentRenderTheme

@Composable
fun <T> AgentAssistantText(
    modifier: Modifier = Modifier,
    agentToolCall: AgentOutput.AssistantText<T>,
) {
    val streamingMarkdownState = rememberStreamingMarkdownState()
    LaunchedEffect(agentToolCall) {
        streamingMarkdownState.append(agentToolCall.content)
    }
    SelectionContainer {
        Markdown(
            modifier = modifier.padding(vertical = 6.dp),
            streamingMarkdownState = streamingMarkdownState,
            typography = AgentRenderTheme.typography.markdownTypography,
            colors = AgentRenderTheme.colorScheme.markdownColors,
        )
    }
}
