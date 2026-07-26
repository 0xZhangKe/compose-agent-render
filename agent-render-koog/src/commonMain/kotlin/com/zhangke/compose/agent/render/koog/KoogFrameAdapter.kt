package com.zhangke.compose.agent.render.koog

import ai.koog.prompt.streaming.StreamFrame
import com.zhangke.compose.agent.render.model.AgentSteamFrameUiModel

fun StreamFrame.toUiModelFrame(): AgentSteamFrameUiModel {
    return when (this) {
        is StreamFrame.ToolCallDelta -> {
            AgentSteamFrameUiModel.ToolCallDelta(
                id = this.id,
                name = this.name,
                content = this.content,
                index = this.index,
            )
        }

        is StreamFrame.ToolCallComplete -> {
            AgentSteamFrameUiModel.ToolCallComplete(
                id = this.id,
                name = this.name,
                content = this.content,
                index = this.index,
            )
        }

        is StreamFrame.ReasoningDelta -> {
            AgentSteamFrameUiModel.ReasoningDelta(
                id = this.id,
                text = this.text,
                summary = this.summary,
                index = this.index,
            )
        }

        is StreamFrame.ReasoningComplete -> {
            AgentSteamFrameUiModel.ReasoningComplete(
                id = this.id,
                content = this.content,
                summary = this.summary,
                encrypted = this.encrypted,
                index = this.index,
            )
        }

        is StreamFrame.TextDelta -> {
            AgentSteamFrameUiModel.TextDelta(
                text = this.text,
                index = this.index,
            )
        }

        is StreamFrame.TextComplete -> {
            AgentSteamFrameUiModel.TextComplete(
                text = this.text,
                index = this.index,
            )
        }

        is StreamFrame.End -> {
            AgentSteamFrameUiModel.End(
                finishReason = this.finishReason,
            )
        }
    }
}
