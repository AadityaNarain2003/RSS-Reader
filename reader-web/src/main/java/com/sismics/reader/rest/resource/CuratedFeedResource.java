package com.sismics.reader.rest.resource;

import com.sismics.reader.core.dao.jpa.UserArticleDao;
import com.sismics.reader.core.model.jpa.UserArticle;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import org.codehaus.jettison.json.JSONArray;

/**
 * Curated Feed REST resources.
 * 
 * This resource exposes endpoints to allow users to add or remove curated feed names
 * for their articles. Users can curate their own feeds by selecting articles from 
 * multiple sources, and later other users can subscribe to these curated feeds.
 * 
 * Note: For now, we only focus on managing the curated feed names (stored as a comma-separated
 * list in the T_USER_ARTICLE table).
 * 
 * @author 
 */
@Path("/curated-feed")
public class CuratedFeedResource extends BaseResource {
    private UserArticleDao userArticleDao = new UserArticleDao();

    /**
     * Adds a curated feed name to an article.
     * Endpoint: POST /curated-feed/{articleId}/add
     */
    // @POST
    // @Path("{articleId: [a-z0-9\\-]+}/add")
    // @Produces(MediaType.APPLICATION_JSON)
    // // @Consumes(MediaType.APPLICATION_JSON)
    // public Response addCuratedFeed(@PathParam("articleId") String articleId, String input) throws JSONException {
    //     System.out.println("[addCuratedFeed] Called with articleId: " + articleId + " and input: " + input);
    //     if (!authenticate()) {
    //         System.out.println("[addCuratedFeed] Authentication failed.");
    //         throw new ForbiddenClientException();
    //     }
        
    //     JSONObject jsonInput = new JSONObject(input);
    //     if (!jsonInput.has("feedName")) {
    //         System.out.println("[addCuratedFeed] Missing parameter: feedName");
    //         throw new ClientException("MissingParameter", "Parameter feedName is missing");
    //     }
    //     String feedName = jsonInput.getString("feedName");
    //     System.out.println("[addCuratedFeed] Feed name to add: " + feedName);
        
    //     UserArticle userArticle = userArticleDao.getUserArticle(articleId, principal.getId());
    //     if (userArticle == null) {
    //         System.out.println("[addCuratedFeed] Article not found: " + articleId);
    //         throw new ClientException("ArticleNotFound", MessageFormat.format("Article not found: {0}", articleId));
    //     }
        
    //     userArticleService.addArticleToCuratedFeed(articleId, feedName);
    //     System.out.println("[addCuratedFeed] Feed name added successfully to article: " + articleId);
        
    //     JSONObject response = new JSONObject();
    //     response.put("status", "ok");
    //     response.put("message", "Feed name added successfully");
    //     return Response.ok().entity(response).build();
    // }
    
    /**
     * Creates a curated feed by adding a feed name to the given article.
     * Endpoint: POST /curated-feed/create
     */
    @POST
    @Path("/create")
    // @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCuratedFeed(String input) throws JSONException {
        System.out.println("[createCuratedFeed] Called with input: " + input);
        if (!authenticate()) {
            System.out.println("[createCuratedFeed] Authentication failed.");
            throw new ForbiddenClientException();
        }
        
        JSONObject jsonInput = new JSONObject(input);
        if (!jsonInput.has("feedName") || !jsonInput.has("articleId")) {
            System.out.println("[createCuratedFeed] Missing parameters. feedName and articleId are required.");
            throw new ClientException("MissingParameter", "Parameters feedName and articleId are required");
        }
        String feedName = jsonInput.getString("feedName");
        String articleId = jsonInput.getString("articleId");
        System.out.println("[createCuratedFeed] Feed name: " + feedName + ", Article ID: " + articleId);
        
        UserArticle userArticle = userArticleDao.getUserArticle(articleId, principal.getId());
        if (userArticle == null) {
            System.out.println("[createCuratedFeed] Article not found: " + articleId);
            throw new ClientException("ArticleNotFound", MessageFormat.format("Article not found: {0}", articleId));
        }
        
        System.out.println("[addArticleToCuratedFeed] Retrieved article: " + userArticle);
        String existingFeeds = userArticle.getCuratedFeedNames();
        List<String> feedList = parseFeedNames(existingFeeds);
        if (!feedList.contains(feedName)) {
            System.out.println("[addArticleToCuratedFeed] Feed not found in existing feeds. Adding feed: " + feedName);
            feedList.add(feedName);
            String updatedFeeds = String.join(",", feedList);
            userArticle.setCuratedFeedNames(updatedFeeds);
            userArticleDao.update(userArticle);
            System.out.println("[addArticleToCuratedFeed] Updated article persisted with curatedFeedNames: " + updatedFeeds);
        } else {
            System.out.println("[addArticleToCuratedFeed] Feed already exists: " + feedName);
        }
        System.out.println("[createCuratedFeed] Curated feed created for article: " + articleId);
        
