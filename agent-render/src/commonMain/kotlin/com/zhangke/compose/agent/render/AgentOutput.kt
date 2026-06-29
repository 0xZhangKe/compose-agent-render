package com.zhangke.compose.agent.render

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.compose.agent.render.core.model.AgentOutput

@Composable
fun AgentOutput(
    modifier: Modifier,
    outputList: List<AgentOutput>,
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            for (output in outputList) {
                when(output){
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
                }
            }
        }
    }
}
