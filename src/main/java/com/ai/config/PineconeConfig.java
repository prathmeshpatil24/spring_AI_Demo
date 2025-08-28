package com.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.pinecone.clients.Index;
import io.pinecone.clients.Pinecone;

//Config for Pinecone store

//@Configuration
//public class PineconeConfig {
//
//	 private static final String API_KEY = "YOUR_API_KEY";
//	    private static final String INDEX_NAME = "docs-example";
//	    private static final String NAMESPACE = "example-namespace";
//
//	    @Bean
//	    public Pinecone pineconeClient() {
//	        return new Pinecone.Builder(API_KEY).build();
//	    }
//
//	    @Bean
//	    public Index pineconeIndex(Pinecone pineconeClient) {
//	        return pineconeClient.getIndexConnection(INDEX_NAME);
//	    }
//
//	    @Bean
//	    public String pineconeNamespace() {
//	        return NAMESPACE;
//	    }
//	
//}
