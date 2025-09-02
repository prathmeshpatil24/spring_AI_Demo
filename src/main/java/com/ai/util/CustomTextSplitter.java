package com.ai.util;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomTextSplitter {

    public List<Document> splitDocument(Document document) {
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(800)
                .withMaxNumChunks(50)
                .build();

        return splitter.apply(List.of(document));
    }
}
