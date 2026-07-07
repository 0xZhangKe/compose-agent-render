package com.zhangke.compose.agent.render.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.MarkdownTypography

@Immutable
data class AgentTypography(
    val content: TextStyle,
    val toolCallLine: TextStyle,
    val inputBarContent: TextStyle,
    val toolCallCommand: TextStyle,
) {

    internal val markdownTypography: MarkdownTypography = DefaultMarkdownTypography(
        h1 = content.copy(
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Bold,
        ),
        h2 = content.copy(
            fontSize = 18.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Bold,
        ),
        h3 = content.copy(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        h4 = content.copy(
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        h5 = content.copy(
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        h6 = content.copy(
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
        ),
        text = content.copy(
            lineHeight = 18.sp,
        ),
        code = content.copy(
            lineHeight = 18.sp,
            fontFamily = FontFamily.Monospace,
        ),
        inlineCode = content.copy(
            lineHeight = 18.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
        ),
        quote = content.copy(
            lineHeight = 18.sp,
            fontStyle = FontStyle.Italic,
        ),
        paragraph = content.copy(
            lineHeight = 18.sp,
        ),
        ordered = content.copy(
            lineHeight = 18.sp,
        ),
        bullet = content.copy(
            lineHeight = 18.sp,
        ),
        list = content.copy(
            lineHeight = 18.sp,
        ),
        textLink = TextLinkStyles(
            style = SpanStyle(
                textDecoration = TextDecoration.Underline,
            ),
        ),
        table = content.copy(
            lineHeight = 18.sp,
        ),
    )

    companion object {

        fun default(): AgentTypography {
            return AgentTypography(
                content = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                ),
                inputBarContent = TextStyle.Default.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                ),
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
