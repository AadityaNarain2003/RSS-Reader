// package com.sismics.reader.core.service;

// // import com.sismics.reader.core.dao.jpa.UserArticleDao;
// // import com.sismics.reader.core.model.jpa.UserArticle;

// // import java.util.ArrayList;
// // import java.util.Arrays;
// // import java.util.List;
// // import java.util.Date;

// public class UserArticleService {

//     // private UserArticleDao userArticleDao = new UserArticleDao();

//     /**
//      * Adds a curated feed name to the specified article.
//      *
//      * @param articleId the ID of the article
//      * @param feedName the name of the curated feed to add
//      */
//     public void addArticleToCuratedFeed(String articleId, String feedName) {
//     System.out.println("[addArticleToCuratedFeed] Called for articleId: " + articleId + ", feedName: " + feedName);
    
//     // Try to retrieve the article for the current user.
//     // UserArticle article = userArticleDao.getUserArticle(articleId, principal.getId());
    
//     // if (article == null) {
//     //     System.out.println("[addArticleToCuratedFeed] Article not found for articleId: " + articleId + ". Creating new article record.");
        
//     //     // Create a new UserArticle and populate all necessary columns.
//     //     article = new UserArticle();
//     //     // Set the reference to the external article identifier
//     //     article.setArticleId(articleId);  
//     //     // Set primary key, if your mapping requires it to be generated separately, let the DAO do it.
//     //     // For now, we set other properties:
//     //     article.setUserId(getCurrentUserId());
//     //     article.setCreateDate(new Date());
//     //     // Initialize the curated feed names with the provided feed
//     //     article.setCuratedFeedNames(feedName);
        
//     //     String newId = userArticleDao.create(article);
//     //     System.out.println("[addArticleToCuratedFeed] Created new user article with id: " + newId);
//     // } else {
//         System.out.println("[addArticleToCuratedFeed] Retrieved article: " + article);
//         String existingFeeds = article.getCuratedFeedNames();
//         List<String> feedList = parseFeedNames(existingFeeds);
//         if (!feedList.contains(feedName)) {
//             System.out.println("[addArticleToCuratedFeed] Feed not found in existing feeds. Adding feed: " + feedName);
//             feedList.add(feedName);
//             String updatedFeeds = String.join(",", feedList);
//             article.setCuratedFeedNames(updatedFeeds);
//             userArticleDao.update(article);
//             System.out.println("[addArticleToCuratedFeed] Updated article persisted with curatedFeedNames: " + updatedFeeds);
//         } else {
//             System.out.println("[addArticleToCuratedFeed] Feed already exists: " + feedName);
//         }
//     // }
// }



//     /**
//      * Removes a curated feed name from the specified article.
//      *
//      * @param articleId the ID of the article
//      * @param feedName the name of the curated feed to remove
//      */
//     public void removeArticleFromCuratedFeed(String articleId, String feedName) {
//         UserArticle article = userArticleDao.getUserArticle(articleId, principal.getId());
//         if (article != null) {
//             String existingFeeds = article.getCuratedFeedNames();
//             List<String> feedList = parseFeedNames(existingFeeds);
//             if (feedList.remove(feedName)) {
//                 article.setCuratedFeedNames(String.join(",", feedList));
//                 userArticleDao.update(article);
//             }
//         }
//     }

//     /**
//      * Helper method to parse the comma-separated feed names into a List.
//      *
//      * @param feedNames comma-separated feed names
//      * @return List of feed names
//      */
//     private List<String> parseFeedNames(String feedNames) {
//         if (feedNames == null || feedNames.trim().isEmpty()) {
//             return new ArrayList<>();
//         }
//         // Split by comma and trim extra whitespace
//         String[] feedsArray = feedNames.split(",");
//         List<String> list = new ArrayList<>();
//         for (String feed : feedsArray) {
//             if (!feed.trim().isEmpty()) {
//                 list.add(feed.trim());
//             }
//         }
//         return list;
//     }

// }
