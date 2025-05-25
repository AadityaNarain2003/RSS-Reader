package com.sismics.reader.core.dao.file.newsapi;

import com.google.common.collect.Lists;
import com.sismics.reader.core.model.jpa.Article;
import com.sismics.reader.core.model.jpa.Feed;
import com.sismics.reader.core.util.http.ReaderHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Collections;


/**
 * NewsAPI adapter to extract articles from websites without RSS feeds.
 * Modified to support arbitrary query parameters.
 */
public class NewsApiAdapter implements ContentExtractorAdapter {
    private static final Logger log = LoggerFactory.getLogger(NewsApiAdapter.class);
    private static final String API_KEY = "6ed2a39914d849879d72e1ea771f23c8";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    private static final String API_BASE_URL = "https://newsapi.org/v2/everything";
    
    // Use either a domain string or a map of parameters.
    private String domain;
    private Map<String, String> parameters;
    
    /**
     * Constructor for domain-only extraction.
     */
    public NewsApiAdapter(String domain) {
        this.domain = domain;
        this.parameters = Collections.singletonMap("domains", domain);

    }
    
    /**
     * Overloaded constructor for arbitrary query parameters.
     */
    public NewsApiAdapter(Map<String, String> parameters) {
        this.parameters = parameters;
        this.domain = parameters.get("domains"); // May be null if not provided.
    }
    
    @Override
    public ExtractedContent extract() throws Exception {
        logToSeen("NewsApiAdapter.extract", parameters.toString());
        final ExtractedContent content = new ExtractedContent();
        content.setFeed(new Feed());
        content.setArticleList(Lists.newArrayList());
        
        try {
            StringBuilder queryBuilder = new StringBuilder("?");
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                queryBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()))
                            .append("&");
            }
            queryBuilder.append("apiKey=").append(API_KEY);
            String apiUrl = API_BASE_URL + queryBuilder.toString();
            
            new ReaderHttpClient() {
                @Override
                public Void process(InputStream is) throws Exception {
                    String responseText = convertStreamToString(is);
                    JSONObject json = new JSONObject(responseText);
                    if ("ok".equalsIgnoreCase(json.getString("status"))) {
                        Feed feed = content.getFeed();
                        feed.setRssUrl(domain != null ? domain : parameters.toString());
                        feed.setUrl(domain != null ? "https://" + domain : API_BASE_URL);
                        feed.setTitle("NewsAPI: " + (domain != null ? domain : parameters.toString()));
                        feed.setDescription("News from NewsAPI using parameters: " + parameters);
                        
                        JSONArray articles = json.getJSONArray("articles");
                        for (int i = 0; i < articles.length(); i++) {
                            JSONObject articleJson = articles.getJSONObject(i);
                            Article article = new Article();
                            article.setTitle(articleJson.getString("title"));
                            article.setDescription(articleJson.optString("content", ""));
                            article.setUrl(articleJson.getString("url"));
                            article.setGuid(articleJson.getString("url"));
                            
                            String publishedAt = articleJson.getString("publishedAt");
                            try {
                                Date pubDate = DATE_FORMAT.parse(publishedAt);
                                article.setPublicationDate(pubDate);
                            } catch (Exception e) {
                                article.setPublicationDate(new Date());
                            }
                            if (!articleJson.isNull("author")) {
                                article.setCreator(articleJson.getString("author"));
                            }
                            content.getArticleList().add(article);
                        }
                    } else {
                        log.error("NewsAPI returned error: " + json.optString("message", "Unknown error"));
                    }
                    return null;
                }
                
                private String convertStreamToString(InputStream is) throws Exception {
                    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    return s.hasNext() ? s.next() : "";
                }
            }.open(new URL(apiUrl));
        } catch (Exception e) {
            log.error("Error fetching content from NewsAPI for parameters: " + parameters, e);
            throw e;
        }
        return content;
    }
    
    @Override
    public boolean supports(String url) {
        return url.startsWith("http") && !url.contains("rss") && !url.contains("feed") && !url.contains("atom");
    }
    
    /**
     * Helper method to log function calls to seen.txt.
     */
    private void logToSeen(String functionName, String parameters) {
        try (PrintWriter out = new PrintWriter(new FileWriter("seen.txt", true))) {
            out.println("Function: " + functionName + " called with parameters: " + parameters + " at " + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error logging to seen.txt", e);
        }
    }
}
