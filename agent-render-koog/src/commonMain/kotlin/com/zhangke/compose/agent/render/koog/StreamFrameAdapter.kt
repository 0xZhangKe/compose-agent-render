package com.zhangke.compose.agent.render.koog

import ai.koog.prompt.streaming.StreamFrame
import com.zhangke.compose.agent.render.model.AgentOutput
import com.zhangke.compose.agent.render.model.ToolStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun Flow<StreamFrame>.reduceToAgentOutput(): Flow<List<AgentOutput>> {
    return flow {
        val reducer = StreamFrameReducer()
        this@reduceToAgentOutput.collect { frame ->
            if (reducer.reduce(frame)) {
                emit(reducer.outputs)
            }
        }
    }
}

private class StreamFrameReducer {

    private val outputsById = linkedMapOf<String, AgentOutput>()
    private val textById = mutableMapOf<String, String>()
    private val reasoningById = mutableMapOf<String, String>()
    private val toolCallsById = mutableMapOf<String, ToolCallState>()

    val outputs: List<AgentOutput>
        get() = outputsById.values.toList()

    fun reduce(frame: StreamFrame): Boolean {
        return when (frame) {
            is StreamFrame.TextDelta -> reduceTextDelta(frame)
            is StreamFrame.TextComplete -> reduceTextComplete(frame)
            is StreamFrame.ReasoningDelta -> reduceReasoningDelta(frame)
            is StreamFrame.ReasoningComplete -> reduceReasoningComplete(frame)
            is StreamFrame.ToolCallDelta -> reduceToolCallDelta(frame)
            is StreamFrame.ToolCallComplete -> reduceToolCallComplete(frame)
            is StreamFrame.End -> false
        }
    }

    private fun reduceTextDelta(frame: StreamFrame.TextDelta): Boolean {
        val id = frame.assistantId
        val content = textById.orEmpty(id) + frame.text
        textById[id] = content
        outputsById[id] = AgentOutput.AssistantText(
            id = id,
            content = content,
        )
        return true
    }

    private fun reduceTextComplete(frame: StreamFrame.TextComplete): Boolean {
        val id = frame.assistantId
        textById[id] = frame.text
        outputsById[id] = AgentOutput.AssistantText(
            id = id,
            content = frame.text,
        )
        textById.remove(id)
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
        )
        reasoningById.remove(id)
        return true
    }

    private fun reduceToolCallDelta(frame: StreamFrame.ToolCallDelta): Boolean {
        val id = frame.toolCallId
        val current = toolCallsById[id] ?: ToolCallState(id = id)
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
        val current = toolCallsById[id] ?: ToolCallState(id = id)
        val next = current.copy(
            name = frame.name,
            arguments = frame.content,
            status = ToolStatus.Success,
        )
        toolCallsById[id] = next
        outputsById[id] = next.toAgentOutput()
        toolCallsById.remove(id)
        return true
    }
}

private data class ToolCallState(
    val id: String,
    val name: String = "",
    val arguments: String = "",
    val status: ToolStatus = ToolStatus.Running,
) {

    fun toAgentOutput(): AgentOutput.ToolCall {
        return AgentOutput.ToolCall(
            id = id,
            name = name,
            arguments = arguments,
            output = "",
            status = status,
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
