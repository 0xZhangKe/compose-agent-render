package com.zhangke.compose.chat.demo.agent

import com.zhangke.compose.agent.render.model.AgentOutput
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val prompt = System.getenv("DEMO_AGENT_PROMPT")
        ?: "Reply with one short sentence confirming that Koog is wired correctly."
    DemoAgent().stream(prompt).collect { output ->
        when (output) {
            is AgentOutput.AssistantText -> print(output.content)
            is AgentOutput.Reasoning -> print(output.content)
            is AgentOutput.ToolCall -> println("\n[tool:${output.status}] ${output.name} ${output.arguments}")
        }
    }
    println()
}
