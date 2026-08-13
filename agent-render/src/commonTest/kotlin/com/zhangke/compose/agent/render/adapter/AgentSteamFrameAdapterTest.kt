package com.zhangke.compose.agent.render.adapter

import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.model.AgentSteamFrameUiModel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AgentSteamFrameAdapterTest {

    @Test
    fun mergesIncrementalTextDeltasAndIgnoresRepeatedFrames() = runBlocking {
        val frames: List<AgentSteamFrameUiModel> = listOf(
            AgentSteamFrameUiModel.TextDelta("Hello ", 0),
            AgentSteamFrameUiModel.TextDelta("world", 0),
            AgentSteamFrameUiModel.TextDelta("world", 0),
            AgentSteamFrameUiModel.TextComplete("Hello world", 0),
        )

        val snapshots = frames.asFlow().reduceToAgentOutput().toList()

        assertEquals(3, snapshots.size)
        assertEquals(
            listOf("Hello ", "Hello world", "Hello world"),
            snapshots.map { (it.single() as AgentOutput.AssistantText).content },
        )
    }

    @Test
    fun mergesCumulativeTextDeltas() = runBlocking {
        val frames: List<AgentSteamFrameUiModel> = listOf(
            AgentSteamFrameUiModel.TextDelta("Hello ", 0),
            AgentSteamFrameUiModel.TextDelta("Hello world", 0),
            AgentSteamFrameUiModel.TextDelta("Hello world", 0),
        )

        val snapshots = frames.asFlow().reduceToAgentOutput().toList()

        assertEquals(2, snapshots.size)
        assertEquals(
            listOf("Hello ", "Hello world"),
            snapshots.map { (it.single() as AgentOutput.AssistantText).content },
        )
    }

    @Test
    fun mergesToolCallDeltasAndDoesNotEmitDuplicateSnapshots() = runBlocking {
        val frames: List<AgentSteamFrameUiModel> = listOf(
            AgentSteamFrameUiModel.ToolCallDelta("call-1", "search", "{\"q\"", 0),
            AgentSteamFrameUiModel.ToolCallDelta(null, null, "{\"q\":\"kotlin\"}", 0),
            AgentSteamFrameUiModel.ToolCallDelta(null, null, "{\"q\":\"kotlin\"}", 0),
            AgentSteamFrameUiModel.ToolCallComplete("call-1", "search", "{\"q\":\"kotlin\"}", 0),
        )

        val snapshots = frames.asFlow().reduceToAgentOutput().toList()

        assertEquals(3, snapshots.size)
        val output = snapshots.last().single() as AgentOutput.ToolCall
        assertEquals("tool-0-call-1", output.id)
        assertEquals("{\"q\":\"kotlin\"}", output.arguments)
    }

    @Test
    fun keepsSamePartIndexSeparateAcrossResponses() = runBlocking {
        val frames: List<AgentSteamFrameUiModel> = listOf(
            AgentSteamFrameUiModel.ToolCallComplete("call-1", "first", "{}", 0),
            AgentSteamFrameUiModel.End("tool_calls"),
            AgentSteamFrameUiModel.ToolCallComplete("call-2", "second", "{}", 0),
        )

        val outputs = frames.asFlow().reduceToAgentOutput().toList().last()

        assertEquals(
            listOf("tool-0-call-1", "tool-1-call-2"),
            outputs.map { (it as AgentOutput.ToolCall).id },
        )
    }

    @Test
    fun customAdapterCanTransformExistingOutputs() = runBlocking {
        data class ReplaceText(val content: String) : AgentSteamFrameUiModel

        val frames: List<AgentSteamFrameUiModel> = listOf(
            AgentSteamFrameUiModel.TextComplete("before", 0),
            ReplaceText("after"),
        )

        val snapshots = frames.asFlow().reduceToAgentOutput { frame, outputsById ->
            if (frame !is ReplaceText) {
                outputsById
            } else {
                outputsById.mapValues { (_, output) ->
                    if (output is AgentOutput.AssistantText) {
                        output.copy(content = frame.content)
                    } else {
                        output
                    }
                }
            }
        }.toList()

        assertEquals(2, snapshots.size)
        assertEquals(
            "after",
            (snapshots.last().single() as AgentOutput.AssistantText).content,
        )
    }

    @Test
    fun textCompleteAdapterCanReplaceDefaultText() = runBlocking {
        data class StructuredResult(
            val id: String,
            val content: String,
            val createAt: Instant,
            override val isFinalResult: Boolean,
        ) : AgentOutput

        val frames: List<AgentSteamFrameUiModel> = listOf(
            AgentSteamFrameUiModel.TextDelta("{\"title\"", 0),
            AgentSteamFrameUiModel.TextComplete("{\"title\":\"Hello\"}", 0),
        )

        val snapshots = frames.asFlow().reduceToAgentOutput(
            textCompleteAdapter = { frame, defaultOutput ->
                TextCompleteAdaptResult.Replace(
                    StructuredResult(
                        id = defaultOutput.id,
                        content = frame.text,
                        createAt = defaultOutput.createAt,
                        isFinalResult = true,
                    ),
                )
            },
        ).toList()

        assertEquals(2, snapshots.size)
        val output = assertIs<StructuredResult>(snapshots.last().single())
        assertEquals("assistant-0-0", output.id)
        assertEquals("{\"title\":\"Hello\"}", output.content)
        assertEquals(true, output.isFinalResult)
    }

    @Test
    fun textCompleteAdapterCanFallBackToDefaultText() = runBlocking {
        val frames: List<AgentSteamFrameUiModel> = listOf(
            AgentSteamFrameUiModel.TextComplete("Hello", 0),
        )

        val snapshots = frames.asFlow().reduceToAgentOutput(
            textCompleteAdapter = { _, _ -> TextCompleteAdaptResult.UseDefault },
        ).toList()

        val output = assertIs<AgentOutput.AssistantText>(snapshots.single().single())
        assertEquals("assistant-0-0", output.id)
        assertEquals("Hello", output.content)
    }

    @Test
    fun textCompleteAdapterCanDropStreamingText() = runBlocking {
        val frames: List<AgentSteamFrameUiModel> = listOf(
            AgentSteamFrameUiModel.TextDelta("internal", 0),
            AgentSteamFrameUiModel.TextComplete("internal", 0),
        )

        val snapshots = frames.asFlow().reduceToAgentOutput(
            textCompleteAdapter = { _, _ -> TextCompleteAdaptResult.Drop },
        ).toList()

        assertEquals(2, snapshots.size)
        assertEquals(emptyList(), snapshots.last())
    }
}
