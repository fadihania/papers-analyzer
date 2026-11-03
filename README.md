# Papers Analyzer

Papers Analyzer is an experimental Java application that lets you ask questions about a collection of research-paper PDFs. It implements a retrieval-augmented generation (RAG) workflow: paper text is extracted, split into paragraph-sized chunks, embedded, stored in a vector database, and retrieved as context for a locally hosted large language model.

The project is currently configured for research on software model matching. It includes sample papers under `src/main/resources/papers` and runs a sample question defined in `Main.java`.

## Functionality

When the application runs, it:
1. Loads and extracts text from every PDF in `src/main/resources/papers`.
2. Connects to Ollama and creates embeddings with `nomic-embed-text`.
3. Connects to Qdrant and creates the `papers_paragraph` collection if it does not already exist.
4. Splits documents by paragraph (300-character segments with a 30-character overlap), embeds them, and stores the vectors in Qdrant when the collection is empty.
5. Retrieves the 10 most relevant chunks for the configured question.
6. Sends the retrieved context to `llama3.1:8b` through Ollama and prints the answer, source PDF filenames, and token usage.
Re-running the application reuses a populated Qdrant collection instead of ingesting the PDFs again.

## Technology

- Java 21
- Maven
- [LangChain4j](https://docs.langchain4j.dev/) for the RAG pipeline and AI-service abstraction
- [Ollama](https://ollama.com/) for local embedding and chat models
- [Qdrant](https://qdrant.tech/) as the vector database
- Apache PDFBox (through LangChain4j) for PDF text extraction
- SLF4J Simple for runtime logging

## Prerequisites

- JDK 21 or newer
- Maven 3.9 or newer
- Ollama running locally at `http://localhost:11434`
- The required Ollama models:

  ```powershell
  ollama pull nomic-embed-text
  ollama pull llama3.1:8b
  ```

- Qdrant available at `localhost:6334` over gRPC. For example, start it with Docker:

  ```powershell
  docker run --name papers-qdrant -p 6333:6333 -p 6334:6334 qdrant/qdrant
  ```


## Configuration

The main runtime settings are constants in `src/main/java/com/fadihania/Main.java`:

| Setting | Current value |
| --- | --- |
| Ollama URL | `http://localhost:11434` |
| Embedding model | `nomic-embed-text` |
| Chat model | `llama3.1:8b` |
| Context window | `131072` |
| Qdrant host / gRPC port | `localhost:6334` |
| Collection | `papers_paragraph` |
| Vector dimensions | `768` |
| Results retrieved per query | `10` |

If you change to an embedding model with a different vector dimension, update `EMBEDDING_MODEL_DIMENSIONS` and use a new Qdrant collection (or recreate the existing one). To force a fresh ingestion after changing the PDFs, remove or recreate the corresponding Qdrant collection.
