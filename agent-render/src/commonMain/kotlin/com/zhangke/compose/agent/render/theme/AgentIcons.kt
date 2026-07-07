package com.zhangke.compose.agent.render.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.zhangke.compose.agent.render.icons.IconChevronDown
import com.zhangke.compose.agent.render.icons.IconChevronUp
import com.zhangke.compose.agent.render.icons.IconCode
import com.zhangke.compose.agent.render.icons.KeyboardArrowDown
import com.zhangke.compose.agent.render.icons.KeyboardArrowRight

interface AgentIconsProvider {

    val toolCallIcon: Painter @Composable get

    val expandMoreIcon: Painter @Composable get() = toolCallIcon

    val expandLessIcon: Painter @Composable get() = toolCallIcon
}

object DefaultAgentIconProvider : AgentIconsProvider {

    override val toolCallIcon: Painter
        @Composable
        get() = rememberVectorPainter(IconCode)

    override val expandMoreIcon: Painter
        @Composable
        get() = rememberVectorPainter(KeyboardArrowRight)

    override val expandLessIcon: Painter
        @Composable
        get() = rememberVectorPainter(KeyboardArrowDown)
}

val LocalAgentIconsProvider = compositionLocalOf<AgentIconsProvider> { DefaultAgentIconProvider }
