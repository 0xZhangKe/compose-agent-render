package com.zhangke.compose.agent.render.icons

import androidx.compose.ui.graphics.vector.ImageVector

internal val IconChevronDown: ImageVector
    get() {
        if (cachingChevronDown != null) {
            return cachingChevronDown!!
        }
        cachingChevronDown = agentIcon(name = "Filled.ChevronDown") {
            agentPath {
                moveTo(7.41f, 8.59f)
                lineTo(12.0f, 13.17f)
                lineTo(16.59f, 8.59f)
                lineTo(18.0f, 10.0f)
                lineTo(12.0f, 16.0f)
                lineTo(6.0f, 10.0f)
                lineTo(7.41f, 8.59f)
                close()
            }
        }
        return cachingChevronDown!!
    }

internal val IconChevronUp: ImageVector
    get() {
        if (cachingChevronUp != null) {
            return cachingChevronUp!!
        }
        cachingChevronUp = agentIcon(name = "Filled.ChevronUp") {
            agentPath {
                moveTo(7.41f, 15.41f)
                lineTo(12.0f, 10.83f)
                lineTo(16.59f, 15.41f)
                lineTo(18.0f, 14.0f)
                lineTo(12.0f, 8.0f)
                lineTo(6.0f, 14.0f)
                lineTo(7.41f, 15.41f)
                close()
            }
        }
        return cachingChevronUp!!
    }

private var cachingChevronDown: ImageVector? = null

private var cachingChevronUp: ImageVector? = null
