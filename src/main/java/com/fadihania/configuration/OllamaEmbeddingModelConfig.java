package com.fadihania.configuration;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;

public class OllamaEmbeddingModelConfig {

    private String url;
    private String model;

    public OllamaEmbeddingModelConfig(String ollamaBaseUrl, String embeddingModel) {
        this.url = ollamaBaseUrl;
        this.model = embeddingModel;
    }

    public EmbeddingModel build() {
        return OllamaEmbeddingModel.builder()
                .baseUrl(url)
                .modelName(model)
                .build();
    }

}
