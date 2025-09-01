package com.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.model.ollama.autoconfigure.OllamaConnectionDetails;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Config for Ollama model

@Configuration
public class OllamaConfig {


//	bean for chat memory
	@Bean
    public ChatMemory chatMemory() {
        // In Spring AI 1.0.1, use MessageWindowChatMemory
        return MessageWindowChatMemory.builder()
                .maxMessages(50)
                .build();
    }

// OllamaChatModel bean definition
   @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel) {
            //Shortcut / convenience factory method.
          //Meant for simple use cases where you just need a client with defaults.
       //	   return ChatClient.create(ollamaChatModel);

         //Builder pattern → gives you flexibility to configure more options.
         //You can add middlewares, configure message converters, interceptors, logging, etc.
          return ChatClient.builder(ollamaChatModel)
                  .defaultSystem("You are a custom ai assistant for one particular project.Always respond with structured JSON.")
                  .defaultUser("How can I help you today?")
                  .build();

    }
}
