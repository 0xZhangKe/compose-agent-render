package com.zhangke.compose.agent.render.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.AgentOutput
import com.zhangke.compose.agent.render.model.AgentChatMessage

@Composable
fun AgentChatList(
    modifier: Modifier,
    messageList: List<AgentChatMessage>,
    completed: Boolean,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
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
                        completed = completed,
                    )
                }

                is AgentChatMessage.HumanInputMessage -> {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AgentHumanInput(
                            modifier = Modifier.fillMaxWidth(0.75F)
                                .align(Alignment.CenterEnd),
                            input = message,
                        )
                    }
                }
            }
        }
    }
}
