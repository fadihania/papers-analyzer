package com.fadihania.services;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;

public class DocumentsLoader {

    public static List<Document> parse(String directoryName) throws URISyntaxException {
        URL resourcesUrl = DocumentsLoader.class.getClassLoader().getResource(directoryName);
        Path resourcesPath = Path.of(resourcesUrl.toURI());
        List<Document> papers = FileSystemDocumentLoader.loadDocuments(resourcesPath, new ApachePdfBoxDocumentParser(true));

        return papers;
    }
}
