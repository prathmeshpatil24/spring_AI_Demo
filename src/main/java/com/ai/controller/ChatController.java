package com.ai.controller;


import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ai.service.ChatService;
import com.ai.util.CustomMessageConveter;
import reactor.core.publisher.Flux;


//Endpoints for chat, RAG queries

@RestController
@RequestMapping("/ollama")
public class ChatController {


    private final ChatClient ollamaChatClient;
    private final ChatMemory chatMemory;
    private final CustomMessageConveter messageConveter;
    private final ChatService chatService;


    public ChatController(@Qualifier("ollamaChatClient") ChatClient ollamaChatClient,
                          ChatMemory chatMemory,
                          CustomMessageConveter messageConveter,
                          ChatService chatService
                          )
    {
        this.ollamaChatClient = ollamaChatClient;
        this.chatMemory = chatMemory;
        this.messageConveter = messageConveter;
        this.chatService = chatService;
    }

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @GetMapping("/{chat}")
    public ResponseEntity<?> getAnswer(@PathVariable String chat){
//    	test data is getting or not here
    	System.out.println("Prompt entered by User:-" +  chat);
    	
//    	// Convert to Message
//    	Message message=messageConveter.convertToMessage(chat);
//        // add the user message to chat memory
//        chatMemory.add("conversation1",message);

        // Get full conversation
        List<Message> conversationHistory = chatMemory.get("conversation1");
        chatMemory.add("conversation1", new UserMessage(chat));

		//main logic to get ans from ai model
       ChatResponse chatResponse = ollamaChatClient
                .prompt() // 1. Send your prompt (the user’s query / message)
                .system("You are a helpful assistant.")
                .messages(conversationHistory) // add  the conversion history
                .user(chat) // 1. Send your prompt (the user’s query / message)
                .call()// 2. Execute the call to the model
                .chatResponse(); // 3. Get the structured ChatResponse object
//after call() method we can use .entity() to get data in entity format also (in class we get the response like title, content, createdAt etc)


       // print the ai model
       //System.out.println(chatResponse.getMetadata().getModel());

        // print the full response
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
        List<Message> conversation1 = chatMemory.get("conversation1");
        System.out.println("chat history of conversation1:");
        conversation1.forEach(
            message -> System.out.println(message.getMessageType() + ": " + message.getText())
        );

        return conversation1;
    }
    
    @GetMapping("/ClearHistory")
    public void clearHistory() {
        chatMemory.clear("conversation1");
        System.out.println("chat history is cleared successfully.");
    }

    
    // Endpoint to ask a question
    @GetMapping("/query")
    public ResponseEntity<String> queryVectorStore(@RequestParam String question) {

      if (question == null || question.isEmpty()) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide a valid questions");
	}
       String ans = chatService.getAnswer(question);
    	return ResponseEntity.status(HttpStatus.OK).body(ans);
    }


    @GetMapping("/stream")
    public ResponseEntity<Flux<String>> streamChatting(@RequestParam String q){
        Flux<String> stringFlux = chatService.streamChat(q);
        return ResponseEntity.ok(stringFlux);
    }

    @GetMapping("/stream-demo")
    public ResponseEntity<Flux<String>> fluxResponseEntity(@RequestParam String q){
        if (q == null || q.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Flux.just("Please provide a valid questions"));
        }
        Flux<String> answerWithStream = chatService.getAnswerWithStream(q);
        return  ResponseEntity.ok(answerWithStream);
    }


}
