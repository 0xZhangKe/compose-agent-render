package com.zhangke.compose.agent.render.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import com.zhangke.compose.agent.render.foundation.Icon
import com.zhangke.compose.agent.render.theme.AgentRenderTheme

internal val IconCode: ImageVector
    get() {
        if (cachingCode != null) {
            return cachingCode!!
        }
        cachingCode = agentIcon(name = "Filled.Code") {
            agentPath {
                moveTo(9.4f, 16.6f)
                lineTo(4.8f, 12.0f)
                lineToRelative(4.6f, -4.6f)
                lineTo(8.0f, 6.0f)
                lineToRelative(-6.0f, 6.0f)
                lineToRelative(6.0f, 6.0f)
                lineToRelative(1.4f, -1.4f)
                close()
                moveTo(14.6f, 16.6f)
                lineToRelative(4.6f, -4.6f)
                lineToRelative(-4.6f, -4.6f)
                lineTo(16.0f, 6.0f)
                lineToRelative(6.0f, 6.0f)
                lineToRelative(-6.0f, 6.0f)
                lineToRelative(-1.4f, -1.4f)
                close()
            }
        }
        return cachingCode!!
    }

private var cachingCode: ImageVector? = null

@Preview
@Composable
private fun PreviewIconCode() {
    AgentRenderTheme {
        Icon(
            painter = rememberVectorPainter(IconCode),
            modifier = Modifier,
            contentDescription = null,
        )
    }
}
