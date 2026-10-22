package com.fadihania;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.fadihania.configuration.OllamaEmbeddingModelConfig;
import com.fadihania.configuration.QdrantEmbeddingStoreConfig;
import com.fadihania.services.Assistant;
import com.fadihania.services.DocumentsLoader;
import com.fadihania.services.EmbeddingIngestor;
import com.fadihania.services.OllamaLlmChatModel;
import com.fadihania.services.OllamaLlmChatModel.LlmModel;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.store.embedding.EmbeddingStore;

public class Main {
    // Ollama
    static final String OLLAMA_BASE_URL = "http://localhost:11434";
    // static final String EMBEDDING_MODEL = "bge-m3";
    static final String EMBEDDING_MODEL = "nomic-embed-text";
    static final int CONTEXT_LENGTH = 131072;
    static final int EMBEDDING_MODEL_DIMENSIONS = 768;

    // Qdrant
    static final String EMBEDDING_STORE_HOST = "localhost";
    static final int EMBEDDING_STORE_PORT = 6334;
    // static final String EMBEDDING_STORE_COLLECTION = "papers";
    static final String EMBEDDING_STORE_COLLECTION = "papers_paragraph";
    static final int MAX_RESULTS_PER_QUERY = 10;

    // Papers
    static final String PAPERS_DIRECTORY_NAME = "papers";
    static final int PAPERS_SEGMENT_SIZE = 300;
    static final int PAPERS_OVERLAP_SIZE = 30;

    public static void main(String[] args) throws URISyntaxException, InterruptedException, ExecutionException {
        //* 1) Load papers' documents
        List<Document> papers = DocumentsLoader.parse(PAPERS_DIRECTORY_NAME);

        //* 2) Create embedding model
        OllamaEmbeddingModelConfig ollamaEmbeddingConfig = new OllamaEmbeddingModelConfig(OLLAMA_BASE_URL,
                EMBEDDING_MODEL);
        EmbeddingModel embeddingModel = ollamaEmbeddingConfig.build();

        //* 3) Create embedding store
        QdrantEmbeddingStoreConfig qdrantConfig = new QdrantEmbeddingStoreConfig(EMBEDDING_STORE_HOST,
                EMBEDDING_STORE_PORT, EMBEDDING_MODEL_DIMENSIONS, EMBEDDING_STORE_COLLECTION);
        EmbeddingStore<TextSegment> embeddingStore = qdrantConfig.build();

        //* 4) Embed all documents into vectors and store
        if (!qdrantConfig.hasElements()) {
            EmbeddingIngestor.ingest(papers, PAPERS_SEGMENT_SIZE, PAPERS_OVERLAP_SIZE,
                    embeddingModel,
                    embeddingStore);
        }

        //* 5) Create LLM model
        ChatModel chatModel = new OllamaLlmChatModel(OLLAMA_BASE_URL, CONTEXT_LENGTH).build(LlmModel.LLAMA3_1_8B);

        //* 6) Create assistant
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .contentRetriever(new EmbeddingStoreContentRetriever(embeddingStore, embeddingModel, MAX_RESULTS_PER_QUERY))
                .build();

        //* 7) Query the model using retrieved data
        String query = "What are the types of different matching algorithms?";
        Result<String> answer = assistant.chat(query);

        System.out.println(answer.content());
        System.out.println("==============================================================================\n");
        for (Content source : answer.sources()) {
            // System.out.println("File Name: " + source.metadata());
            System.out.println("File Name: " + source.textSegment().metadata().getString("file_name"));
            System.out.println("==============================================================================");
        }
        System.out.println();
        System.out.println(answer.tokenUsage().totalTokenCount());
    }
}