package com.zhangke.compose.agent.render.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import com.zhangke.compose.agent.render.foundation.Icon

internal val ArrowUpward: ImageVector
    get() {
        if (cachingArrowUpward != null) {
            return cachingArrowUpward!!
        }
        cachingArrowUpward = agentIcon(name = "Filled.ArrowUpward") {
            agentPath {
                moveTo(4.0f, 12.0f)
                lineToRelative(1.41f, 1.41f)
                lineTo(11.0f, 7.83f)
                verticalLineTo(20.0f)
                horizontalLineToRelative(2.0f)
                verticalLineTo(7.83f)
                lineToRelative(5.58f, 5.59f)
                lineTo(20.0f, 12.0f)
                lineToRelative(-8.0f, -8.0f)
                lineToRelative(-8.0f, 8.0f)
                close()
            }
        }
        return cachingArrowUpward!!
    }

private var cachingArrowUpward: ImageVector? = null

@Composable
@Preview
private fun PreviewArrowUpward(){
    Icon(
        painter = rememberVectorPainter(ArrowUpward),
        modifier = Modifier,
        contentDescription = null,
    )
}
