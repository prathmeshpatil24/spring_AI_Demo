package com.ai.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    public EmbeddingService(EmbeddingModel embeddingModel,
                            VectorStore vectorStore) {
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
    }

    private static final Logger logger = LoggerFactory.getLogger(EmbeddingService.class);

//    ================== for embedding testing =========================
    //	for string embedding
//    for testing purpose
    public  void testEmbeddingDocument(List<Document> documents){
        for (int i = 0; i < documents.size() ; i++) {
            float[] embedded = embeddingModel.embed(documents.get(i));
            System.out.println("Embedding for Document " + i + ": " + Arrays.toString(embedded));
        }
        System.out.println("Embedding data complete");
    }

//    =================== for vector store =========================
    //	for list of document embedding
    public void embedDocument(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            System.out.println("No documents to embed.");
            return;
        }

        // Filter out empty docs
        List<Document> filteredDocs = documents.stream()
                .filter(d -> d.getText() != null && !d.getText().isBlank())
                .toList();

        if (filteredDocs.isEmpty()) {
            System.out.println("All documents are empty. Nothing to embed.");
            return;
        }

        try {
            // Batch add documents to VectorStore (Pinecone or SimpleVectorStore)
            vectorStore.add(filteredDocs);
            System.out.println("Added " + filteredDocs.size() + " docs into Vector DB");

            // Optional: verification using topK search on a few sample docs
            int samplesToCheck = Math.min(3, filteredDocs.size());
            for (int i = 0; i < samplesToCheck; i++) {
                Document sampleDoc = filteredDocs.get(i);
                List<Document> results = vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(sampleDoc.getText())
                                .topK(3)   // check top 3 similar docs
                                .build()
                );
                System.out.println("Verification for doc " + i + ": " + results.size() + " docs found");
            }

        } catch (Exception e) {
            System.err.println("Error embedding documents: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
