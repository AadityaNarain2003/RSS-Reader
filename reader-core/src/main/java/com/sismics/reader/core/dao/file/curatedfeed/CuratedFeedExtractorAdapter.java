package com.sismics.reader.core.dao.file.curatedfeed;

import com.google.common.collect.Lists;
import com.sismics.reader.core.model.jpa.Article;
import com.sismics.reader.core.model.jpa.Feed;
import com.sismics.reader.core.model.jpa.UserArticle;
import com.sismics.reader.core.util.http.ReaderHttpClient;
import com.sismics.reader.core.dao.file.newsapi.ContentExtractorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sismics.reader.core.dao.jpa.UserArticleDao;
import com.sismics.reader.core.dao.jpa.ArticleDao;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

/**
 * Adapter to extract curated feeds from the database.
 */
public class CuratedFeedExtractorAdapter implements ContentExtractorAdapter {
    private static final Logger log = LoggerFactory.getLogger(CuratedFeedExtractorAdapter.class);
    private String curatedFeedName;
    private UserArticleDao userArticleDao;
    private ArticleDao articleDao;
    
    /**
     * Constructor.
     *
     * @param curatedFeedName The name of the curated feed
     */
    public CuratedFeedExtractorAdapter(String curatedFeedName) {
        this.curatedFeedName = curatedFeedName;
    }
    
    /**
     * Set UserArticleDao.
     * 
     * @param userArticleDao The UserArticleDao instance
     */
    public void setUserArticleDao(UserArticleDao userArticleDao) {
        this.userArticleDao = userArticleDao;
    }
    
    /**
     * Set ArticleDao.
     * 
     * @param articleDao The ArticleDao instance
     */
    public void setArticleDao(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }
    
    @Override

    public ExtractedContent extract() throws Exception {
    logToSeen("CuratedFeedExtractorAdapter.extract", "Curated Feed: " + curatedFeedName);
    System.out.println("DEBUG: Starting extraction for curated feed: " + curatedFeedName);

    final ExtractedContent content = new ExtractedContent();
    content.setFeed(new Feed());
    content.setArticleList(Lists.newArrayList());

    if (userArticleDao == null) {
        log.error("ERROR: UserArticleDao is not set!");
        throw new IllegalStateException("UserArticleDao not initialized");
    } else {
        System.out.println("DEBUG: UserArticleDao is set.");
    }

    if (articleDao == null) {
        log.error("ERROR: ArticleDao is not set!");
        throw new IllegalStateException("ArticleDao not initialized");
    } else {
        System.out.println("DEBUG: ArticleDao is set.");
    }

    // Retrieve the results as a raw list
    System.out.println("DEBUG: Fetching curated articles for feed: " + curatedFeedName);
    List<?> results = userArticleDao.getCuratedArticlesForUser(curatedFeedName);
    
    if (results == null || results.isEmpty()) {
        log.warn("WARNING: No articles found for curated feed: {}", curatedFeedName);
        System.out.println("DEBUG: No articles found for curated feed: " + curatedFeedName);
        return content;
    } else {
        System.out.println("DEBUG: Retrieved " + results.size() + " articles.");
    }

    // Create feed metadata
    Feed feed = content.getFeed();
    feed.setRssUrl("curated://feed/" + curatedFeedName);
    feed.setUrl("http://curatedfeed/user/" + curatedFeedName);
    feed.setTitle("Curated Feed: " + curatedFeedName);
    feed.setDescription("Articles curated for " + curatedFeedName);
    
    System.out.println("DEBUG: Feed metadata created successfully.");

    Object firstElement = results.get(0);
    if (firstElement instanceof Article) {
        System.out.println("DEBUG: Retrieved list of Article objects.");
        List<Article> articles = (List<Article>) results;
        for (Article article : articles) {
            System.out.println("DEBUG: Processing article: " + article.getTitle());
            Article newArticle = new Article();
            newArticle.setTitle(article.getTitle());
            newArticle.setDescription(article.getDescription());
            newArticle.setUrl(article.getUrl());
            newArticle.setGuid(article.getGuid());
            newArticle.setPublicationDate(article.getPublicationDate() != null ? article.getPublicationDate() : new Date());
            newArticle.setCreator(article.getCreator());
            content.getArticleList().add(newArticle);
        }
    } else if (firstElement instanceof UserArticle) {
        System.out.println("DEBUG: Retrieved list of UserArticle objects.");
        List<UserArticle> userArticles = (List<UserArticle>) results;
        for (UserArticle userArticle : userArticles) {
            System.out.println("DEBUG: Fetching full article details for UserArticle ID: " + userArticle.getArticleId());
            Article article = articleDao.findById(userArticle.getArticleId());
            if (article != null) {
                System.out.println("DEBUG: Found Article with title: " + article.getTitle());
                Article newArticle = new Article();
                newArticle.setTitle(article.getTitle());
                newArticle.setDescription(article.getDescription());
                newArticle.setUrl(article.getUrl());
                newArticle.setGuid(article.getGuid());
                newArticle.setPublicationDate(article.getPublicationDate() != null ? article.getPublicationDate() : new Date());
                newArticle.setCreator(article.getCreator());
                content.getArticleList().add(newArticle);
            } else {
                Article newArticle = new Article();
                newArticle.setTitle("Dummy Article Title");
                newArticle.setDescription("This is a placeholder description for a missing article.");
                newArticle.setUrl("http://dummy.url");
                newArticle.setGuid("dummy-guid-" + userArticle.getArticleId());
                newArticle.setPublicationDate(new Date());
                newArticle.setCreator("Unknown Author");
                content.getArticleList().add(newArticle);
                log.warn("WARNING: Article not found for id: {}", userArticle.getArticleId());
                System.out.println("DEBUG: WARNING - Article not found for ID: " + userArticle.getArticleId());
            }
        }
    } else {
        log.error("ERROR: Unexpected return type from getCuratedArticlesForUser");
        throw new IllegalStateException("Unexpected return type from UserArticleDao");
    }
    
    System.out.println("DEBUG: Extraction complete. Returning extracted content.");
    return content;
}


    
    @Override
    public boolean supports(String url) {
        return url.startsWith("http://user/");
    }
    
    /**
     * Helper method to log function calls.
     */
    private void logToSeen(String functionName, String parameters) {
        try (PrintWriter out = new PrintWriter(new FileWriter("seen2.txt", true))) {
            out.println("Function: " + functionName + " called with parameters: " + parameters + " at " + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error logging to seen.txt", e);
        }
    }
}
