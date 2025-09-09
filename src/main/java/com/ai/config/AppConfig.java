package com.ai.config;

import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
	
	 @Bean
	    public RestTemplate restTemplate() {
	        return new RestTemplate();
	    }



	 
	 


}
