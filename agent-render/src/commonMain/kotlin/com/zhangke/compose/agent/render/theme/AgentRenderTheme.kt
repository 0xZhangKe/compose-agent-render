package com.zhangke.compose.agent.render.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun AgentRenderTheme(
    colorScheme: AgentColorScheme = AgentColorScheme.light(),
    typography: AgentTypography = AgentTypography.default(),
    iconsProvider: AgentIconsProvider = DefaultAgentIconProvider,
    shape: AgentShape = AgentShape.default(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAgentColorScheme provides colorScheme,
        LocalAgentTypography provides typography,
        LocalAgentIconsProvider provides iconsProvider,
        LocalAgentShape provides shape,
    ) {
        content()
    }
}

object AgentRenderTheme {

    val colorScheme: AgentColorScheme
        @Composable @ReadOnlyComposable get() = LocalAgentColorScheme.current

    val typography: AgentTypography
        @Composable @ReadOnlyComposable get() = LocalAgentTypography.current

    val iconsProvider: AgentIconsProvider
        @Composable @ReadOnlyComposable get() = LocalAgentIconsProvider.current

    val shape: AgentShape
        @Composable @ReadOnlyComposable get() = LocalAgentShape.current
}
