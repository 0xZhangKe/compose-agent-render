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
import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.theme.AgentRenderTheme
import com.zhangke.compose.agent.render.utils.noRippleClick

@Composable
fun <T> AgentOutput(
    modifier: Modifier = Modifier,
    outputList: List<AgentOutput<T>>,
    completed: Boolean,
    custom: @Composable ((data: T) -> Unit)? = null,
) {
    val icons = AgentRenderTheme.iconsProvider
    Box(modifier = modifier) {
        if (outputList.isEmpty()) return@Box
        Column(modifier = Modifier.fillMaxWidth()) {
            var expanded by rememberSaveable { mutableStateOf(true) }
            val finalResultOutput: AgentOutput.AssistantText<T>? by remember(outputList, completed) {
                val output = outputList.lastOrNull { it is AgentOutput.AssistantText && it.completed }?.let { it as? AgentOutput.AssistantText<T> }
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
                        text = "Reasoning Finished",
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

                            is AgentOutput.Custom -> {
                                custom?.invoke(output.data)
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
