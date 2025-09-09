package com.ai.service.scraping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class JsonApiScraperService {

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(JsonApiScraperService.class);

    // Crawling JSON API that returns an array
    public List<Map<String, Object>> crawlJsonApi(String url) {
        try {
            List<Map<String, Object>> body = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    }
            ).getBody();

            return body;
        } catch (Exception e) {
            throw new RuntimeException("Error while crawling JSON: " + e.getMessage(), e);
        }
    }

}
