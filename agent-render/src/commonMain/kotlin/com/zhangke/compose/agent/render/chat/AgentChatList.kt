package com.zhangke.compose.agent.render.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.AgentOutput
import com.zhangke.compose.agent.render.model.AgentChatMessage
import com.zhangke.compose.agent.render.model.AgentOutputMessageState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration.Companion.milliseconds

private val BottomReachedThreshold = 100.dp

@OptIn(FlowPreview::class)
@Composable
fun AgentChatList(
    modifier: Modifier,
    messageList: List<AgentChatMessage>,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val density = LocalDensity.current
    val bottomThresholdPx = with(density) { BottomReachedThreshold.toPx() }
    val bottomContentPaddingPx = with(density) { contentPadding.calculateBottomPadding().toPx() }
    var isAtBottom by remember { mutableStateOf(true) }

    LaunchedEffect(listState, bottomContentPaddingPx, bottomThresholdPx) {
        var lastItemHeight = 0
        snapshotFlow { listState.layoutInfo }
            .debounce(100.milliseconds)
            .collect { layoutInfo ->
                println("layoutInfo: $layoutInfo")
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                if (lastVisibleItem != null) {
                    if (lastItemHeight != lastVisibleItem.size && isAtBottom) {
                        listState.scrollToBottom()
                        isAtBottom = true
                    } else {
                        val realBottom = layoutInfo.viewportEndOffset - bottomContentPaddingPx
                        val lastItemBottom = lastVisibleItem.offset + lastVisibleItem.size
                        isAtBottom = lastItemBottom - realBottom < bottomThresholdPx
                    }
                }
                lastItemHeight = lastVisibleItem?.size ?: 0
                println("is at bottom: $isAtBottom")
            }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
    ) {
        itemsIndexed(messageList) { index, message ->
            when (message) {
                is AgentChatMessage.AgentOutputMessage -> {
                    AgentOutput(
                        modifier = Modifier.fillMaxWidth(),
                        outputList = message.outputList,
                        completed = message.state is AgentOutputMessageState.Completed || message.state is AgentOutputMessageState.Error,
                    )
                }

                is AgentChatMessage.HumanInputMessage -> {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier.align(Alignment.CenterEnd)
                                .widthIn(max = maxWidth * 0.75F),
                        ) {
                            AgentHumanInput(
                                modifier = Modifier,
                                input = message,
                                state = message.state,
                            )
                        }
                    }
                }
            }
            if (index < messageList.lastIndex) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private suspend fun LazyListState.scrollToBottom() {
    val lastIndex = layoutInfo.totalItemsCount - 1
    if (lastIndex < 0) return
    scrollToItem(index = lastIndex, scrollOffset = Int.MAX_VALUE)
}
