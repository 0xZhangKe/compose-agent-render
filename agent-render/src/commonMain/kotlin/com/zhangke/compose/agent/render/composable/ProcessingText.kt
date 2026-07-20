package com.zhangke.compose.agent.render.composable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun ProcessingText(
    text: String,
    baseColor: Color,
    highlightColor: Color,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = TextStyle.Default,
    showLatestText: Boolean = false,
) {
    var textWidthPx by remember { mutableFloatStateOf(0f) }

    val density = LocalDensity.current
    val highlightWidthPx = with(density) { 24.dp.toPx() }

    val transition = rememberInfiniteTransition(label = "text-shimmer")
    val offsetX by transition.animateFloat(
        initialValue = -highlightWidthPx,
        targetValue = textWidthPx + highlightWidthPx,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3_500,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "highlight-offset",
    )
    LaunchedEffect(offsetX){
        println("offsetX: $offsetX, textWidthPx: $textWidthPx, highlightWidthPx: $highlightWidthPx")
    }

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
            textWidthPx = result.size.width.toFloat()
        },
        style = style.copy(
            brush = Brush.linearGradient(
                colorStops = arrayOf(
                    0f to baseColor,
                    0.24f to baseColor,
                    0.25f to highlightColor,
                    0.75f to highlightColor,
                    0.76f to baseColor,
                    1f to baseColor,
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