        JSONObject response = new JSONObject();
        response.put("status", "ok");
        response.put("message", "Curated feed created successfully for article " + articleId);
        return Response.ok().entity(response).build();
    }


    private List<String> parseFeedNames(String feedNames) {
        if (feedNames == null || feedNames.trim().isEmpty()) {
            return new ArrayList<>();
        }
        // Split by comma and trim extra whitespace
        String[] feedsArray = feedNames.split(",");
        List<String> list = new ArrayList<>();
        for (String feed : feedsArray) {
            if (!feed.trim().isEmpty()) {
                list.add(feed.trim());
            }
        }
        return list;
    }
    /**
     * Removes a curated feed name from an article.
     * Endpoint: POST /curated-feed/{articleId}/remove
     */
    @POST
    @Path("{articleId: [a-z0-9\\-]+}/remove")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCuratedFeed(@PathParam("articleId") String articleId, String input) throws JSONException {
        System.out.println("[removeCuratedFeed] Called with articleId: " + articleId + " and input: " + input);
        if (!authenticate()) {
            System.out.println("[removeCuratedFeed] Authentication failed.");
            throw new ForbiddenClientException();
        }
        
        JSONObject jsonInput = new JSONObject(input);
        if (!jsonInput.has("feedName")) {
            System.out.println("[removeCuratedFeed] Missing parameter: feedName");
            throw new ClientException("MissingParameter", "Parameter feedName is missing");
        }
        String feedName = jsonInput.getString("feedName");
        System.out.println("[removeCuratedFeed] Feed name to remove: " + feedName);
        
        UserArticle userArticle = userArticleDao.getUserArticle(articleId, principal.getId());
        if (userArticle == null) {
            System.out.println("[removeCuratedFeed] Article not found: " + articleId);
            throw new ClientException("ArticleNotFound", MessageFormat.format("Article not found: {0}", articleId));
        }
        
        // userArticleService.removeArticleFromCuratedFeed(articleId, feedName);
        /** 
         * Need to implement this logic
         * will look at it later.
         */
        System.out.println("[removeCuratedFeed] Feed name removed successfully from article: " + articleId);
        
        JSONObject response = new JSONObject();
        response.put("status", "ok");
        response.put("message", "Feed name removed successfully");
        return Response.ok().entity(response).build();
    }
    
    /**
     * Retrieves the curated feed names associated with an article.
     * Endpoint: GET /curated-feed/{articleId}
     */
    @GET
    @Path("{articleId: [a-z0-9\\-]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCuratedFeeds(@PathParam("articleId") String articleId) throws JSONException {
        System.out.println("[getCuratedFeeds] Called for articleId: " + articleId);
        if (!authenticate()) {
            System.out.println("[getCuratedFeeds] Authentication failed.");
            throw new ForbiddenClientException();
        }
        
        UserArticle userArticle = userArticleDao.getUserArticle(articleId, principal.getId());
        if (userArticle == null) {
            System.out.println("[getCuratedFeeds] Article not found: " + articleId);
            throw new ClientException("ArticleNotFound", MessageFormat.format("Article not found: {0}", articleId));
        }
        
        System.out.println("[getCuratedFeeds] Returning curated feed names: " + userArticle.getCuratedFeedNames());
        JSONObject response = new JSONObject();
        response.put("articleId", articleId);
        response.put("curatedFeedNames", userArticle.getCuratedFeedNames());
        return Response.ok().entity(response).build();
    }
    // @GET
// @Path("/list")
// @Produces(MediaType.APPLICATION_JSON)
// public Response getCuratedFeeds() throws JSONException {
//     System.out.println("[getCuratedFeeds] Called to list curated feeds for current user.");
//     if (!authenticate()) {
//         System.out.println("[getCuratedFeeds] Authentication failed.");
//         throw new ForbiddenClientException();
//     }
    
//     // Retrieve all articles for the current user that have curated feed names set
//     string curatedFeedName = "curated://feed/";
//     List<UserArticle> curatedArticles = userArticleDao.getCuratedArticlesForUser(curatedFeedName);
//     System.out.println("[getCuratedFeeds] Number of curated articles found: " + curatedArticles.size());
    
//     JSONObject response = new JSONObject();
//     JSONArray articlesArray = new JSONArray();
//     for (UserArticle article : curatedArticles) {
//          JSONObject obj = new JSONObject();
//          obj.put("articleId", article.getId());
//          obj.put("curatedFeedNames", article.getCuratedFeedNames());
//          // Optionally, add more article fields as needed (e.g., title, url, etc.) 
//          articlesArray.put(obj);
//     }
//     response.put("articles", articlesArray);
//     return Response.ok().entity(response).build();
// }

}
