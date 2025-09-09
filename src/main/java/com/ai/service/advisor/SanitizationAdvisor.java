package com.ai.service.advisor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;

import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

public class SanitizationAdvisor implements CallAdvisor {

    private static final String[] FORBIDDEN_PATTERNS = {
            "ignore previous instructions",
            "forget your rules",
            "do anything",
            "tell me secret",
            "password",
            "key",
            "admin",
            "system prompt",
            "override",
            "bypass"
    };

    private static final String[] FORBIDDEN_RESPONSE_WORDS = {
            "password",
            "key",
            "secret",
            "admin",
            "token",
            "credential"
    };

    private Logger logger = LoggerFactory.getLogger(SanitizationAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {

        //1 sanitization
        logRequest(request);

        // Call the next advisor/model
        ChatClientResponse response = chain.nextCall(request);

        logResponse(response);

        return response;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private void logRequest(ChatClientRequest request) {
        logger.debug("request: {}", request);
    }

    private void logResponse(ChatClientResponse chatClientResponse) {
        logger.debug("response: {}", chatClientResponse);
    }
}