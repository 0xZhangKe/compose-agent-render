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
    val success: Color = primary,
    val warning: Color = primary,
    val toolCallContainer: Color = contentVariant.copy(alpha = 0.12F),
    val toolCallContent: Color = content,
    val toolCallContentVariant: Color = contentVariant,
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
                success = Color(0xFF1B7F45),
                warning = Color(0xFFB26A00),
                toolCallContainer = Color(0xFFEDEDED),
                toolCallContent = Color(0xFF202124),
                toolCallContentVariant = Color(0xFF5F6368),
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
                success = Color(0xFF81C995),
                warning = Color(0xFFFFD18A),
                toolCallContainer = Color(0xFF2B2C2F),
                toolCallContent = Color(0xFFE8EAED),
                toolCallContentVariant = Color(0xFFB7B9BD),
            )
        }
    }
}

val LocalAgentColorScheme = compositionLocalOf<AgentColorScheme> { throw IllegalStateException("No AgentColorScheme provided!") }
