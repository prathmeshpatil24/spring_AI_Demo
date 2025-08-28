package com.ai.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ai.service.ChatService;
import com.ai.util.CustomMessageConveter;

//Endpoints for chat, RAG queries

@RestController
@RequestMapping("/ollama")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final CustomMessageConveter messageConveter;
    private final ChatService chatService;
    private final VectorStore vectorStore;
  

    public ChatController(ChatClient chatClient, ChatMemory chatMemory, CustomMessageConveter messageConveter, ChatService chatService, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.messageConveter = messageConveter;
        this.chatService = chatService;
        this.vectorStore = vectorStore;
       
    }

    @GetMapping("/{chat}")
    public ResponseEntity<String> getAnswer(@PathVariable String chat){
//    	test data is getting or not here
    	System.out.println("Prompt entered by User:-" +  chat);
    	
//    	// Convert to Message
//    	Message message=messageConveter.convertToMessage(chat);
//
//        // add the user message to chat memory
//        chatMemory.add("conversation1",message);
        
        
        // Get full conversation
        List<Message> conversationHistory = chatMemory.get("conversation1");
        chatMemory.add("conversation1", new UserMessage(chat));

		//main logic to get ans from ai model
        ChatResponse chatResponse = chatClient
                .prompt(chat) // 1. Send your prompt (the user’s query / message)
                .messages(conversationHistory) // add  the conversion history
                .system("You are a helpful assistant.") 
                .call() // 2. Execute the call to the model
                .chatResponse(); // 3. Get the structured ChatResponse object

       // print the ai model
        System.out.println(chatResponse.getMetadata().getModel());

        //build the response in string format
        String response = chatResponse.getResult().getOutput().getText();
        // testing response
        System.out.println("Response from Ai model:-" +  response);
        
        // Store AI response in memory
        chatMemory.add("conversation1", new AssistantMessage(response));
        
        return ResponseEntity.ok(response);
    }
    
    
    @GetMapping("/history")
    public List<Message> getHistory() {
        return chatMemory.get("conversation1");
    }
    
    @GetMapping("/ClearHistory")
    public void clearHistory() {
        chatMemory.clear("conversation1");
        System.out.println("chat history is cleared successfully.");
    }

    
    // Endpoint to ask a question
    @GetMapping("/query")
    public ResponseEntity<?> queryVectorStore(@RequestParam String question) {

      if (question == null || question.isEmpty()) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide a valid questions");
	}
      
    
       String ans = chatService.getAnswer(question);
    	
    	return ResponseEntity.status(HttpStatus.OK).body(ans);
        
    }


}
