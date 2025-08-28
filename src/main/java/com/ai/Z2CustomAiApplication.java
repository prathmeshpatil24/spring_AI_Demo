package com.ai;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.ai.service.CrawlService;
import com.ai.service.EmbeddingService;

@SpringBootApplication
public class Z2CustomAiApplication {

	public static void main(String[] args) {
//	SpringApplication.run(Z2CustomAiApplication.class, args);
  ApplicationContext context = SpringApplication.run(Z2CustomAiApplication.class, args);
		System.out.println("Z2 Custom AI Application started successfully.");
		
	CrawlService crawlService = context.getBean(CrawlService.class);
  EmbeddingService embeddingService = context.getBean(EmbeddingService.class);
//	
//	String url = "https://quotes.toscrape.com/";
//	Map<String, Object> mapData = crawlService.crawlWebPage(url);
//	
//	for (Map.Entry<String, Object> entry : mapData.entrySet()) {
//	    System.out.println(entry.getKey() + " : " + entry.getValue());
//	}
//	
//	List<String> stringList = mapData.values()
//            .stream()
//            .map(Object::toString)
//            .toList();
//	
//	List<float[]> vectors = embeddingService.getEmbeddingList(stringList);
//	int totalVectorDataCount =0;
//	 System.out.println("Embeddings for multipe sentences:- ");
//	 for(float[]vector: vectors) {
//		 for(float vectorElement:vector ) {
//			 totalVectorDataCount++;
//			 System.out.println(vectorElement);
//		 }
//	 }
//	 System.out.println("total vectorDataCount:- " + totalVectorDataCount);
//	 System.out.println("Total Embeddings for multipe sentences:- " + vectors.size());
		
		
		
//	String url = "https://fakestoreapi.com/products";
//
//	List<Map<String, Object>> data = crawlService.crawlJsonApi(url);
//
//	data.forEach(product -> {
//	    System.out.println("ID: " + product.get("id"));
//	    System.out.println("Title: " + product.get("title"));
//	    System.out.println("Price: " + product.get("price"));
//	    System.out.println("Category: " + product.get("category"));
//	    System.out.println("-------------------------");
//	});
	
    
//    pdf reading 
//    String pathString = "C:/Users/91930/Desktop/inter/java/Java Interview Preparation.pdf";
//    
//		String string =crawlService.crawlPdf(pathString);
//	System.out.println(string);
//	String txtFilePath = "C:/Users/91930/Desktop/inter/Sql/sqlQuestions.txt";
    
//    String filePath = "C:/Users/91930/Downloads/file2.doc";
//    List<Document> chunkDocs = crawlService.crawlFiles(txtFilePath);
//    System.out.println("Total Chunks;- " +  chunkDocs.size());
//    System.out.println(chunkDocs.toArray());
	
//	for excle reading
	
//	String filePath = "C:/Users/91930/Downloads/file1_XLSX.xlsx";
//	String jsonData = crawlService.convertExcelToJson(filePath);
//	System.out.println(jsonData);
	
	
//	for csv file reading
	
	
//	String csvFilePath = "C:/Users/91930/Downloads/business.csv";
//	
//	String csvJsonData = crawlService.convertCsvToJson(csvFilePath);
//	
//	System.out.println(csvJsonData);
	
	
//	pipeline for scraping and embedding data
//  String filePath = "C:/Users/91930/Downloads/file1_XLSX.xlsx";
//	String string =crawlService.convertExcelToJson(filePath);
//	
//	embeddingService.getEmbedding(string);
	
  
  
//  pineline for scrapping, embedding and storing in vector db
  
//  String path = "C:/Users/91930/Desktop/inter/Sql/sqlQuestions.txt";
// List<Document> doc = crawlService.crawlFiles(path);
// embeddingService.embedDocument(doc);
  

//  
//  
//  Pinecone pc = new Pinecone.Builder("pcsk_3ZLDAm_LXkQncYMKCkzpniorhnfkXenEbB2tvZnfEvJZLw4G8mukD74ExzEpqABWvhTe").build();
//  Index index = pc.getIndexConnection("my-software-docs");
//  List<String> ids = Arrays.asList("01a6d994-3665-4270-a327-e91102d95263",
//		  "7a4ef7ff-37aa-45c8-bae7-09c52a639375",
//		  "16f9dc00-0e8f-495f-98c8-385ed306650c", 
//		  "13e40f13-5d92-4da3-b0dd-33ddb54686b8",
//		  "f365aeb1-f723-4811-868a-6cd21f87ee95",
//		  "c6d913da-67f5-45a1-8eca-956820ebcaa8",
//		  "8986e34a-6136-4ace-a0f8-10fce89fd0e1",
//		  "ca283c55-5364-403e-b98d-fa129513c60d",
//		  "cfd2eae9-7e49-4b96-ae57-a5087416f166",
//		  "f0d5fefc-f8e6-486b-92ea-9f8ec0dc5824");
//  index.deleteByIds(ids);
//
//  System.out.println("✅ Cleared Pinecone DB");
  
//Try fetching a known ID (replace with one you had before, or just a random one)
//  FetchResponse response = index.fetch(Collections.singletonList("01a6d994-3665-4270-a327-e91102d95263"));
//
//  if (response.getVectorsCount() == 0) {
//      System.out.println("✅ No vectors found. Index is empty.");
//  } else {
//      System.out.println("⚠️ Found vectors: " + response.getVectorsMap());
//  }
  




	
	
	
	

	
		
	}
}
