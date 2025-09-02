package com.ai.service.scraping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebScrapingService {

    private static final Logger logger = LoggerFactory.getLogger(WebScrapingService.class);

    //	 Scraping raw HTML
    //this method crawl a web page and return a map containing text, title and etc.
    public String crawlWebPage(String url) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Object> result = new HashMap<>();
            //here we are using jsoup library to crawl the web pages
            Document doc = Jsoup.connect(url).get();

            // ================= HEAD ==================
            //getting all head data
            Element headAttributes = doc.head();
            Map<String, String> headMap = new HashMap<>();
            for (Element headChild : headAttributes.children()) {
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
            result.put("p", pTagData);

            // ================= Span tag ==================
            List<String> spanTagData = new ArrayList<>();
            for (Element s : doc.select("span")) {
                String sDataText = s.text();
                spanTagData.add(sDataText);
            }
            result.put("span", spanTagData);

            // ================= blockquote ==================
            List<String> blockQuotes = new ArrayList<>();
            for (Element quotes : doc.select("blockquote")) {
                String blockQuotesData = quotes.text();
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

        // add custom id and classes for scrapping

            // ================= IMAGES ==================
//                List<String> images = new ArrayList<>();
//                for (Element img : doc.select("img[src]")) {
//                    images.add(img.attr("abs:src"));
//                }
//                result.put("images", images);

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

            return jsonFormat;
        } catch (Exception e) {
            throw new RuntimeException("Error while crawling web page: " + e.getMessage());
        }
    }

}
