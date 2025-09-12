package com.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Z2CustomAiApplication {

	public static void main(String[] args) {
//	SpringApplication.run(Z2CustomAiApplication.class, args);
  ApplicationContext context = SpringApplication.run(Z2CustomAiApplication.class, args);
		System.out.println("Z2 Custom AI Application started successfully.");
		
//	    CrawlService crawlService = context.getBean(CrawlService.class);
       /*
        EmbeddingService embeddingService = context.getBean(EmbeddingService.class);
        DocumentConverter documentConverter = context.getBean(DocumentConverter.class);
        CsvScraperService csvScraperService = context.getBean(CsvScraperService.class);
        DocxScraperService docxScraperService = context.getBean(DocxScraperService.class);
        ExcelScraperService excelScraperService = context.getBean(ExcelScraperService.class);
        PdfScraperService pdfScraperService = context.getBean(PdfScraperService.class);
        WebScrapingService webScrapingService = context.getBean(WebScrapingService.class);
        JsonApiScraperService jsonApiScraperService = context.getBean(JsonApiScraperService.class);
        */

        /*
        //  web scraping
	    String url = "https://quotes.toscrape.com/";
        String string = webScrapingService.crawlWebPage(url);
        System.out.println(string);
        List<Document> documentList = documentConverter.convertJsonStringToDocument(string);
        embeddingService.testEmbeddingDocument(documentList);
        */

        /*
       //api scraping
       String url = "https://fakestoreapi.com/products";
        List<Map<String, Object>> maps = jsonApiScraperService.crawlJsonApi(url);
        List<Document> documentList = documentConverter.convertJsonApiToDocument(maps);
        embeddingService.testEmbeddingDocument(documentList);
         */

     /*
        //pdf reading
        String pathString = "C:/Users/91930/Desktop/inter/java/Java Interview Preparation.pdf";
        List<Document> documentList = pdfScraperService.crawlPdf(pathString);
        embeddingService.testEmbeddingDocument(documentList);
      */


        /*
       //text scraping
	String txtFilePath = "C:/Users/91930/Desktop/inter/Sql/sqlQuestions.txt";
        List<Document> documentList = docxScraperService.crawlFiles(txtFilePath);
//        for (int i = 0; i<documentList.size(); i++) {
//            System.out.println("data of " + i + " :- " + documentList.get(i));
//        }
//        System.out.println("complete Scraping");
        embeddingService.testEmbeddingDocument(documentList);
       */

        /*
//        docs scraping
        String filePath = "C:/Users/91930/Downloads/file1.docx";
        List<Document> documentList = docxScraperService.crawlFiles(filePath);
        for (int i = 0; i <documentList.size() ; i++) {
            System.out.println("data of " + i + " :- " + documentList.get(i));
        }
        System.out.println("reading complete");
        System.out.println("=============================");
        embeddingService.testEmbeddingDocument(documentList);
        */


      /*
      //for excle reading
	   String filePath = "C:/Users/91930/Downloads/file1_XLSX.xlsx";
        String string = excelScraperService.convertExcelToJson(filePath);
        List<Document> documentList = documentConverter.convertJsonStringToDocument(string);
        embeddingService.testEmbeddingDocument(documentList);
       */


        /*
   //for csv file reading
	   String csvFilePath = "C:/Users/91930/Downloads/business.csv";
        String string = csvScraperService.convertCsvToJson(csvFilePath);
       //System.out.println(string);
        List<Document> documentList = documentConverter.convertJsonStringToDocument(string);
        System.out.println("Embedding data of csv file");
        embeddingService.testEmbeddingDocument(documentList);
         */

        /*
//        flux concept
        Flux<String> flux = Flux.just("Hello", " ", "World", "!");
        flux.subscribe(System.out::println);
//        System.out.println(flux.);
        System.out.println("============================");
        String block = flux.collect(Collectors.joining()).block();
        System.out.println(block);
         */

//        String chat = "hello how r u";
//        UserMessage userMessage = new UserMessage(chat);
//        System.out.println(userMessage);
//        System.out.println("===========");
//        System.out.println(userMessage.getText());
//        System.out.println(userMessage.getClass().getName());
//       if (userMessage instanceof UserMessage){
//           System.out.println("userMessage is a Message");
//       }

    }
}
