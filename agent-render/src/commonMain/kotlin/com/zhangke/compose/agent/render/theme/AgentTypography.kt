package com.zhangke.compose.agent.render.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@Immutable
data class AgentTypography(
    val content: TextStyle,
    val toolCallLine: TextStyle,
    val toolCallCommand: TextStyle = content,
) {

    companion object {

        fun default(): AgentTypography {
            return AgentTypography(
                content = TextStyle.Default,
                toolCallLine = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                ),
                toolCallCommand = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                ),
            )
        }
    }
}

val LocalAgentTypography = compositionLocalOf { AgentTypography.default() }
