package com.zhangke.compose.agent.render.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.foundation.Icon
import com.zhangke.compose.agent.render.icons.ArrowUpward
import com.zhangke.compose.agent.render.theme.AgentRenderTheme

@Composable
fun InputBar(
    modifier: Modifier,
    minLines: Int = 2,
    maxLines: Int = Int.MAX_VALUE,
    onSendClick: (String) -> Unit,
) {
    Box(
        modifier = modifier.clip(AgentRenderTheme.shape.medium)
            .shadow(
                elevation = 6.dp,
                shape = AgentRenderTheme.shape.medium,
                clip = false,
            )
            .border(width = 1.dp, color = AgentRenderTheme.colorScheme.outline, shape = AgentRenderTheme.shape.medium)
            .background(color = AgentRenderTheme.colorScheme.inputBarContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Column {
            var inputtedValue by remember { mutableStateOf(TextFieldValue()) }
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = inputtedValue,
                minLines = minLines,
                maxLines = maxLines,
                textStyle = AgentRenderTheme.typography.inputBarContent.copy(
                    color = AgentRenderTheme.colorScheme.content,
                ),
                onValueChange = { inputtedValue = it },
            )

            Box(
                modifier = Modifier.align(Alignment.End)
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(onClick = { onSendClick(inputtedValue.text) }),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier,
                    painter = rememberVectorPainter(ArrowUpward),
                    contentDescription = null,
                    tint = AgentRenderTheme.colorScheme.content,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewInputBar() {
    AgentRenderTheme {
        InputBar(
            modifier = Modifier.fillMaxWidth(),
            onSendClick = {},
        )
    }
}
