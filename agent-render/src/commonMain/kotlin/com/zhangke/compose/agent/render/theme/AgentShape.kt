package com.zhangke.compose.agent.render.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

data class AgentShape(
    val small: Shape,
    val medium: Shape,
    val large: Shape,
) {

    companion object {

        @Composable
        fun default(): AgentShape {
            return AgentShape(
                small = RoundedCornerShape(4.dp),
                medium = RoundedCornerShape(8.dp),
                large = RoundedCornerShape(16.dp),
            )
        }
    }
}

val LocalAgentShape =
    compositionLocalOf<AgentShape> { throw IllegalStateException("No AgentShape provided!") }
