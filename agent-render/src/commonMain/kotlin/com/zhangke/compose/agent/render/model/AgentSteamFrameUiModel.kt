package com.zhangke.compose.agent.render.model

/**
 * A frame emitted while an agent response is being produced.
 *
 * This interface is intentionally not sealed so applications can define their own frame types.
 */
interface AgentSteamFrameUiModel {

    /**
     * A frame containing the complete value of one response part.
     */
    interface CompleteFrame : AgentSteamFrameUiModel {
        val index: Int?
    }

    /**
     * A frame containing an incremental value of one response part.
     */
    interface DeltaFrame : AgentSteamFrameUiModel {
        val index: Int?
    }

    data class TextDelta(
        val text: String,
        override val index: Int? = null,
    ) : DeltaFrame

    data class TextComplete(
        val text: String,
        override val index: Int? = null,
    ) : CompleteFrame

    data class ReasoningDelta(
        val id: String? = null,
        val text: String? = null,
        val summary: String? = null,
        override val index: Int? = null,
    ) : DeltaFrame

    data class ReasoningComplete(
        val id: String?,
        val content: List<String>,
        val summary: List<String>? = null,
        val encrypted: String? = null,
        override val index: Int? = null,
    ) : CompleteFrame

    data class ToolCallDelta(
        val id: String?,
        val name: String?,
        val content: String?,
        override val index: Int? = null,
    ) : DeltaFrame

    data class ToolCallComplete(
        val id: String?,
        val name: String,
        val content: String,
        override val index: Int? = null,
    ) : CompleteFrame

    data class End(
        val finishReason: String? = null,
    ) : AgentSteamFrameUiModel
}
