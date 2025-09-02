package com.ai.service.scraping;

import com.ai.service.CrawlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class CsvScraperService {

    private static final Logger logger = LoggerFactory.getLogger(CsvScraperService.class);

//    method to convert CSV file into JSON string
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
