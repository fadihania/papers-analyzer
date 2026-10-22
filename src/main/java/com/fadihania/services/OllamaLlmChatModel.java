package com.fadihania.services;

import java.time.Duration;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaChatRequestParameters;

public class OllamaLlmChatModel {
    private String url;
    private int contextLength;

    public OllamaLlmChatModel(String baseUrl) {
        this(baseUrl, 2048);

    }

    public OllamaLlmChatModel(String baseUrl, int contextLength) {
        url = baseUrl;
        this.contextLength = contextLength;
    }

    public ChatModel build(LlmModel model) {
        return OllamaChatModel.builder()
                .baseUrl(url)
                .modelName(model.getName())
                .maxRetries(2)
                .timeout(Duration.ofMinutes(10))
                .defaultRequestParameters(
                        OllamaChatRequestParameters
                                .builder()
                                .temperature(0.0)
                                .seed(0)
                                .numCtx(contextLength)
                                // .topK(0)
                                // .topP(0.0)
                                .build())
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    public enum LlmModel {
        PHI4("phi4"),
        PHI3_14B("phi3:14b"),
        LLAMA3_1_8B("llama3.1:8b"),
        DEEPSEEK_R1_7B("deepseek-r1:7b"),
        DEEPSEEK_R1_14B("deepseek-r1:14b");

        private final String name;

        LlmModel(String modelName) {
            name = modelName;
        }

        public String getName() {
            return name;
        }
    }
}
