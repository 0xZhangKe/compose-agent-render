package com.zhangke.compose.agent.render.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AgentColorScheme(
    val primary: Color,
    val content: Color,
    val contentVariant: Color,
    val link: Color,
    val error: Color,
    val toolCallContainer: Color,
    val toolCallContent: Color,
    val humanInputContainer: Color,
    val outline: Color,
    val inputBarContainer: Color,
) {

    companion object {

        @Composable
        fun light(): AgentColorScheme {
            return AgentColorScheme(
                primary = Color(0xFF2E87FF),
                content = Color(0xFF191B22),
                contentVariant = Color(0xE0424753),
                link = Color(0xFF2E87FF),
                error = Color(0xFFA0004C),
                humanInputContainer = Color(0xFFECEDF7),
                toolCallContainer = Color(0xFFECEDF7),
                toolCallContent = Color(0xFF191B22),
                outline = Color(0xFFC2C6D6),
                inputBarContainer = Color(0xFFF9FBFF),
            )
        }

        @Composable
        fun dark(): AgentColorScheme {
            return AgentColorScheme(
                primary = Color(0xFFADC6FF),
                content = Color(0xFFE1E2EC),
                contentVariant = Color(0xFFC2C6D6),
                link = Color(0xFFADC6FF),
                error = Color(0xFFFFB1C4),
                humanInputContainer = Color(0xFF1D2027),
                toolCallContainer = Color(0xFF1D2027),
                toolCallContent = Color(0xFFE1E2EC),
                outline = Color(0xFF424753),
                inputBarContainer = Color(0xFF10131A),
            )
        }
    }
}

val LocalAgentColorScheme =
    compositionLocalOf<AgentColorScheme> { throw IllegalStateException("No AgentColorScheme provided!") }
