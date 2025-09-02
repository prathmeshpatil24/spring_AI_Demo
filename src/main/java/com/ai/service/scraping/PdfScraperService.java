package com.ai.service.scraping;


import com.ai.util.CustomTextSplitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfScraperService {
    @Autowired
    private CustomTextSplitter customTextSplitter;

    private static final Logger logger = LoggerFactory.getLogger(PdfScraperService.class);

    //	pdf --> document directly without converting into string
    public List<Document> crawlPdf(String path) {
        List<Document> chunkDocs = new ArrayList<>();

        try {
            File file = new File(path);
            PDDocument pdfDocument = PDDocument.load(file);

            int totalPages = pdfDocument.getPages().getCount();
            System.out.println("Total number of pages: " + totalPages);

            PDFTextStripper pdfStripper = new PDFTextStripper();

            for (int i = 1; i <= totalPages; i++) {
                pdfStripper.setStartPage(i);
                pdfStripper.setEndPage(i);

                String pageText = pdfStripper.getText(pdfDocument);

                // Wrap into Document with page metadata
                Document pageDoc = new Document(pageText);
                pageDoc.getMetadata().put("filename", file.getName());
                pageDoc.getMetadata().put("filepath", file.getAbsolutePath());
                pageDoc.getMetadata().put("page", String.valueOf(i));

                // Split this page into chunks
                List<Document> documentList = customTextSplitter.splitDocument(pageDoc);
                chunkDocs.addAll(documentList);
            }

            pdfDocument.close();

        } catch (Exception e) {
            throw new RuntimeException("Error reading PDF: " + e.getMessage(), e);
        }
        return chunkDocs;
    }
}
