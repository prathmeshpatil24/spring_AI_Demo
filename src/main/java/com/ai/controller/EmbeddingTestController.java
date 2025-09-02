package com.ai.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai.service.EmbeddingService;

//@RestController
//@RequestMapping("/embedding-test")
//public class EmbeddingTestController {
	
//	@Autowired
//	private EmbeddingService embeddingService;
//
//	@GetMapping("/single-text")
//	public ResponseEntity<?> getSingleTextEmbedding(){
////		for single text
//		float[] embed = embeddingService.getEmbedding("hello world!");
//	    List<Float> embededList = new ArrayList<>();
//	    for (float f : embed) {
//			embededList.add(f);
//		}
//
//	    System.out.println(embededList);
//	    System.out.println(embededList.size());
//
//	    return ResponseEntity.status(HttpStatus.OK).body(embededList);
//	}
//
//	@GetMapping("/multipe-text")
//	public ResponseEntity<?> getMultipleTextEmbedding()
//	{
////	    for multi text
//		 List<String> sentences = List.of(
//	    "Spring Boot makes Java development easy.",
//	    "Artificial Intelligence is evolving fast.",
//	    "Embedding converts text into vectors."
//	);
//
//		 List<float[]> vectors = embeddingService.getEmbeddingList(sentences);
//		int totalVectorDataCount =0;
//		 System.out.println("Embeddings for multipe sentences:- ");
//		 for(float[]vector: vectors) {
//			 for(float vectorElement:vector ) {
//				 totalVectorDataCount++;
//				 System.out.println(vectorElement);
//			 }
//		 }
//		 System.out.println("total vectorDataCount:- " + totalVectorDataCount);
//		 System.out.println("Total Embeddings for multipe sentences:- " + vectors.size());
//
//		 return ResponseEntity.status(HttpStatus.OK).body(totalVectorDataCount);
//	}
//}
