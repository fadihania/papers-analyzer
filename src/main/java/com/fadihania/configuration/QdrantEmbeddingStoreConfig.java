package com.fadihania.configuration;

import java.util.concurrent.ExecutionException;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;

public class QdrantEmbeddingStoreConfig {

    private String host;
    private int port;
    private int dimensions;
    private String collection;

    QdrantClient client;

    public QdrantEmbeddingStoreConfig(String host, int port, int dimensions, String collectionName) {
        this.host = host;
        this.port = port;
        this.dimensions = dimensions;
        this.collection = collectionName;

        client = new QdrantClient(QdrantGrpcClient.newBuilder(host, port, false).build());
    }

    public Boolean collectionExists() throws InterruptedException, ExecutionException {
        return client.collectionExistsAsync(collection).get();
    }

    public void createCollection() throws InterruptedException, ExecutionException {
        client.createCollectionAsync(
                collection,
                Collections.VectorParams.newBuilder()
                        .setDistance(Collections.Distance.Cosine)
                        .setSize(dimensions)
                        .build())
                .get();
    }

    public Boolean hasElements() throws InterruptedException, ExecutionException {
        return collectionExists() && client.countAsync(collection).get() > 0;
    }

    public EmbeddingStore<TextSegment> build() throws InterruptedException, ExecutionException {
        if (!collectionExists()) {
            createCollection();
        }

        return QdrantEmbeddingStore.builder()
                .host(host)
                .port(port)
                .collectionName(collection)
                // .client(client)
                .build();
    }

}
