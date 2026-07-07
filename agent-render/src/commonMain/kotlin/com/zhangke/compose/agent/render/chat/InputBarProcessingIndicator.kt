package com.zhangke.compose.agent.render.chat

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.theme.AgentRenderTheme

@Composable
fun InputBarProcessingIndicator(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "InputBarProcessingIndicator")
    val indicatorScale by transition.animateFloat(
        initialValue = 0.6F,
        targetValue = 1F,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1100,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "indicatorScale",
    )
    val indicatorAlpha by transition.animateFloat(
        initialValue = 0.6F,
        targetValue = 1F,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1100,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "indicatorAlpha",
    )

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(AgentRenderTheme.colorScheme.primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(11.dp)
                .graphicsLayer {
                    scaleX = indicatorScale
                    scaleY = indicatorScale
                    alpha = indicatorAlpha
                }
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White),
        )
    }
}
