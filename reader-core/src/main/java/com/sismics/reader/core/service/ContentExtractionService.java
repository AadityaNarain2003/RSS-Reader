package com.sismics.reader.core.service;

import com.sismics.reader.core.dao.file.newsapi.ContentExtractorAdapter;
import com.sismics.reader.core.dao.file.newsapi.NewsApiAdapter;
import com.sismics.reader.core.dao.file.curatedfeed.CuratedFeedExtractorAdapter;
import com.sismics.reader.core.dao.jpa.UserArticleDao;
import com.sismics.reader.core.dao.jpa.ArticleDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentExtractionService {
    private static final Logger log = LoggerFactory.getLogger(ContentExtractionService.class);

    private List<ContentExtractorAdapter> extractors;
    private UserArticleDao userArticleDao;
    private ArticleDao articleDao;

    public ContentExtractionService() {
        this.extractors = new ArrayList<>();
        // Register available extractors
        this.extractors.add(new NewsApiAdapter(""));
    }

    public void setUserArticleDao(UserArticleDao userArticleDao) {
        this.userArticleDao = userArticleDao;
    }

    public void setArticleDao(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    public ContentExtractorAdapter getExtractorForUrl(String url) {
        logToSeen("getExtractorForUrl", "url=" + url);

        if (url.startsWith("http://user/")) {
            String curatedFeedName = url.substring("http://user/".length());
            CuratedFeedExtractorAdapter adapter = new CuratedFeedExtractorAdapter(curatedFeedName);
            logToSeen("CuratedFeedExtractorAdapter", "url=" + url);
            if (userArticleDao != null) {
                adapter.setUserArticleDao(userArticleDao);
            } else {
                log.warn("UserArticleDao not set in ContentExtractionService - CuratedFeedExtractorAdapter may not work correctly");
            }
            if (articleDao != null) {
                adapter.setArticleDao(articleDao);
            }
            return adapter;
        }

        for (ContentExtractorAdapter extractor : extractors) {
            if (extractor.supports(url)) {
                return extractor;
            }
        }

        return null;
    }

    public ContentExtractorAdapter.ExtractedContent extract(String url) throws Exception {
        logToSeen("ContentExtractionService.extract", "url=" + url);
        if (url.startsWith("http://user/")) {
            String curatedFeedName = url.substring("http://user/".length());
            CuratedFeedExtractorAdapter adapter = new CuratedFeedExtractorAdapter(curatedFeedName);
            adapter.setUserArticleDao(new UserArticleDao()); // Ensure this is properly initialized
            adapter.setArticleDao(new ArticleDao());
            logToSeen("CuratedFeedExtractorAdapter", "url=" + url);
            // if (userArticleDao != null) {
            //     adapter.setUserArticleDao(userArticleDao);
            // } else {
            //     log.warn("UserArticleDao not set in ContentExtractionService - CuratedFeedExtractorAdapter may not work correctly");
            // }
            // if (articleDao != null) {
            //     adapter.setArticleDao(articleDao);
            // }
            return adapter.extract();
        }
        Map<String, String> params = extractParameters(url);
        ContentExtractorAdapter adapter;
        if (params.isEmpty()) {
            String domain = extractDomain(url);
            adapter = new NewsApiAdapter(domain);
        } else {
            adapter = new NewsApiAdapter(params);
        }

        return adapter.extract();
    }

    private String extractDomain(String url) {
        try {
            URL u = new URL(url);
            String host = u.getHost();
            if (host.startsWith("rss.")) {
                host = host.substring(4);
            }
            return host;
        } catch (Exception e) {
            return url;
        }
    }

    private Map<String, String> extractParameters(String url) {
        Map<String, String> parameters = new HashMap<>();
        try {
            URL u = new URL(url);
            String query = u.getQuery();
            if (query != null && !query.isEmpty()) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        parameters.put(
                            URLDecoder.decode(keyValue[0], "UTF-8"),
                            URLDecoder.decode(keyValue[1], "UTF-8")
                        );
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error extracting parameters from url: " + url, e);
        }
        return parameters;
    }

    private void logToSeen(String functionName, String parameters) {
        try (PrintWriter out = new PrintWriter(new FileWriter("seen.txt", true))) {
            out.println("Function: " + functionName + " called with parameters: " + parameters + " at " + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error logging to seen.txt", e);
        }
    }
}
