package com.ai.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ai.controller.ChatController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ai.util.CustomMessageConveter;

// Handles RAG queries with Ollama + Pinecone

@Service
public class ChatService {
	
    private final ChatMemory chatMemory;
    private final CustomMessageConveter messageConveter;
	private final ChatClient ollamaChatClient;
	private final VectorStore vectorStore;
	
	public ChatService(
    @Qualifier("ollamaChatModel")ChatClient ollamaChatClient,
            ChatMemory chatMemory,
            CustomMessageConveter messageConveter,
            VectorStore vectorStore
    )
    {
        this.ollamaChatClient = ollamaChatClient;
        this.chatMemory = chatMemory;
        this.messageConveter = messageConveter;
        this.vectorStore = vectorStore;

	}

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

	private static String sanitizeInput(String input) {
		String[] forbiddenPatterns= {
				 "ignore previous instructions",
		            "forget your rules",
		            "do anything",
		            "tell me secret",
		            "password",
		            "key",
		            "admin"
		};
		
		 String sanitized = input.toLowerCase();
		    for (String pattern : forbiddenPatterns) {
		        sanitized = sanitized.replace(pattern, " ");
		    }
		    return sanitized;
		
	}
	
	private static boolean isResponseSafe(String response) {
	    String[] forbiddenWords = {"password", "key", "secret", "admin"};
	    for (String word : forbiddenWords) {
	        if (response.toLowerCase().contains(word)) {
	            return false;
	        }
	    }
	    return true;
	}

	public String getAnswer(String query) {

        if (query.equalsIgnoreCase("Hii")) {
            return "Hello! How can I assist you today?";
        } else if (query.equalsIgnoreCase("Hello")) {
            return "Hi! How can I assist you today?";
        } else {
            // Step 1: Sanitize user query
            String safeQuery = sanitizeInput(query);

            // step 2: to maintain chat history
            List<Message> conversationHistory = chatMemory.get("conversation1");

            // Step 3: Search Pinecone VectorStore with sanitized query
            // Retrieve documents similar to a query
            List<Document> results = this.vectorStore
                    .similaritySearch
                            (SearchRequest.builder()
                                    .query(safeQuery)
                                    .topK(8)// fetch top 2 similar docs
                                    .similarityThreshold(0.6) // filter by similarity and ignore low- score matches
                                    .build()
                            );
//		Check retrieval results before calling LLM
//		check for data in docs result
//		if (results == null || results.isEmpty()) {
//		    return "I could not find relevant information in the documents for your question.";
//		}

            // Step 4: Convert results into readable Strings
            String context = results.stream()
                    .map(doc -> "Text: " + doc.getText() +
                            "\nMetadata: " + doc.getMetadata())
                    .collect(Collectors.joining("\n--\n"));
//        System.out.println(context);

            // Step 5: Build a proper prompt for the LLM
//		this will give ans only if query is related to context otherwise it will say no relevant info
            String userPrompt = "You are an AI assistant. Follow these strict instructions:\n" +
                    "1. ONLY answer based on the provided context.\n" +
                    "2. Do NOT use any outside knowledge or assumptions.\n" +
                    "3. If the context does not contain the answer, respond exactly with: " +
                    "'Sorry, I could not find relevant information in the documents.'\n" +
                    "4. Keep your answer concise, clear, and factual.\n" +
                    "=== Context Start ===\n" + context + "\n=== Context End ===\n\n" +
                    "Question: " + query + "\n" +
                    "Answer:";

            // Step 6: Ask the LLM with retrieved context
            String formating = """
                    You are a helpful assistant.
                    Only use the provided context to answer the question.
                    Do not use outside knowledge.
                    If the answer is not in the context, say:
                    "Sorry, I could not find relevant information in the documents."
                    
                    When answering:
                    - Present the information in a clear and structured way.
                    - Use bullet points, numbered lists, or tables if applicable.
                    - Highlight key terms in **bold**.
                    - Keep explanations concise and easy to read.
                    """;
            ChatResponse chatResponse = ollamaChatClient
                    .prompt()
                    .system(formating) // 1. Inject system instructions (rules/guidelines)
                    .messages(conversationHistory)// 2. Add all past user+assistant turns
                    .user(userPrompt)  // 3. Add the new user query
                    .call() // 4. Trigger the model call
                    .chatResponse(); // 5. Get the structured response


            String readableResponse = chatResponse.getResult().getOutput().getText();

            // Step 5: Post-filter the AI response
            if (!isResponseSafe(readableResponse)) {
                return "Sorry, I cannot provide this information.";
            }

            // Step 7: Store turn in memory
            chatMemory.add("conversation1", new UserMessage(safeQuery));
            chatMemory.add("conversation1", new AssistantMessage(readableResponse));
//      step 8: giving proper response
//        System.out.println("response from ai model:-" + readableResponse);
            return readableResponse;
        }
    }

}
