package com.zhangke.compose.agent.render.model

import kotlin.time.Duration

data class AgentCompleteMetaDataUiModel(
    val tokens: Long? = null,
    val duration: Duration? = null,
    val toolcallTimes: Int? = null,
)
