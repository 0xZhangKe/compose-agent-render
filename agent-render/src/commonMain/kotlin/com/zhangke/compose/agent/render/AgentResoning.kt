package com.zhangke.compose.agent.render

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.theme.AgentRenderTheme

@Composable
fun AgentReasoning(
    modifier: Modifier = Modifier,
    agentToolCall: AgentOutput.Reasoning,
) {
    BasicText(
        text = agentToolCall.content,
        modifier = modifier
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AgentRenderTheme.colorScheme.contentVariant.copy(alpha = 0.08F))
            .padding(12.dp),
        style = AgentRenderTheme.typography.content.merge(
            TextStyle(
                color = AgentRenderTheme.colorScheme.contentVariant,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            ),
        ),
    )
}
