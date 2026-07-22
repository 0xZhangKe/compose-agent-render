package com.zhangke.compose.agent.render.composable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ProcessingText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = TextStyle.Default,
    showLatestText: Boolean = false,
    shimmerSpeed: Dp = 80.dp,
    maxShimmerDurationMillis: Int = 2_000,
    repeatDelayMillis: Int = 1000,
) {
    require(maxShimmerDurationMillis > 0) {
        "maxShimmerDurationMillis must be greater than 0"
    }
    require(repeatDelayMillis >= 0) { "repeatDelayMillis must not be negative" }

    var textWidthPx by remember { mutableFloatStateOf(0f) }

    val density = LocalDensity.current
    var highlightWidthPx by remember { mutableStateOf(with(density) { 24.dp.toPx() }) }
    val shimmerDurationMillis = with(density) {
        val speedPxPerSecond = shimmerSpeed.toPx()
        require(speedPxPerSecond > 0F) { "shimmerSpeed must be greater than 0.dp" }
        (((textWidthPx + highlightWidthPx * 2F) / speedPxPerSecond) * 1_000F)
            .roundToInt()
            .coerceIn(1, maxShimmerDurationMillis)
    }

    val transition = rememberInfiniteTransition(label = "text-shimmer")
    val shimmerAnimationSpec = if (repeatDelayMillis == 0) {
        tween(
            durationMillis = shimmerDurationMillis,
            easing = LinearEasing,
        )
    } else {
        keyframes {
            durationMillis = shimmerDurationMillis + repeatDelayMillis
            highlightWidthPx at 0
            textWidthPx + highlightWidthPx at shimmerDurationMillis using LinearEasing
            textWidthPx + highlightWidthPx at durationMillis
        }
    }
    val offsetX by transition.animateFloat(
        initialValue = -highlightWidthPx,
        targetValue = textWidthPx + highlightWidthPx,
        animationSpec = infiniteRepeatable(
            animation = shimmerAnimationSpec,
            repeatMode = RepeatMode.Restart,
        ),
        label = "highlight-offset",
    )

    var displayText by remember(text) { mutableStateOf(text) }
    BasicText(
        text = displayText,
        modifier = modifier,
        maxLines = maxLines,
        minLines = minLines,
        overflow = overflow,
        onTextLayout = { result ->
            if (showLatestText && result.hasVisualOverflow && displayText == text) {
                val cut = text.length / 3
                displayText = text.takeLast(text.length - cut)
            }
            textWidthPx = (0 until result.lineCount).maxOfOrNull { lineIndex ->
                result.getLineRight(lineIndex) - result.getLineLeft(lineIndex)
            } ?: 0F
            highlightWidthPx = highlightWidthPx.coerceAtMost(textWidthPx / 3F)
            println("textWidthPx: $textWidthPx, highlightWidthPx: $highlightWidthPx, shimmerDurationMillis: $shimmerDurationMillis")
        },
        style = style.copy(
            brush = Brush.linearGradient(
                colorStops = arrayOf(
                    0f to color,
                    0.24f to color,
                    0.25f to color.copy(alpha = 0.5f),
                    0.75f to color.copy(alpha = 0.5f),
                    0.76f to color,
                    1f to color,
                ),
                start = Offset(offsetX - highlightWidthPx, 0f),
                end = Offset(offsetX + highlightWidthPx, 0f),
                tileMode = TileMode.Clamp,
            ),
            fontSize = fontSize,
            lineHeight = lineHeight,
        )
    )
}
