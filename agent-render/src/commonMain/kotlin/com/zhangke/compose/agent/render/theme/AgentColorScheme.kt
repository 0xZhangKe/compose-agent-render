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
) {

    companion object {

        @Composable
        fun light(): AgentColorScheme {
            return AgentColorScheme(
                primary = Color(0xFF6200EE),
                content = Color(0xFF000000),
                contentVariant = Color(0xFF666666),
                link = Color(0xFF1E88E5),
                error = Color(0xFFC62828),
                humanInputContainer = Color(0xFFEDEDED),
                toolCallContainer = Color(0xFFEDEDED),
                toolCallContent = Color(0xFF202124),
            )
        }

        @Composable
        fun dark(): AgentColorScheme {
            return AgentColorScheme(
                primary = Color(0xFFBB86FC),
                content = Color(0xFFFFFFFF),
                contentVariant = Color(0xFFCCCCCC),
                link = Color(0xFF90CAF9),
                error = Color(0xFFFFB4AB),
                humanInputContainer = Color(0xFF2B2C2F),
                toolCallContainer = Color(0xFF2B2C2F),
                toolCallContent = Color(0xFFE8EAED),
            )
        }
    }
}

val LocalAgentColorScheme =
    compositionLocalOf<AgentColorScheme> { throw IllegalStateException("No AgentColorScheme provided!") }
