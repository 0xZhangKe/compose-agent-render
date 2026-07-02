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
                    is StreamFrame.End -> false
                }
            }

            is AgentAdapterFrame.CustomFrame -> {
                val customOutput = customAdapter(frame.frame)
                if (customOutput == null) {
                    false
                } else {
                    outputsById[customOutput.id] = customOutput
                    true
                }
            }
        }
    }

    private fun reduceTextDelta(frame: StreamFrame.TextDelta): Boolean {
        val id = frame.assistantId
        val content = textById.orEmpty(id) + frame.text
        textById[id] = content
        outputsById[id] = AgentOutput.AssistantText(
            id = id,
            content = content,
            createAt = createAtById.getOrCreate(id),
        )
        return true
    }

    private fun reduceTextComplete(frame: StreamFrame.TextComplete): Boolean {
        val id = frame.assistantId
        textById[id] = frame.text
        outputsById[id] = AgentOutput.AssistantText(
            id = id,
            content = frame.text,
            createAt = createAtById.getOrCreate(id),
        )
        textById.remove(id)
        createAtById.remove(id)
        return true
    }

    private fun reduceReasoningDelta(frame: StreamFrame.ReasoningDelta): Boolean {
        val delta = frame.summary ?: frame.text ?: return false
        val id = frame.reasoningId
        val content = reasoningById.orEmpty(id) + delta
        reasoningById[id] = content
        outputsById[id] = AgentOutput.Reasoning(
            id = id,
            content = content,
            createAt = createAtById.getOrCreate(id),
        )
        return true
    }

    private fun reduceReasoningComplete(frame: StreamFrame.ReasoningComplete): Boolean {
        val id = frame.reasoningId
        val content = frame.summary?.joinToString(separator = "")
            ?: frame.text.joinToString(separator = "")
        reasoningById[id] = content
        outputsById[id] = AgentOutput.Reasoning(
            id = id,
            content = content,
            createAt = createAtById.getOrCreate(id),
        )
        reasoningById.remove(id)
        createAtById.remove(id)
        return true
    }

    private fun reduceToolCallDelta(frame: StreamFrame.ToolCallDelta): Boolean {
        val id = frame.toolCallId
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
        outputsById[id] = next.toAgentOutput()
        return true
    }

    private fun reduceToolCallComplete(frame: StreamFrame.ToolCallComplete): Boolean {
        val id = frame.toolCallId
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
        outputsById[id] = next.toAgentOutput()
        toolCallsById.remove(id)
        createAtById.remove(id)
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

private val StreamFrame.TextDelta.assistantId: String
    get() = "assistant-${index ?: 0}"

private val StreamFrame.TextComplete.assistantId: String
    get() = "assistant-${index ?: 0}"

private val StreamFrame.ReasoningDelta.reasoningId: String
    get() = "reasoning-${id ?: index ?: 0}"

private val StreamFrame.ReasoningComplete.reasoningId: String
    get() = "reasoning-${id ?: index ?: 0}"

private val StreamFrame.ToolCallDelta.toolCallId: String
    get() = "tool-${id ?: index ?: 0}"

private val StreamFrame.ToolCallComplete.toolCallId: String
    get() = "tool-${id ?: index ?: 0}"

private fun Map<String, String>.orEmpty(key: String): String {
    return this[key].orEmpty()
}

private fun String.mergeDelta(delta: String?): String {
    if (delta.isNullOrEmpty()) return this
    return if (delta.startsWith(this)) {
        delta
    } else {
        this + delta
    }
}
