package com.zhangke.compose.chat.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.zhangke.compose.chat.demo.screen.ProcessingTextDemoScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "compose agent renderer",
    ) {
//        ChatListScreen()
        ProcessingTextDemoScreen()
    }
}
