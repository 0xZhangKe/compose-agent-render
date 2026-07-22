package com.zhangke.compose.chat.demo.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.compose.agent.render.composable.ProcessingText

@Composable
fun ProcessingTextDemoScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(top = 64.dp),
    ) {
        ProcessingText(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            text = "Processing text, Processing text, Processing text, Processing text, please wait...",
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
