package com.zhangke.compose.agent.render.koog

import ai.koog.prompt.streaming.StreamFrame
import com.zhangke.compose.agent.render.model.AgentOutput
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class StreamFrameAdapterTest {

    @Test
    fun mergesToolCallDeltasAndDoesNotEmitDuplicateSnapshots() = runBlocking {
        val frames = listOf(
            StreamFrame.ToolCallDelta("call-1", "search", "{\"q\"", 0),
            StreamFrame.ToolCallDelta(null, null, "{\"q\":\"kotlin\"}", 0),
            StreamFrame.ToolCallDelta(null, null, "{\"q\":\"kotlin\"}", 0),
            StreamFrame.ToolCallComplete("call-1", "search", "{\"q\":\"kotlin\"}", 0),
        ).map { AgentAdapterFrame.LlmFrame<Nothing>(it) }

        val snapshots = frames.asFlow().reduceToAgentOutput().toList()

        assertEquals(3, snapshots.size)
        val output = snapshots.last().single() as AgentOutput.ToolCall
        assertEquals("tool-0-call-1", output.id)
        assertEquals("{\"q\":\"kotlin\"}", output.arguments)
    }

    @Test
    fun keepsSamePartIndexSeparateAcrossLlmResponses() = runBlocking {
        val frames = listOf(
            StreamFrame.ToolCallComplete("call-1", "first", "{}", 0),
            StreamFrame.End("tool_calls"),
            StreamFrame.ToolCallComplete("call-2", "second", "{}", 0),
        ).map { AgentAdapterFrame.LlmFrame<Nothing>(it) }

        val outputs = frames.asFlow().reduceToAgentOutput().toList().last()

        assertEquals(listOf("tool-0-call-1", "tool-1-call-2"), outputs.map { it.id })
    }

    @Test
    fun keepsToolCallsWithIdsSeparateWhenIndexIsMissing() = runBlocking {
        val frames = listOf(
            StreamFrame.ToolCallComplete("call-1", "first", "{}"),
            StreamFrame.ToolCallComplete("call-2", "second", "{}"),
        ).map { AgentAdapterFrame.LlmFrame<Nothing>(it) }

        val outputs = frames.asFlow().reduceToAgentOutput().toList().last()

        assertEquals(listOf("tool-0-call-1", "tool-0-call-2"), outputs.map { it.id })
    }

    @Test
    fun keepsTextPartsSeparateAcrossLlmResponses() = runBlocking {
        val frames = listOf(
            StreamFrame.TextComplete("first", 0),
            StreamFrame.End("stop"),
            StreamFrame.TextComplete("second", 0),
        ).map { AgentAdapterFrame.LlmFrame<Nothing>(it) }

        val outputs = frames.asFlow().reduceToAgentOutput().toList().last()

        assertEquals(listOf("assistant-0-0", "assistant-1-0"), outputs.map { it.id })
    }
}
