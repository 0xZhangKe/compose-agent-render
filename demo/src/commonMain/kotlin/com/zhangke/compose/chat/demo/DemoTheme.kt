package com.zhangke.compose.chat.demo

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DemoTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            background = Color.White,
        ),
        typography = MaterialTheme.typography,
        content = content,
    )
}
