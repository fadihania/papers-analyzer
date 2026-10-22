package com.fadihania.services;

import java.util.List;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;

public class EmbeddingIngestor {

    public static void ingest(List<Document> documents, int segmentSize, int overlapSize, EmbeddingModel model,
            EmbeddingStore<TextSegment> store) {
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                // .documentSplitter(DocumentSplitters.recursive(segmentSize, overlapSize))
                .documentSplitter(new DocumentByParagraphSplitter(segmentSize, overlapSize))
                .embeddingModel(model)
                .embeddingStore(store)
                .build();

        ingestor.ingest(documents);
    }

}
