package com.ai.service;


import java.util.List;

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

	public EmbeddingService(EmbeddingModel embeddingModel, VectorStore vectorStore) {
		this.embeddingModel = embeddingModel;
		this.vectorStore = vectorStore;
	}

//	for string embedding
	public float[] getEmbedding(String text) {
//		for testing purpose
		float[] response = embeddingModel.embed(text);
		for(float f: response) {
			System.out.println(f);
		}
		
		return embeddingModel.embed(text);
	}

//	for list of String embedding
	public List<float[]> getEmbeddingList(List<String> texts) {
		// embeddingModel.embed(texts);
		// Call embedding API for multiple strings
		EmbeddingResponse response = embeddingModel.embedForResponse(texts);

		// Extract all embeddings (one per string)
		return response.getResults().stream().map(r -> r.getOutput()) // each embedding is float[]
				.toList();

	}

//	for Document embedding
	public float[] getEmbedding(Document text) {
		return embeddingModel.embed(text);
	}
	
	
//	for list of document embedding
	public void embedDocument(List<Document>documents) {
		 // Store documents in VectorStore
	    vectorStore.add(documents);
	    System.out.println("Added " + documents.size() + " docs into Vector DB");
	    
	 // Pick the first doc’s text
	    String checkText = documents.get(0).getText();
	    
	    List<Document> results = this.vectorStore.similaritySearch(SearchRequest.builder().query(checkText).topK(1).build());
	    
	    System.out.println("Verification Results: " + results.size() + "docs found");
	  
	}
	

////	for list of document embedding
//	public void embedDocument(List<Document> documents, EmbeddingOptions options, BatchingStrategy batchingStrategy) {
//// Call the embedding model
//		List<float[]> embeddings = embeddingModel.embed(documents, options, batchingStrategy);
//
//// For now, just print them
//		for (int i = 0; i < embeddings.size(); i++) {
//			System.out.println("Document " + (i + 1) + " embedding: " + Arrays.toString(embeddings.get(i)));
//		}
//
//        // TODO: Later you can store these embeddings in a Vector DB (like Pinecone, Weaviate, etc.)
//	
//		
//	}
	
	


}
