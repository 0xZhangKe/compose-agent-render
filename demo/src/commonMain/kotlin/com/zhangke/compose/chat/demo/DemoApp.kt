package com.zhangke.compose.chat.demo

import ai.koog.prompt.streaming.StreamFrame
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.AgentOutput
import com.zhangke.compose.agent.render.koog.reduceToAgentOutput
import com.zhangke.compose.agent.render.model.AgentOutput as AgentOutputModel
import com.zhangke.compose.agent.render.theme.AgentRenderTheme
import com.zhangke.compose.chat.demo.agent.MockAgent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun DemoApp() {
    val mockAgent = remember { MockAgent() }
    val coroutineScope = rememberCoroutineScope()
    var outputList by remember { mutableStateOf<List<AgentOutputModel>>(emptyList()) }
    var runningJob by remember { mutableStateOf<Job?>(null) }

    fun runMock(streamProvider: MockAgent.() -> Flow<StreamFrame>) {
        runningJob?.cancel()
        outputList = emptyList()
        runningJob = coroutineScope.launch {
            mockAgent.streamProvider()
                .reduceToAgentOutput()
                .collect { outputList = it }
        }
    }

    LaunchedEffect(Unit) {
        runMock {
            streamAllFrames("Render a full mock agent stream.")
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AgentRenderTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Mock Agent",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(
                            onClick = {
                                runMock {
                                    streamAllFrames("Render a full mock agent stream.")
                                }
                            },
                        ) {
                            Text("All Frames")
                        }
                        Button(
                            onClick = {
                                runMock {
                                    streamText("This text is emitted as multiple TextDelta frames before TextComplete.")
                                }
                            },
                        ) {
                            Text("Text")
                        }
                        Button(
                            onClick = {
                                runMock {
                                    streamReasoning()
                                }
                            },
                        ) {
                            Text("Reasoning")
                        }
                        Button(
                            onClick = {
                                runMock {
                                    streamToolCall()
                                }
                            },
                        ) {
                            Text("Tool Call")
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        AgentOutput(
                            modifier = Modifier.fillMaxWidth(),
                            outputList = outputList,
                        )
                    }
                }
            }
        }
    }
}
