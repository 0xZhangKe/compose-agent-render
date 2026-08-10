package com.zhangke.compose.agent.render

import com.zhangke.compose.agent.render.model.AgentCompleteMetaDataUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class AgentCompleteMetaDataUiModelTest {

    @Test
    fun keepsDefaultTitleWhenMetadataOrDurationIsMissing() {
        assertEquals("Reasoning Finished", null.toCompleteSummary())
        assertEquals(
            "Reasoning Finished · 120 tokens · 2 tool calls",
            AgentCompleteMetaDataUiModel(tokens = 120, toolcallTimes = 2).toCompleteSummary(),
        )
    }

    @Test
    fun formatsDurationUsingAReadableUnit() {
        assertEquals(
            "Worked for 1.5s",
            AgentCompleteMetaDataUiModel(duration = 1500.milliseconds).toCompleteSummary(),
        )
        assertEquals(
            "Worked for 2min",
            AgentCompleteMetaDataUiModel(duration = 2.minutes).toCompleteSummary(),
        )
        assertEquals(
            "Worked for 1.5 hours",
            AgentCompleteMetaDataUiModel(duration = 1.hours + 30.minutes).toCompleteSummary(),
        )
    }

    @Test
    fun displaysEveryAvailableMetadataValue() {
        assertEquals(
            "Worked for 12s · 2.05K tokens · 1 tool call",
            AgentCompleteMetaDataUiModel(
                tokens = 2048,
                duration = 12.seconds,
                toolcallTimes = 1,
            ).toCompleteSummary(),
        )
    }

    @Test
    fun formatsLargeTokenCountsWithTwoDecimalPlaces() {
        assertEquals(
            "Reasoning Finished · 1.00K tokens",
            AgentCompleteMetaDataUiModel(tokens = 1000).toCompleteSummary(),
        )
        assertEquals(
            "Reasoning Finished · 1.25M tokens",
            AgentCompleteMetaDataUiModel(tokens = 1_250_000).toCompleteSummary(),
        )
        assertEquals(
            "Reasoning Finished · 2.50B tokens",
            AgentCompleteMetaDataUiModel(tokens = 2_500_000_000).toCompleteSummary(),
        )
    }
}
