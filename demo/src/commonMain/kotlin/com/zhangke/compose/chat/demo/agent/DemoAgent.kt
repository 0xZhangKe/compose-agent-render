package com.zhangke.compose.chat.demo.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.GraphAIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import com.zhangke.compose.agent.render.model.AgentOutput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DemoAgent(
    private val agent: GraphAIAgent<String, String> = buildOpenRouterAgent(),
) {

    fun stream(message: String): Flow<AgentOutput> = flow {
        val config = agent.agentConfig
        val currentPrompt = prompt(config.prompt) {
            user {
                text(message)
            }
        }
        val toolDescriptors = agent.toolRegistry.tools.map { it.descriptor }

        agent.promptExecutor.executeStreaming(
            prompt = currentPrompt,
            model = config.model,
            tools = toolDescriptors,
        ).collect { frame ->
            frame.toAgentOutput()?.let { output ->
                emit(output)
            }
        }
    }

    suspend fun ask(message: String): String {
        val text = StringBuilder()
        stream(message).collect { output ->
            if (output is AgentOutput.AssistantText) {
                text.append(output.content)
            }
        }
        return text.toString()
    }
}

private fun buildOpenRouterAgent(): GraphAIAgent<String, String> {
    val client = OpenAILLMClient(
        apiKey = LLMConfigs.openRouter,
        settings = OpenAIClientSettings(
            baseUrl = "https://openrouter.ai/api",
            chatCompletionsPath = "v1/chat/completions",
            modelsPath = "v1/models",
        ),
    )

    return AIAgent(
        promptExecutor = MultiLLMPromptExecutor(client),
        systemPrompt = "You are a concise assistant for the Compose AI Chat demo.",
        llmModel = LLModel(
            provider = LLMProvider.OpenAI,
            id = "qwen/qwen3.7-plus",
            capabilities = listOf(
                LLMCapability.Completion,
                LLMCapability.OpenAIEndpoint.Completions,
            ),
        ),
        temperature = 0.4,
        toolRegistry = ToolRegistry.EMPTY,
        maxIterations = 3,
    ) as GraphAIAgent<String, String>
}
