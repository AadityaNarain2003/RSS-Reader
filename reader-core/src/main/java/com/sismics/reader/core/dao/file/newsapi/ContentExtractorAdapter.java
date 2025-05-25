package com.sismics.reader.core.dao.file.newsapi;

import com.sismics.reader.core.model.jpa.Article;
import com.sismics.reader.core.model.jpa.Feed;

import java.util.List;

/**
 * Interface for content extraction adapters.
 *
 * @author jtremeaux
 */
public interface ContentExtractorAdapter {
    /**
     * Extract content from a source.
     *
     * @return Extracted content
     * @throws Exception If an error occurs during extraction
     */
    ExtractedContent extract() throws Exception;
    
    /**
     * Check if this adapter supports the given URL.
     *
     * @param url URL to check
     * @return True if this adapter can handle the URL
     */
    boolean supports(String url);
    
    /**
     * Class representing extracted content.
     */
    class ExtractedContent {
        private Feed feed;
        private List<Article> articleList;
        
        public Feed getFeed() {
            return feed;
        }
        
        public void setFeed(Feed feed) {
            this.feed = feed;
        }
        
        public List<Article> getArticleList() {
            return articleList;
        }
        
        public void setArticleList(List<Article> articleList) {
            this.articleList = articleList;
        }
    }
}