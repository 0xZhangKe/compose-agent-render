package com.zhangke.compose.agent.render.adapter

import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.model.AgentSteamFrameUiModel
import com.zhangke.compose.agent.render.model.ToolStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlin.time.Instant

typealias CustomerAdapter = (
    frame: AgentSteamFrameUiModel,
    outputsById: Map<String, AgentOutput>,
) -> Map<String, AgentOutput>

/**
 * Controls how completed assistant text is represented in the output list.
 */
sealed interface TextCompleteAdaptResult {

    /** Keep the default [AgentOutput.AssistantText] representation. */
    data object UseDefault : TextCompleteAdaptResult

    /** Replace the default assistant text with a custom [AgentOutput]. */
    data class Replace(
        val output: AgentOutput,
    ) : TextCompleteAdaptResult

    /** Remove the completed assistant text from the output list. */
    data object Drop : TextCompleteAdaptResult
}

typealias TextCompleteAdapter = (
    frame: AgentSteamFrameUiModel.TextComplete,
    defaultOutput: AgentOutput.AssistantText,
) -> TextCompleteAdaptResult

fun Flow<AgentSteamFrameUiModel>.reduceToAgentOutput(
    customAdapter: CustomerAdapter = { _, outputsById -> outputsById },
): Flow<List<AgentOutput>> {
    return reduceToAgentOutputInternal(
        customAdapter = customAdapter,
        textCompleteAdapter = { _, _ -> TextCompleteAdaptResult.UseDefault },
    )
}

/**
 * Reduces frames while allowing completed assistant text to be replaced with custom UI outputs.
 *
 * The default output passed to [textCompleteAdapter] contains the reducer-generated stable id and
 * creation time. Custom outputs should retain that identity when applicable so an earlier
 * streaming text row is replaced in place.
 */
fun Flow<AgentSteamFrameUiModel>.reduceToAgentOutput(
    textCompleteAdapter: TextCompleteAdapter,
    customAdapter: CustomerAdapter = { _, outputsById -> outputsById },
): Flow<List<AgentOutput>> {
    return reduceToAgentOutputInternal(
        customAdapter = customAdapter,
        textCompleteAdapter = textCompleteAdapter,
    )
}

private fun Flow<AgentSteamFrameUiModel>.reduceToAgentOutputInternal(
    customAdapter: CustomerAdapter,
    textCompleteAdapter: TextCompleteAdapter,
): Flow<List<AgentOutput>> {
    return flow {
        val reducer = AgentSteamFrameReducer(
            customAdapter = customAdapter,
            textCompleteAdapter = textCompleteAdapter,
        )
        this@reduceToAgentOutputInternal.collect { frame ->
            if (reducer.reduce(frame)) {
                emit(reducer.outputs)
            }
        }
    }
}

