package com.ai.service.scraping;

import com.ai.service.CrawlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelScraperService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelScraperService.class);


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

}
