package com.ai.controller;

import com.ai.service.LeadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("lead")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService){
        this.leadService = leadService;
    }

    @PostMapping("/ask")
    public ResponseEntity<Flux<String>>guideToCounselor(@RequestBody String chat){
        return ResponseEntity.ok(leadService.getAnswer(chat));
    }
}
