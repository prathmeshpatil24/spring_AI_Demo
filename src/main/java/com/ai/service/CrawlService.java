package com.ai.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

//  Handles URL/PDF/JSON crawling & embeddings
@Service
public class CrawlService {
	
	@Autowired
    private RestTemplate restTemplate;
    
//	public List<String> crawlWebPage(String url){
//		
//		List<String> results = new ArrayList<>();
//		
//		try {
////            String url = "https://quotes.toscrape.com/";
//            Document doc = Jsoup.connect(url).get();
//
//            Elements quotes = doc.select(".root");
//
//            for (Element quote : quotes) {
//                String data = quote.text();
//                System.out.println(data);
//               results.add(data); 
//            }
//
//		} catch (Exception e) {
//			throw new RuntimeException("Error while crawling the web page" + e.getMessage());
//		}
//		return results;
//	}
	
//	pdf --> document directly without converting into string
	public List<Document> crawlPdf(String path) {
	    try {
	        File file = new File(path);
	        PDDocument document = PDDocument.load(file);

	        PDFTextStripper pdfStripper = new PDFTextStripper();
	        String text = pdfStripper.getText(document);
	        
	       int count = document.getPages().getCount(); 
	       System.out.println("total number of pages:- " +  count);

	        document.close();

	        // Directly wrap into a Spring AI Document
	        Document aiDoc = new Document(text);
	        aiDoc.getMetadata().put("filename", file.getName());
	        aiDoc.getMetadata().put("filepath", file.getAbsolutePath());
	       
	        
	        TokenTextSplitter textSplitter = new TokenTextSplitter();
	        
	       List<Document> chunkDocument =textSplitter.apply(List.of(aiDoc));

	        return chunkDocument;

	    } catch (Exception e) {
	        throw new RuntimeException("Error reading PDF: " + e.getMessage(), e);
	    }
	}
	
//	 Scraping raw HTML
       //this method crawl a web page and return a map containig text, title and etc.	
	    public String crawlWebPage(String url) {

	        try {
                ObjectMapper objectMapper = new ObjectMapper();

                Map<String, Object> result = new HashMap<>();
              //here we are using jsoup library to crawl the web pages      	
	        	org.jsoup.nodes.Document doc = Jsoup.connect(url).get();

                // ================= HEAD ==================
                //getting all head data
                Element headAttributes = doc.head();
                Map<String,String> headMap = new HashMap<>();
                for (Element headChild: headAttributes.children()){
                    headMap.put(headChild.tagName(), headChild.outerHtml());
                }
                result.put("head", headMap);

                // ================= TITLE ==================
                result.put("title", doc.title());

                // ================= META TAGS ==================
                Map<String, String> metaMap = new HashMap<>();
                for (Element meta : doc.select("meta")) {
                    String name = meta.hasAttr("name") ? meta.attr("name") : meta.attr("property");
                    String content = meta.attr("content");
                    if (!name.isEmpty()) {
                        metaMap.put(name, content);
                    }
                }
                result.put("meta", metaMap);

                // ================= LINKS ==================
                List<String> links = new ArrayList<>();
                for (Element link : doc.select("a[href]")) {
                    links.add(link.attr("abs:href"));// absolute URLs
                }
                result.put("links", links);

                // ================= IMAGES ==================
//                List<String> images = new ArrayList<>();
//                for (Element img : doc.select("img[src]")) {
//                    images.add(img.attr("abs:src"));
//                }
//                result.put("images", images);

                // ================= HEADINGS ==================
                Map<String, List<String>> headings = new HashMap<>();
                for (int i = 1; i <= 6; i++) {
                    List<String> hs = doc.select("h" + i).eachText();
                    if (!hs.isEmpty()) {
                        headings.put("h" + i, hs);
                    }
                }
                result.put("headings", headings);

                // ================= P Tag ==================
                List<String> pTagData = new ArrayList<>();
                for (Element p : doc.select("p")) {
                    String pDataText = p.text();
                    pTagData.add(pDataText);
                }
                result.put("p",pTagData);

                // ================= Span tag ==================
                List<String> spanTagData = new ArrayList<>();
                for (Element s : doc.select("span")) {
                    String sDataText = s.text();
                    spanTagData.add(sDataText);
                }
                result.put("span",spanTagData);

                // ================= blockquote ==================
                List<String> blockQuotes = new ArrayList<>();
                for (Element quotes: doc.select("blockquote")){
                    String blockQuotesData= quotes.text();
                    blockQuotes.add(blockQuotesData);
                }
                result.put("blockquote", blockQuotes);

                // ================= LISTS ==================
                List<String> listItems = doc.select("li").eachText();
                result.put("listItems", listItems);

                // ================= TABLES ==================
                List<String> tables = new ArrayList<>();
                for (Element table : doc.select("table")) {
                    tables.add(table.outerHtml()); // store full table HTML
                }
                result.put("tables", tables);

                // ================= FORMS ==================
                List<String> forms = new ArrayList<>();
                for (Element form : doc.select("form")) {
                    forms.add(form.outerHtml());
                }
                result.put("forms", forms);

                // ================= SCRIPTS ==================
//                List<String> scripts = new ArrayList<>();
//                for (Element script : doc.select("script")) {
//                    if (script.hasAttr("src")) {
//                        scripts.add(script.attr("abs:src"));
//                    } else {
//                        scripts.add(script.data()); // inline script
//                    }
//                }
//                result.put("scripts", scripts);

                // ================= STYLES ==================
//                List<String> styles = new ArrayList<>();
//                for (Element style : doc.select("style")) {
//                    styles.add(style.data());
//                }
//                result.put("styles", styles);
                String jsonFormat = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
                return  jsonFormat;
            } catch (Exception e) {
                throw new RuntimeException("Error while crawling web page: " + e.getMessage());
            }
	    }
	    
	    
	 // Crawling JSON API that returns an array
	    public List<Map<String, Object>> crawlJsonApi(String url) {
	        try {
	            return restTemplate.exchange(
	                    url,
	                    HttpMethod.GET,
	                    null,
	                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
	            ).getBody();
	        } catch (Exception e) {
	            throw new RuntimeException("Error while crawling JSON: " + e.getMessage(), e);
	        }
	    }
	    
	    
        // pdf crawling by pdfbox library
//	    pdf --> string
	    public String crawlPdfDemo(String path) {
	        StringBuilder pdfContent = new StringBuilder();
	        try {
	            File file = new File(path);
	            PDDocument document = PDDocument.load(file);

	            PDFTextStripper pdfStripper = new PDFTextStripper();
	            String text = pdfStripper.getText(document);

	            System.out.println("PDF Content:\n" + text);
	            pdfContent.append(text);

	            document.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return pdfContent.toString();
	    }
	    
	    
	    
	    
	    // scrap the file like docx, text etc and directly convert into document chunks
	    public List<Document> crawlFiles(String filePath) {
	    	
	    	try {
	    		File file = new File(filePath); // local file
	    		Resource resource = new FileSystemResource(file); // wraps it as Spring Resource

	             TikaDocumentReader reader = new TikaDocumentReader(resource);
	             List<Document> originalDocuments = reader.get();
	             
//	             here we can get origina docs
//	             System.out.println("original Document content:- " + originalDocuments.toString() );
//	             System.out.println("original Docs Size:- "  + originalDocuments.size());

	             TokenTextSplitter textSplitter = new TokenTextSplitter();
	             /*even though it looks like you’re “directly passing the doc”, the splitter actually breaks it into smaller Document pieces internally.
	              * PDFs, Word docs, articles, manuals, or large datasets → chunking is mandatory.*/
	             List<Document> chunkedDocuments = textSplitter.apply(originalDocuments);
	             
//	             here we get chunked docs data 
//	             System.out.println("chunked doc:- " +  chunkedDocuments.size());

//	             for (Document doc : chunkedDocuments) {
//	                 System.out.println("Chunk Content: " + doc.getText() );
//	                 System.out.println("meta Data:- " +  doc.getMetadata());
//	             }
	             
	             return chunkedDocuments;
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
				throw new RuntimeException("Error while crawling the file:- " +  e.getMessage());
			}
	    }
	    
	    
	    /**
	     * Reads an Excel (.xlsx) file and converts it into a JSON string.
	     *
	     * @param filePath path to the Excel file
	     * @return JSON string of all rows
	     */
	    // Method to convert Excel file (XLSX) into JSON string
	    public String convertExcelToJson(String filePath) {
	        // Use try-with-resources to automatically close FileInputStream and Workbook
	        try (FileInputStream fis = new FileInputStream(new File(filePath));
	             Workbook workbook = new XSSFWorkbook(fis)) {
	        	
	        	 ObjectMapper mapper = new ObjectMapper();

	            // DataFormatter ensures cell values (numbers, dates, etc.) are read as Strings
	            DataFormatter dataFormatter = new DataFormatter();

	            // Get the first sheet from the Excel workbook
	            Sheet sheet = workbook.getSheetAt(0);

	            // Read the first row (usually contains column headers)
	            Row headerRow = sheet.getRow(0);
	            if (headerRow == null) {
	                return "[]"; // Return empty JSON if no header row
	            }

	            // Store headers (column names) in a list
	            List<String> headers = new ArrayList<>();
	            for (Cell cell : headerRow) {
	                headers.add(dataFormatter.formatCellValue(cell)); // Convert cell to String and add to headers
	            }

	            // List to hold all rows of data as maps (key = header, value = cell content)
	            List<Map<String, String>> allRows = new ArrayList<>();

	            // Loop through each row starting from row 1 (skip header row)
	            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
	                Row row = sheet.getRow(i);
	                if (row == null) continue; // Skip empty rows

	                // Map to hold key-value pairs for one row
	                Map<String, String> rowData = new LinkedHashMap<>();

	                // Loop through each column based on header size
	                for (int j = 0; j < headers.size(); j++) {
	                    // Get cell, create blank if missing
	                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

	                    // Put header name as key and cell value as value
	                    rowData.put(headers.get(j), dataFormatter.formatCellValue(cell));
	                }

	                // Add the row data map into the list of all rows
	                allRows.add(rowData);
	            }

	            // Convert list of maps (rows) into JSON string (pretty-printed)
	            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(allRows);

	        } catch (IOException e) {
	            // Log the specific error for debugging
	            System.err.println("Error reading Excel file: " + e.getMessage());
	            e.printStackTrace();
	            return "[]"; // Return empty JSON if there is an error
	        } catch (Exception e) {
	            // Handle any other exceptions (JSON conversion, etc.)
	            System.err.println("Error processing Excel data: " + e.getMessage());
	            e.printStackTrace();
	            return "[]";
	        }
	    }
	    
	    
	    /**
	     * Reads a CSV file and converts its contents into JSON format.
	     * Each row in the CSV becomes a JSON object, with headers as keys.
	     *
	     * @param filePath Path to the CSV file
	     * @return JSON string representing the CSV data
	     */
	    public String convertCsvToJson(String filePath) {
	        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
	        	
	        	 ObjectMapper mapper = new ObjectMapper();

	            // Read all rows from CSV
	            List<String[]> rows = csvReader.readAll();

	            if (rows.isEmpty()) {
	                return "[]"; // Return empty JSON if CSV is empty
	            }

	            // First row is header (column names)
	            String[] headers = rows.get(0);

	            // Store all row data
	            List<Map<String, String>> allRows = new ArrayList<>();

	            // Loop through each row (starting from 2nd row, index 1)
	            for (int i = 1; i < rows.size(); i++) {
	                String[] row = rows.get(i);

	                // Create map to store column-value pairs
	                Map<String, String> rowData = new LinkedHashMap<>();
	                for (int j = 0; j < headers.length; j++) {
	                    String key = headers[j];
	                    String value = (j < row.length) ? row[j] : ""; // Handle missing values
	                    rowData.put(key, value);
	                }

	                allRows.add(rowData);
	            }

	            // Convert list of maps into JSON string
	            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(allRows);

	        } catch (Exception e) {
	            e.printStackTrace();
	            return "[]"; // return empty JSON on error
	        }
	    }
}

