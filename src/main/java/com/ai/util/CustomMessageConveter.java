package com.ai.util;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

@Component
public class CustomMessageConveter {
	
	public static Message convertToMessage(String chat) {
        // A simple wrapper that turns user input into a UserMessage
        return new UserMessage(chat);
    }

}