private class AgentSteamFrameReducer(
    private val customAdapter: CustomerAdapter,
    private val textCompleteAdapter: TextCompleteAdapter,
) {

    private var outputsById: MutableMap<String, AgentOutput> = linkedMapOf()
    private val textById = mutableMapOf<String, String>()
    private val reasoningById = mutableMapOf<String, String>()
    private val toolCallsById = mutableMapOf<String, ToolCallState>()
    private val createAtById = mutableMapOf<String, Instant>()
    private val reasoningIdsByIndex = mutableMapOf<Int, String>()
    private val toolCallIdsByIndex = mutableMapOf<Int, String>()
    private var responseIndex = 0

    val outputs: List<AgentOutput>
        get() = outputsById.values.toList()

    fun reduce(frame: AgentSteamFrameUiModel): Boolean {
        return when (frame) {
            is AgentSteamFrameUiModel.TextDelta -> reduceTextDelta(frame)
            is AgentSteamFrameUiModel.TextComplete -> reduceTextComplete(frame)
            is AgentSteamFrameUiModel.ReasoningDelta -> reduceReasoningDelta(frame)
            is AgentSteamFrameUiModel.ReasoningComplete -> reduceReasoningComplete(frame)
            is AgentSteamFrameUiModel.ToolCallDelta -> reduceToolCallDelta(frame)
            is AgentSteamFrameUiModel.ToolCallComplete -> reduceToolCallComplete(frame)
            is AgentSteamFrameUiModel.End -> {
                responseIndex++
                reasoningIdsByIndex.clear()
                toolCallIdsByIndex.clear()
                false
            }

            else -> reduceCustomFrame(frame)
        }
    }

    private fun reduceCustomFrame(frame: AgentSteamFrameUiModel): Boolean {
        val currentOutputs = outputsById.toMap()
        val adaptedOutputs = customAdapter(frame, currentOutputs)
        if (currentOutputs.entries.toList() == adaptedOutputs.entries.toList()) return false
        outputsById = adaptedOutputs.toMutableMap()
        return true
    }

    private fun reduceTextDelta(frame: AgentSteamFrameUiModel.TextDelta): Boolean {
        val id = frame.assistantId(responseIndex)
        val content = textById.orEmpty(id).mergeDelta(frame.text)
        textById[id] = content
        return putOutput(
            id,
            AgentOutput.AssistantText(
                id = id,
                content = content,
                createAt = createAtById.getOrCreate(id),
                completed = false,
                isFinalResult = false,
            ),
        )
    }

    private fun reduceTextComplete(frame: AgentSteamFrameUiModel.TextComplete): Boolean {
        val id = frame.assistantId(responseIndex)
        val defaultOutput = AgentOutput.AssistantText(
            id = id,
            content = frame.text,
            createAt = createAtById.getOrCreate(id),
            completed = true,
            isFinalResult = false,
        )
        val changed = when (val result = textCompleteAdapter(frame, defaultOutput)) {
            TextCompleteAdaptResult.UseDefault -> putOutput(id, defaultOutput)
            is TextCompleteAdaptResult.Replace -> putOutput(id, result.output)
            TextCompleteAdaptResult.Drop -> outputsById.remove(id) != null
        }
        textById.remove(id)
        createAtById.remove(id)
        return changed
    }

    private fun reduceReasoningDelta(frame: AgentSteamFrameUiModel.ReasoningDelta): Boolean {
        val delta = frame.summary ?: frame.text ?: return false
        val id = frame.reasoningId(responseIndex)
        val content = reasoningById.orEmpty(id) + delta
        reasoningById[id] = content
        return putOutput(
            id,
            AgentOutput.Reasoning(
                id = id,
                content = content,
                createAt = createAtById.getOrCreate(id),
            ),
        )
    }

    private fun reduceReasoningComplete(frame: AgentSteamFrameUiModel.ReasoningComplete): Boolean {
        val id = frame.reasoningId(responseIndex)
        val content = frame.summary?.joinToString(separator = "")
            ?: frame.content.joinToString(separator = "")
        reasoningById[id] = content
        val changed = putOutput(
            id,
            AgentOutput.Reasoning(
                id = id,
                content = content,
                createAt = createAtById.getOrCreate(id),
            ),
        )
        reasoningById.remove(id)
        createAtById.remove(id)
        return changed
    }

    private fun reduceToolCallDelta(frame: AgentSteamFrameUiModel.ToolCallDelta): Boolean {
        val id = frame.toolCallId(responseIndex)
        val current = toolCallsById[id] ?: ToolCallState(
            id = id,
            createAt = createAtById.getOrCreate(id),
        )
        val next = current.copy(
            name = frame.name ?: current.name,
            arguments = current.arguments.mergeDelta(frame.content),
            status = ToolStatus.Running,
        )
        toolCallsById[id] = next
        return putOutput(id, next.toAgentOutput())
    }

    private fun reduceToolCallComplete(frame: AgentSteamFrameUiModel.ToolCallComplete): Boolean {
        val id = frame.toolCallId(responseIndex)
        val current = toolCallsById[id] ?: ToolCallState(
            id = id,
            createAt = createAtById.getOrCreate(id),
        )
        val next = current.copy(
            name = frame.name,
            arguments = frame.content,
            status = ToolStatus.Success,
        )
        toolCallsById[id] = next
        val changed = putOutput(id, next.toAgentOutput())
        toolCallsById.remove(id)
        createAtById.remove(id)
        return changed
    }

    private fun AgentSteamFrameUiModel.ReasoningDelta.reasoningId(responseIndex: Int): String =
        resolveFrameId("reasoning", responseIndex, index, id, reasoningIdsByIndex)

    private fun AgentSteamFrameUiModel.ReasoningComplete.reasoningId(responseIndex: Int): String =
        resolveFrameId("reasoning", responseIndex, index, id, reasoningIdsByIndex)

    private fun AgentSteamFrameUiModel.ToolCallDelta.toolCallId(responseIndex: Int): String =
        resolveFrameId("tool", responseIndex, index, id, toolCallIdsByIndex)

    private fun AgentSteamFrameUiModel.ToolCallComplete.toolCallId(responseIndex: Int): String =
        resolveFrameId("tool", responseIndex, index, id, toolCallIdsByIndex)

    private fun resolveFrameId(
        type: String,
        responseIndex: Int,
        frameIndex: Int?,
        frameId: String?,
        idsByIndex: MutableMap<Int, String>,
    ): String {
        if (frameIndex == null) return "$type-$responseIndex-${frameId ?: "unknown"}"
        val existing = idsByIndex[frameIndex]
        if (existing != null) return existing
        return "$type-$responseIndex-${frameId ?: frameIndex}"
            .also { idsByIndex[frameIndex] = it }
    }

    private fun putOutput(id: String, output: AgentOutput): Boolean {
        if (outputsById[id] == output) return false
        outputsById[id] = output
        return true
    }
}

private fun MutableMap<String, Instant>.getOrCreate(id: String): Instant {
    return getOrPut(id) { Clock.System.now() }
}

private data class ToolCallState(
    val id: String,
    val name: String = "",
    val arguments: String = "",
    val status: ToolStatus = ToolStatus.Running,
    val createAt: Instant,
) {

    fun toAgentOutput(): AgentOutput.ToolCall {
        return AgentOutput.ToolCall(
            id = id,
            name = name,
            arguments = arguments,
            output = "",
            status = status,
            createAt = createAt,
        )
    }
}

private fun AgentSteamFrameUiModel.TextDelta.assistantId(responseIndex: Int): String =
    "assistant-$responseIndex-${index ?: 0}"

private fun AgentSteamFrameUiModel.TextComplete.assistantId(responseIndex: Int): String =
    "assistant-$responseIndex-${index ?: 0}"

private fun Map<String, String>.orEmpty(key: String): String {
    return this[key].orEmpty()
}

private fun String.mergeDelta(delta: String?): String {
    if (delta.isNullOrEmpty()) return this
    return when {
        delta == this -> this
        delta.startsWith(this) -> delta
        endsWith(delta) -> this
        else -> this + delta
    }
}
