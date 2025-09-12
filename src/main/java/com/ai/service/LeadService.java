package com.ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.stringtemplate.v4.ST;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class LeadService {

    private final ChatMemory chatMemory;
    private  final ChatClient ollamaChatClient;

    public LeadService(ChatMemory chatMemory,
                       ChatClient ollamaChatClient){
     this.chatMemory = chatMemory;
     this.ollamaChatClient = ollamaChatClient;
    }

    private static final Logger logger = LoggerFactory.getLogger(LeadService.class);
    public Flux<String> getAnswer(String chat){
        //test data is getting or not here
        System.out.println("Prompt entered by User:-" +  chat);

        String systemPrompt = "Summarize the coming lead data." +
                         "Then suggest only one clear counselor action to be taken in 3-4 concise bullet points";

        String systemPrompt1 = "Analyze the provided lead data and recommend one primary action for the counselor. State this action only in 3 to 4 bullet points that are strictly tactical next steps";
        //main logic to get ans from ai model
        long start = System.currentTimeMillis();
        return ollamaChatClient
                .prompt()
                .system(systemPrompt1)
                .user(chat)
                .stream()
                .content()
                .doOnSubscribe(sub -> logger.info("Pipeline subscribed at {} ms", System.currentTimeMillis() - start)) //before AI call
                .doOnNext(token -> {
                    long firstResponseTime = System.currentTimeMillis() - start;
                    logger.info("First AI response received at {} ms", firstResponseTime);
                })// log when first token arrives from AI
                .doFinally(
                        signal -> logger.info("Total pipeline took {} ms",
                                System.currentTimeMillis() - start)
                );// at last total time
    }
}
