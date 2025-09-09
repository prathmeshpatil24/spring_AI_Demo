package com.ai.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ai.controller.ChatController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.ai.util.CustomMessageConveter;
import reactor.core.publisher.Flux;

// Handles RAG queries with Ollama + Pinecone

@Service
public class ChatService {
    private final ChatMemory chatMemory;
	private final ChatClient ollamaChatClient;
	private final VectorStore vectorStore;
//    @Value("classpath:/prompts/user-message.st")
//    private Resource userMessage;
//
//    @Value("classpath:/prompts/system-message.st")
//    private Resource systemPrompt;
	
	public ChatService(
    @Qualifier("ollamaChatModel")ChatClient ollamaChatClient,
            ChatMemory chatMemory,
            VectorStore vectorStore
    )
    {
        this.ollamaChatClient = ollamaChatClient;
        this.chatMemory = chatMemory;
        this.vectorStore = vectorStore;
	}

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

	private  String sanitizeInput(String input) {
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
	
	private boolean isResponseSafe(String response) {
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
                                    .topK(5)// fetch top 2 similar docs
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
//            String userPrompt = "You are a custom AI assistant. Follow these strict instructions:\n" +
//                    "1. ONLY answer based on the provided context.\n" +
//                    "2. Do NOT use any outside knowledge or assumptions.\n" +
//                    "3. If the context does not contain the answer, respond exactly with: " +
//                    "'Sorry, I could not find relevant information in the documents.'\n" +
//                    "=== Context Start ===\n" + context + "\n=== Context End ===\n\n" +
//                    "Question: " + query + "\n" +
//                    "Answer:";

            String userPrompt =
                    "Context:\n" + context + "\n\n" +
                            "Q: " + query + "\n" +
                            "A:";

            // Step 6: Ask the LLM with retrieved context
//            String systemMessage  = """
//                    You are a custom AI assistant.
//                    - Present the information in a clear and structured way.
//                    - Use bullet points, numbered lists, or tables if applicable.
//                    - Highlight key terms in **bold**.
//                    - Keep explanations concise,clear,factual and easy to read.
//                    """;
            //  Saves ~40–50 tokens.
            String systemMessage = """
                                  You are a project assistant.
                                  Answer from context only.
                                  Respond in clear, structured bullet points.
                                  Say "No relevant info found." if answer missing.
                                  """;

            ChatResponse chatResponse = ollamaChatClient
                    .prompt()
                    .system(systemMessage ) // 1. Inject system instructions (rules/guidelines)
                    .messages(conversationHistory)// 2. Add all past user+assistant turns
//                    .user(promptUserSpec ->promptUserSpec.text(this.userMessage).param("context", context).param("query", query))
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

//    flux stream demo
    public Flux<String>streamChat(String q){

        Flux<String> content = ollamaChatClient
                .prompt()
                .system("you are helpful coding assistance, explain the concept in details")
                .user(q)
                .stream()
                .content();

       return content;
    }

    public Flux<String> getAnswerWithStream(String query) {
        if (query.equalsIgnoreCase("Hii")) {
            return Flux.just("Hello! How can I assist you today?");
        } else if (query.equalsIgnoreCase("Hello")) {
            return Flux.just("Hi! How can I assist you today?");
        } else {
            // Step 1: Sanitize user query
            String safeQuery = sanitizeInput(query);

            // Step 2: Maintain chat history
            List<Message> conversationHistory = chatMemory.get("conversation1");

            // Step 3: Search Pinecone VectorStore with sanitized query
            List<Document> results = this.vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(safeQuery)
                            .topK(8)
                            .similarityThreshold(0.6)
                            .build()
            );

            // Step 4: Convert results into readable Strings
            String context = results.stream()
                    .map(doc -> "Text: " + doc.getText() +
                            "\nMetadata: " + doc.getMetadata())
                    .collect(Collectors.joining("\n--\n"));

            // Step 5: Build user prompt
            String userPrompt =
                    "Context:\n" + context + "\n\n" +
                            "Q: " + query + "\n" +
                            "A:";

            // Step 6: System message (short version to save tokens)
            String systemMessage = """
                You are a project assistant.
                Answer from context only.
                Respond in clear, structured bullet points.
                Say "No relevant info found." if answer missing.
                """;

            // Step 7: Stream response from LLM
            Flux<String> streamResponse = ollamaChatClient
                    .prompt()
                    .system(systemMessage)
                    .messages(conversationHistory)
                    .user(userPrompt)
                    .stream()
                    .content();

            // Step 5: Post-filter the AI response
            if (!isResponseSafe(String.valueOf(streamResponse))) {
                return Flux.just("Sorry, I cannot provide this information.");
            }

            chatMemory.add("conversation1", new UserMessage(safeQuery));
            chatMemory.add("conversation1", new AssistantMessage(String.valueOf(streamResponse)));

            return streamResponse;
        }
    }
}
