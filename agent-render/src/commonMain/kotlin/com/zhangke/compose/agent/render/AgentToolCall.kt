package com.zhangke.compose.agent.render

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.foundation.Icon
import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.model.ToolStatus
import com.zhangke.compose.agent.render.theme.AgentColorScheme
import com.zhangke.compose.agent.render.theme.AgentRenderTheme

@Composable
fun AgentToolCall(
    modifier: Modifier = Modifier,
    agentToolCall: AgentOutput.ToolCall,
) {
    var expanded by remember(agentToolCall.id) { mutableStateOf(false) }
    val colors = AgentRenderTheme.colorScheme
    val typography = AgentRenderTheme.typography
    val icons = AgentRenderTheme.iconsProvider

    Column(
        modifier = modifier.padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painter = icons.toolCallIcon,
                contentDescription = null,
                tint = colors.toolCallContentVariant,
                modifier = Modifier.size(18.dp),
            )
            BasicText(
                text = agentToolCall.commandTitle(),
                modifier = Modifier.weight(1F),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = typography.toolCallCommand.copy(
                    color = colors.toolCallContentVariant,
                ),
            )
            Icon(
                painter = if (expanded) icons.expandLessIcon else icons.expandMoreIcon,
                contentDescription = null,
                tint = colors.toolCallContentVariant,
                modifier = Modifier.size(18.dp),
            )
        }

        if (expanded) {
            ToolCallLog(
                title = agentToolCall.logTitle(),
                content = agentToolCall.logContent(),
            )
        }
    }
}

@Composable
private fun ToolCallLog(
    title: String,
    content: String,
) {
    val colors = AgentRenderTheme.colorScheme
    val typography = AgentRenderTheme.typography

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 220.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colors.toolCallContainer)
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            BasicText(
                text = title,
                style = typography.toolCallLine.copy(
                    color = colors.toolCallContentVariant,
                ),
            )
            Box(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                BasicText(
                    text = content,
                    modifier = Modifier.fillMaxWidth(),
                    style = typography.toolCallLine.copy(
                        color = colors.toolCallContent,
                    ),
                )
            }
        }
    }
}

private fun AgentOutput.ToolCall.commandTitle(): String {
    val command = arguments.toSingleLine()
    return buildString {
        if (name.isNotEmpty() && name.isNotBlank()) {
            append("Ran ")
            append(name)
        }
        if (command.isNotEmpty() && command.isNotBlank()) {
            append("  ")
            append(command)
        }
    }
}

private fun AgentOutput.ToolCall.logTitle(): String {
    return name.ifBlank { "Tool" }
}

private fun AgentOutput.ToolCall.logContent(): String {
    return buildString {
        if (arguments.isNotBlank()) {
            append("$ ")
            append(arguments.trim())
        }
        if (output.isNotBlank()) {
            if (isNotEmpty()) {
                append("\n\n")
            }
            append(output.trim())
        }
    }
}

private fun String.toSingleLine(): String {
    return trim()
        .lineSequence()
        .joinToString(separator = " ") { it.trim() }
        .replace(Regex("\\s+"), " ")
}

private val ToolStatus.label: String
    get() = when (this) {
        ToolStatus.Running -> "Running"
        ToolStatus.Success -> "Success"
        ToolStatus.Error -> "Error"
    }

private fun ToolStatus.color(colors: AgentColorScheme) =
    when (this) {
        ToolStatus.Running -> colors.warning
        ToolStatus.Success -> colors.success
        ToolStatus.Error -> colors.error
    }
