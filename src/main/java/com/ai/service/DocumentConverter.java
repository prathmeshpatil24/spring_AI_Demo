package com.ai.service;

import com.ai.util.CustomTextSplitter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocumentConverter {

    @Autowired
    private CustomTextSplitter customTextSplitter;

    @Autowired
    private ObjectMapper objectMapper;

    // Convert plain text/json to Document list
    public List<Document> convertJsonStringToDocument(String jsonString) {
        if (jsonString == null || jsonString.isBlank()) {
            throw new IllegalArgumentException("Input text is null or empty");
        }

        Document document = new Document(jsonString.trim());

        List<Document> documentList = customTextSplitter.splitDocument(document);
        return documentList;
    }

//    convert jsonApi response to Document
    public List<Document> convertJsonApiToDocument(List<Map<String, Object>> jsonApiResponse){

        try {

            if (jsonApiResponse == null || jsonApiResponse.isEmpty()) {
                throw new IllegalArgumentException("JSON API response is null or empty");
            }

            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonApiResponse);

            Document document = new Document(jsonString);

            List<Document> documentList = customTextSplitter.splitDocument(document);
            return  documentList;

        }catch (Exception e) {
            throw new RuntimeException("Error converting JSON API response to Document", e);
        }
    }
}
