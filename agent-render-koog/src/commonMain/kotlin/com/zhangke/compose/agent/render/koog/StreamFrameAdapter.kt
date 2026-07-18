package com.zhangke.compose.agent.render.koog

import ai.koog.prompt.streaming.StreamFrame
import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.model.ToolStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlin.time.Instant

fun <T> Flow<AgentAdapterFrame<T>>.reduceToAgentOutput(
    customAdapter: (frame: T) -> AgentOutput.Custom<T>? = { null },
): Flow<List<AgentOutput<T>>> {
    return flow {
        val reducer = StreamFrameReducer(customAdapter)
        this@reduceToAgentOutput.collect { frame ->
            if (reducer.reduce(frame)) {
                emit(reducer.outputs)
            }
        }
    }
}

sealed interface AgentAdapterFrame<T> {

    data class LlmFrame<T>(val frame: StreamFrame) : AgentAdapterFrame<T>

    data class CustomFrame<T>(val frame: T) : AgentAdapterFrame<T>
}

private class StreamFrameReducer<T>(
    private val customAdapter: (frame: T) -> AgentOutput.Custom<T>?,
) {

    private val outputsById = linkedMapOf<String, AgentOutput<T>>()
    private val textById = mutableMapOf<String, String>()
    private val reasoningById = mutableMapOf<String, String>()
    private val toolCallsById = mutableMapOf<String, ToolCallState<T>>()
    private val createAtById = mutableMapOf<String, Instant>()
    private val reasoningIdsByIndex = mutableMapOf<Int, String>()
    private val toolCallIdsByIndex = mutableMapOf<Int, String>()
    private var responseIndex = 0

    val outputs: List<AgentOutput<T>>
        get() = outputsById.values.toList()

    fun reduce(frame: AgentAdapterFrame<T>): Boolean {
        return when (frame) {
            is AgentAdapterFrame.LlmFrame<T> -> {
                when (frame.frame) {
                    is StreamFrame.TextDelta -> reduceTextDelta(frame.frame)
                    is StreamFrame.TextComplete -> reduceTextComplete(frame.frame)
                    is StreamFrame.ReasoningDelta -> reduceReasoningDelta(frame.frame)
                    is StreamFrame.ReasoningComplete -> reduceReasoningComplete(frame.frame)
                    is StreamFrame.ToolCallDelta -> reduceToolCallDelta(frame.frame)
                    is StreamFrame.ToolCallComplete -> reduceToolCallComplete(frame.frame)
                    is StreamFrame.End -> {
                        responseIndex++
                        reasoningIdsByIndex.clear()
                        toolCallIdsByIndex.clear()
                        false
                    }
                }
            }

            is AgentAdapterFrame.CustomFrame -> {
                val customOutput = customAdapter(frame.frame)
                if (customOutput == null) {
                    false
                } else {
                    putOutput(customOutput)
                }
            }
        }
    }

    private fun reduceTextDelta(frame: StreamFrame.TextDelta): Boolean {
        val id = frame.assistantId(responseIndex)
        val content = textById.orEmpty(id).mergeDelta(frame.text)
        textById[id] = content
        return putOutput(
            AgentOutput.AssistantText(
                id = id,
                content = content,
                createAt = createAtById.getOrCreate(id),
                completed = false,
            ),
        )
    }

    private fun reduceTextComplete(frame: StreamFrame.TextComplete): Boolean {
        val id = frame.assistantId(responseIndex)
        val changed = putOutput(AgentOutput.AssistantText(
            id = id,
            content = frame.text,
            createAt = createAtById.getOrCreate(id),
            completed = true,
        ))
        textById.remove(id)
        createAtById.remove(id)
        return changed
    }

    private fun reduceReasoningDelta(frame: StreamFrame.ReasoningDelta): Boolean {
        val delta = frame.summary ?: frame.text ?: return false
        val id = frame.reasoningId(responseIndex)
        val content = reasoningById.orEmpty(id) + delta
        reasoningById[id] = content
        return putOutput(AgentOutput.Reasoning(
            id = id,
            content = content,
            createAt = createAtById.getOrCreate(id),
        ))
    }

    private fun reduceReasoningComplete(frame: StreamFrame.ReasoningComplete): Boolean {
        val id = frame.reasoningId(responseIndex)
        val content = frame.summary?.joinToString(separator = "")
            ?: frame.content.joinToString(separator = "")
        reasoningById[id] = content
        val changed = putOutput(AgentOutput.Reasoning(
            id = id,
            content = content,
            createAt = createAtById.getOrCreate(id),
        ))
        reasoningById.remove(id)
        createAtById.remove(id)
        return changed
    }

    private fun reduceToolCallDelta(frame: StreamFrame.ToolCallDelta): Boolean {
        val id = frame.toolCallId(responseIndex)
        val current = toolCallsById[id] ?: ToolCallState<T>(
            id = id,
            createAt = createAtById.getOrCreate(id),
        )
        val next = current.copy(
            name = frame.name ?: current.name,
            arguments = current.arguments.mergeDelta(frame.content),
            status = ToolStatus.Running,
        )
        toolCallsById[id] = next
        return putOutput(next.toAgentOutput())
    }

    private fun reduceToolCallComplete(frame: StreamFrame.ToolCallComplete): Boolean {
        val id = frame.toolCallId(responseIndex)
        val current = toolCallsById[id] ?: ToolCallState<T>(
            id = id,
            createAt = createAtById.getOrCreate(id),
        )
        val next = current.copy(
            name = frame.name,
            arguments = frame.content,
            status = ToolStatus.Success,
        )
        toolCallsById[id] = next
        val changed = putOutput(next.toAgentOutput())
        toolCallsById.remove(id)
        createAtById.remove(id)
        return changed
    }

    private fun StreamFrame.ReasoningDelta.reasoningId(responseIndex: Int): String =
        resolveFrameId("reasoning", responseIndex, index, id, reasoningIdsByIndex)

    private fun StreamFrame.ReasoningComplete.reasoningId(responseIndex: Int): String =
        resolveFrameId("reasoning", responseIndex, index, id, reasoningIdsByIndex)

    private fun StreamFrame.ToolCallDelta.toolCallId(responseIndex: Int): String =
        resolveFrameId("tool", responseIndex, index, id, toolCallIdsByIndex)

    private fun StreamFrame.ToolCallComplete.toolCallId(responseIndex: Int): String =
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

    private fun putOutput(output: AgentOutput<T>): Boolean {
        if (outputsById[output.id] == output) return false
        outputsById[output.id] = output
        return true
    }
}

private fun MutableMap<String, Instant>.getOrCreate(id: String): Instant {
    return getOrPut(id) { Clock.System.now() }
}

private data class ToolCallState<T>(
    val id: String,
    val name: String = "",
    val arguments: String = "",
    val status: ToolStatus = ToolStatus.Running,
    val createAt: Instant,
) {

    fun toAgentOutput(): AgentOutput.ToolCall<T> {
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

private fun StreamFrame.TextDelta.assistantId(responseIndex: Int): String =
    "assistant-$responseIndex-${index ?: 0}"

private fun StreamFrame.TextComplete.assistantId(responseIndex: Int): String =
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
