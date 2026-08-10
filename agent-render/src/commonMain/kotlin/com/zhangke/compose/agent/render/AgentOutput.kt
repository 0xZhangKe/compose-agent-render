package com.zhangke.compose.agent.render

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.foundation.Icon
import com.zhangke.compose.agent.render.model.AgentCompleteMetaDataUiModel
import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.theme.AgentRenderTheme
import com.zhangke.compose.agent.render.utils.noRippleClick
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

@Composable
fun AgentOutput(
    modifier: Modifier = Modifier,
    outputList: List<AgentOutput>,
    completed: Boolean,
    completeMetaDataUiModel: AgentCompleteMetaDataUiModel? = null,
    custom: @Composable ((data: AgentOutput) -> Unit)? = null,
) {
    val icons = AgentRenderTheme.iconsProvider
    Box(modifier = modifier) {
        if (outputList.isEmpty()) return@Box
        Column(modifier = Modifier.fillMaxWidth()) {
            var expanded by rememberSaveable { mutableStateOf(true) }
            val finalResultOutput: AgentOutput.AssistantText? by remember(outputList, completed) {
                val output = outputList.lastOrNull { it is AgentOutput.AssistantText && it.completed }?.let { it as? AgentOutput.AssistantText }
                mutableStateOf(output)
            }
            val collapsedOutputList by remember(finalResultOutput, outputList) {
                mutableStateOf(outputList.filterNot { it == finalResultOutput })
            }
            if (finalResultOutput != null) {
                LaunchedEffect(finalResultOutput) {
                    expanded = false
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(AgentRenderTheme.shape.small)
                        .noRippleClick {
                            expanded = !expanded
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicText(
                        text = completeMetaDataUiModel
                            .takeIf { completed }
                            .toCompleteSummary(),
                        modifier = Modifier.weight(1F),
                        style = AgentRenderTheme.typography.content.copy(
                            color = AgentRenderTheme.colorScheme.contentVariant,
                        ),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        modifier = Modifier.size(16.dp),
                        tint = AgentRenderTheme.colorScheme.contentVariant,
                        painter = if (expanded) icons.expandLessIcon else icons.expandMoreIcon,
                        contentDescription = null,
                    )
                }
            }
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = expanded,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    for (output in collapsedOutputList) {
                        when (output) {
                            is AgentOutput.ToolCall -> {
                                AgentToolCall(
                                    modifier = Modifier.fillMaxWidth(),
                                    agentToolCall = output,
                                )
                            }

                            is AgentOutput.Reasoning -> {
                                AgentReasoning(
                                    modifier = Modifier.fillMaxWidth(),
                                    agentToolCall = output,
                                )
                            }

                            is AgentOutput.AssistantText -> {
                                AgentAssistantText(
                                    modifier = Modifier.fillMaxWidth(),
                                    agentToolCall = output,
                                )
                            }

                            else -> {
                                custom?.invoke(output)
                            }
                        }
                    }
                }
            }
            finalResultOutput?.let { finalTextOutput ->
                AgentAssistantText(
                    modifier = Modifier.fillMaxWidth(),
                    agentToolCall = finalTextOutput,
                )
            }
        }
    }
}

internal fun AgentCompleteMetaDataUiModel?.toCompleteSummary(): String {
    if (this == null) return "Reasoning Finished"
    return buildList {
        add(duration?.let { "Worked for ${it.toDisplayDuration()}" } ?: "Reasoning Finished")
        tokens?.let { add("${it.toTokenDisplay()} tokens") }
        toolcallTimes?.let { times ->
            add("$times tool ${if (times == 1) "call" else "calls"}")
        }
    }.joinToString(separator = " · ")
}

private fun Long.toTokenDisplay(): String {
    val absoluteValue = kotlin.math.abs(toDouble())
    val (divisor, suffix) = when {
        absoluteValue >= 1_000_000_000 -> 1_000_000_000.0 to "B"
        absoluteValue >= 1_000_000 -> 1_000_000.0 to "M"
        absoluteValue >= 1_000 -> 1_000.0 to "K"
        else -> return toString()
    }
    return (toDouble() / divisor).formatTwoDecimals() + suffix
}

private fun Duration.toDisplayDuration(): String {
    val nonNegativeDuration = coerceAtLeast(Duration.ZERO)
    return when {
        nonNegativeDuration >= 1.hours -> {
            val hours = nonNegativeDuration.toDouble(DurationUnit.HOURS).formatAtMostOneDecimal()
            "$hours ${if (hours == "1") "hour" else "hours"}"
        }

        nonNegativeDuration >= 1.minutes -> {
            "${nonNegativeDuration.toDouble(DurationUnit.MINUTES).formatAtMostOneDecimal()}min"
        }

        else -> "${nonNegativeDuration.toDouble(DurationUnit.SECONDS).formatAtMostOneDecimal()}s"
    }
}

private fun Double.formatAtMostOneDecimal(): String {
    val tenths = (this * 10).roundToLong()
    return if (tenths % 10L == 0L) {
        (tenths / 10L).toString()
    } else {
        "${tenths / 10L}.${kotlin.math.abs(tenths % 10L)}"
    }
}

private fun Double.formatTwoDecimals(): String {
    val hundredths = (kotlin.math.abs(this) * 100).roundToLong()
    val sign = if (this < 0) "-" else ""
    val fraction = (hundredths % 100L).toString().padStart(2, '0')
    return "$sign${hundredths / 100L}.$fraction"
}
