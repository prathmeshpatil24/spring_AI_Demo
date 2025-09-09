package com.ai.service.scraping;

import com.ai.util.CustomTextSplitter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocxScraperService {

    @Autowired
    private CustomTextSplitter customTextSplitter;

    private static final Logger logger = LoggerFactory.getLogger(DocxScraperService.class);

    // scrap the file like docx, text etc and directly convert into document chunks
    public List<Document> crawlFiles(String filePath) {
        List<Document> chunkedDocuments = new ArrayList<>();
        File file = new File(filePath);

        try {
            String fileName = file.getName().toLowerCase();

            if (fileName.endsWith(".docx")) {
                // Use Apache POI for DOCX
                try (FileInputStream fis = new FileInputStream(file);
                     XWPFDocument doc = new XWPFDocument(fis)) {

                    List<XWPFParagraph> paragraphs = doc.getParagraphs();
                    int paraIndex = 1;

                    for (XWPFParagraph para : paragraphs) {
                        String text = para.getText().trim();
                        if (text.isEmpty()) continue;

                        Document aiDoc = new Document(text);
                        aiDoc.getMetadata().put("filename", file.getName());
                        aiDoc.getMetadata().put("filepath", file.getAbsolutePath());
                        aiDoc.getMetadata().put("paragraph", String.valueOf(paraIndex++));

                        List<Document> chunks = customTextSplitter.splitDocument(aiDoc);
                        chunkedDocuments.addAll(chunks);
                    }
                }
            } else {
                // Use Tika for TXT etc.
                Resource resource = new FileSystemResource(file);
                TikaDocumentReader reader = new TikaDocumentReader(resource);
                List<Document> originalDocs = reader.get();

//                define separately text splitting
                TokenTextSplitter textSplitter = TokenTextSplitter.builder()
                    .withChunkSize(800)
                    .withMaxNumChunks(50)
                    .build();

                chunkedDocuments = textSplitter.apply(originalDocs);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while crawling file: " + e.getMessage(), e);
        }

        return chunkedDocuments;
    }
}
