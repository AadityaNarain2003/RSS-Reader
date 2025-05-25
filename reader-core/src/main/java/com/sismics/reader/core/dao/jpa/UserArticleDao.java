package com.sismics.reader.core.dao.jpa;

import com.sismics.reader.core.dao.jpa.criteria.UserArticleCriteria;
import com.sismics.reader.core.dao.jpa.dto.UserArticleDto;
import com.sismics.reader.core.dao.jpa.mapper.UserArticleMapper;
import com.sismics.reader.core.model.jpa.UserArticle;
import com.sismics.reader.core.util.jpa.SortCriteria;
import com.sismics.util.context.ThreadLocalContext;
import com.sismics.util.jpa.BaseDao;
import com.sismics.util.jpa.QueryParam;
import com.sismics.util.jpa.filter.FilterCriteria;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * User article DAO.
 */
public class UserArticleDao extends BaseDao<UserArticleDto, UserArticleCriteria> {

    private static final Logger logger = Logger.getLogger(UserArticleDao.class.getName());
    
    static {
        try {
            // Configure the logger with handler and formatter
            FileHandler fh = new FileHandler("logging.txt", true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            // If file logging fails, fallback to console
            logger.log(Level.SEVERE, "Failed to initialize file handler for logger.", e);
        }
    }
    
    @Override
    protected QueryParam getQueryParam(UserArticleCriteria criteria, FilterCriteria filterCriteria) {
        logger.info("[UserArticleDao.getQueryParam] Called with criteria: " + criteria);
        List<String> criteriaList = new ArrayList<>();
        Map<String, Object> parameterMap = new HashMap<>();
        StringBuilder sb = new StringBuilder("select ua.USA_ID_C, ua.USA_READDATE_D, ua.USA_STARREDDATE_D, ua.USA_FEEDNAMES_C, f.FED_TITLE_C, fs.FES_ID_C, fs.FES_TITLE_C, a.ART_ID_C, a.ART_URL_C, a.ART_GUID_C, a.ART_TITLE_C, a.ART_CREATOR_C, a.ART_DESCRIPTION_C, a.ART_COMMENTURL_C, a.ART_COMMENTCOUNT_N, a.ART_ENCLOSUREURL_C, a.ART_ENCLOSURELENGTH_N, a.ART_ENCLOSURETYPE_C, a.ART_PUBLICATIONDATE_D");
        
        if (criteria.isVisible()) {
            if (criteria.isUnread() || criteria.isStarred()) {
                sb.append("  from T_USER_ARTICLE ua ");
                sb.append("  join T_ARTICLE a on(a.ART_ID_C = ua.USA_IDARTICLE_C) ");
            } else {
                sb.append("  from T_ARTICLE a ");
                sb.append("  join T_USER_ARTICLE ua on(a.ART_ID_C = ua.USA_IDARTICLE_C) ");
            }
            criteriaList.add("ua.USA_IDUSER_C = :userId and ua.USA_DELETEDATE_D is null");
        } else if (criteria.getUserId() != null) {
            sb.append("  from T_ARTICLE a ");
            sb.append("  left join T_USER_ARTICLE ua on(a.ART_ID_C = ua.USA_IDARTICLE_C and ua.USA_IDUSER_C = :userId and ua.USA_DELETEDATE_D is null) ");
        } else {
            sb.append("  from T_ARTICLE a ");
            sb.append("  left join T_USER_ARTICLE ua on(a.ART_ID_C = ua.USA_IDARTICLE_C and ua.USA_DELETEDATE_D is null) ");
        }
        
        sb.append("  join T_FEED f on(f.FED_ID_C = a.ART_IDFEED_C and f.FED_DELETEDATE_D is null) ");
        if (criteria.isFetchAllFeedSubscription()) {
            sb.append("  left join T_FEED_SUBSCRIPTION fs on(fs.FES_IDFEED_C = f.FED_ID_C and fs.FES_DELETEDATE_D is null) ");
        } else {
            sb.append("  left join T_FEED_SUBSCRIPTION fs on(fs.FES_IDFEED_C = f.FED_ID_C and fs.FES_IDUSER_C = :userId and fs.FES_DELETEDATE_D is null) ");
        }

        // Adds search criteria
        criteriaList.add("a.ART_DELETEDATE_D is null");
        if (criteria.getUserId() != null) {
            parameterMap.put("userId", criteria.getUserId());
        }
        if (criteria.getFeedId() != null) {
            criteriaList.add("a.ART_IDFEED_C = :feedId");
            parameterMap.put("feedId", criteria.getFeedId());
        }
        if (criteria.getArticleId() != null) {
            criteriaList.add("a.ART_ID_C = :articleId");
            parameterMap.put("articleId", criteria.getArticleId());
        }
        if (criteria.getArticleIdIn() != null) {
            criteriaList.add("a.ART_ID_C IN (:articleIdIn)");
            parameterMap.put("articleIdIn", criteria.getArticleIdIn());
        }
        if (criteria.getUserArticleId() != null) {
            criteriaList.add("ua.USA_ID_C = :userArticleId");
            parameterMap.put("userArticleId", criteria.getUserArticleId());
        }
        if (criteria.isSubscribed()) {
            criteriaList.add("fs.FES_ID_C is not null");
        }
        if (criteria.getCategoryId() != null) {
            criteriaList.add("fs.FES_IDCATEGORY_C = :categoryId");
            parameterMap.put("categoryId", criteria.getCategoryId());
        }
        if (criteria.isUnread()) {
            criteriaList.add("(ua.USA_READDATE_D is null and ua.USA_ID_C is not null)");
        }
        if (criteria.isStarred()) {
            criteriaList.add("ua.USA_STARREDDATE_D is not null");
        }
        if (criteria.getArticlePublicationDateMax() != null && criteria.getArticleIdMax() != null) {
            criteriaList.add("(a.ART_PUBLICATIONDATE_D < :articlePublicationDateMax or " +
                    "  a.ART_PUBLICATIONDATE_D = :articlePublicationDateMax and a.ART_ID_C < :articleIdMax" +
                    ")");
            parameterMap.put("articlePublicationDateMax", criteria.getArticlePublicationDateMax());
            parameterMap.put("articleIdMax", criteria.getArticleIdMax());
        }
        if (criteria.getUserArticleStarredDateMax() != null && criteria.getUserArticleIdMax() != null) {
            criteriaList.add("(ua.USA_STARREDDATE_D < :userArticleStarredDateMax or " +
                    "  ua.USA_STARREDDATE_D = :userArticleStarredDateMax and ua.USA_ID_C < :userArticleIdMax" +
                    ")");
            parameterMap.put("userArticleStarredDateMax", criteria.getUserArticleStarredDateMax());
            parameterMap.put("userArticleIdMax", criteria.getUserArticleIdMax());
        }

        SortCriteria sortCriteria;
        if (criteria.isStarred()) {
            sortCriteria = new SortCriteria(" order by ua.USA_STARREDDATE_D desc, ua.USA_ID_C desc");
        } else {
            sortCriteria = new SortCriteria(" order by a.ART_PUBLICATIONDATE_D desc, ua.USA_ID_C desc");
        }

        logger.info("[UserArticleDao.getQueryParam] Final query: " + sb.toString());
        logger.info("[UserArticleDao.getQueryParam] Criteria List: " + criteriaList);
        logger.info("[UserArticleDao.getQueryParam] Parameter Map: " + parameterMap);
        logger.info("[UserArticleDao.getQueryParam] Sort Criteria: " + sortCriteria);
        return new QueryParam(sb.toString(), criteriaList, parameterMap, sortCriteria, filterCriteria, new UserArticleMapper());
    }

    /**
     * Returns curated articles for a specific user.
     *
     * @param userId User ID
     * @return List of user articles
     */
    // public List<UserArticle> getCuratedArticlesForUser() {
    //     // string userId="currentUserId";
    //     // logger.info("[UserArticleDao.getCuratedArticlesForUser] Called for userId: " + userId);
    //     // EntityManager em = ThreadLocalContext.get().getEntityManager();
    //     // try {
    //     //     Query q = em.createQuery(
    //     //         "select ua from UserArticle ua " +
    //     //         "and ua.deleteDate is null " +
    //     //         "and ua.curatedFeedNames is not null " +
    //     //         "and ua.curatedFeedNames <> ''"
    //     //     );
    //     //     q.setParameter("userId", userId);
    //     //     List<UserArticle> result = q.getResultList();
    //     //     logger.info("[UserArticleDao.getCuratedArticlesForUser] Retrieved articles: " + result);
    //     //     return result;
    //     // } catch (NoResultException e) {
    //     //     logger.info("[UserArticleDao.getCuratedArticlesForUser] No results found for userId: " + userId);
    //     //     return new ArrayList<>();
    //     // }
    //     return null;
    // }
    public List<UserArticle> getCuratedArticlesForUser(String curatedFeedName) {
    EntityManager em = ThreadLocalContext.get().getEntityManager();
    
    // Using LIKE query for comma-separated string values
    // This will match both standalone feed names and feed names within a list
    Query query = em.createQuery(
        "SELECT ua FROM UserArticle ua WHERE " +
        // Match if it's the only value
        "ua.curatedFeedNames = :exactFeedName OR " +
        // Match if it's at the start of list followed by comma
        "ua.curatedFeedNames LIKE :startPattern OR " +
        // Match if it's in the middle of list with commas on both sides
        "ua.curatedFeedNames LIKE :middlePattern OR " +
        // Match if it's at the end of list preceded by comma
        "ua.curatedFeedNames LIKE :endPattern"
    );
    
    System.out.println("curatedFeedName: " + curatedFeedName);
    
    query.setParameter("exactFeedName", curatedFeedName);
    query.setParameter("startPattern", curatedFeedName + ",%");
    query.setParameter("middlePattern", "%," + curatedFeedName + ",%");
    query.setParameter("endPattern", "%," + curatedFeedName);
    
    List<UserArticle> result = query.getResultList();
    
    System.out.println("result: " + result);
    logger.info("[UserArticleDao.getCuratedArticlesForUser] Retrieved articles: " + result);
    
    return result;
}

    /**
     * Creates a new user article.
     *
     * @param userArticle User article to create
     * @return New ID
     */
    public String create(UserArticle userArticle) {
        logger.info("[UserArticleDao.create] Called with userArticle: " + userArticle);
        // Create the UUID for the user article's primary key
        userArticle.setId(UUID.randomUUID().toString());
        if(userArticle.getCuratedFeedNames() == null) {
            logger.info("hello");
            userArticle.setCuratedFeedNames("");
        }
        // Create the user article
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        userArticle.setCreateDate(new Date());
        em.persist(userArticle);
        logger.info("[UserArticleDao.create] Persisted userArticle: " + userArticle);
        // Print current state from database
        printUserArticle(userArticle.getId());
        return userArticle.getId();
    }

    /**
     * Updates a user article.
     *
     * @param userArticle UserArticle to update
     * @return Updated UserArticle
     */
    public UserArticle update(UserArticle userArticle) {
        logger.info("[UserArticleDao.update] Called with userArticle: " + userArticle);
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        // Get the record from the database
        Query q = em.createQuery("select ua from UserArticle ua where ua.id = :id and ua.deleteDate is null")
                .setParameter("id", userArticle.getId());
        UserArticle userArticleFromDb = (UserArticle) q.getSingleResult();
        logger.info("[UserArticleDao.update] Retrieved from DB: " + userArticleFromDb);

        // Update the fields, including curatedFeedNames
        userArticleFromDb.setCuratedFeedNames(userArticle.getCuratedFeedNames());
        userArticleFromDb.setReadDate(userArticle.getReadDate());
        userArticleFromDb.setStarredDate(userArticle.getStarredDate());
        logger.info("[UserArticleDao.update] Updated userArticle: " + userArticleFromDb);
        // Print current state from database
        printUserArticle(userArticleFromDb.getId());
        return userArticleFromDb;
    }

    /**
     * Marks all articles in a category as read.
     *
     * @param criteria Deletion criteria
     */
    public void markAsRead(UserArticleCriteria criteria) {
        logger.info("[UserArticleDao.markAsRead] Called with criteria: " + criteria);
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        StringBuilder sb = new StringBuilder("update T_USER_ARTICLE as ua set USA_READDATE_D = :readDate where ua.USA_ID_C in (");
        sb.append("  select ua2.USA_ID_C from T_USER_ARTICLE ua2 ");
        sb.append("  join T_ARTICLE a on a.ART_ID_C = ua2.USA_IDARTICLE_C ");
        if (criteria.getFeedSubscriptionId() != null || criteria.getCategoryId() != null) {
            sb.append("  join T_FEED f on (f.FED_ID_C = a.ART_IDFEED_C and f.FED_DELETEDATE_D is null)");
            sb.append("  join T_FEED_SUBSCRIPTION fs on (fs.FES_IDFEED_C = f.FED_ID_C and fs.FES_DELETEDATE_D is null) ");
        }
        sb.append("  where a.ART_ID_C = ua2.USA_IDARTICLE_C and a.ART_DELETEDATE_D is null ");
        if (criteria.getFeedSubscriptionId() != null) {
            sb.append("    and fs.FES_ID_C = :feedSubscriptionId ");
        }
        if (criteria.getCategoryId() != null) {
            sb.append("    and fs.FES_IDCATEGORY_C = :categoryId ");
        }
        sb.append(" and ua2.USA_IDUSER_C = :userId and ua2.USA_DELETEDATE_D is null and ua2.USA_READDATE_D is null) ");
        Query q = em.createNativeQuery(sb.toString())
                .setParameter("userId", criteria.getUserId())
                .setParameter("readDate", new Date());
        if (criteria.getFeedSubscriptionId() != null) {
            q.setParameter("feedSubscriptionId", criteria.getFeedSubscriptionId());
        }
        if (criteria.getCategoryId() != null) {
            q.setParameter("categoryId", criteria.getCategoryId());
        }
        int updated = q.executeUpdate();
        logger.info("[UserArticleDao.markAsRead] Number of rows updated: " + updated);
    }

    /**
     * Deletes a user article (logical deletion).
     *
     * @param id User article ID
     */
    public void delete(String id) {
        logger.info("[UserArticleDao.delete] Called with id: " + id);
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        // Retrieve the record
        Query q = em.createQuery("select ua from UserArticle ua where ua.id = :id and ua.deleteDate is null")
                .setParameter("id", id);
        UserArticle userArticleFromDb = (UserArticle) q.getSingleResult();
        logger.info("[UserArticleDao.delete] Retrieved userArticle: " + userArticleFromDb);

        // Mark as deleted
        userArticleFromDb.setDeleteDate(new Date());
        logger.info("[UserArticleDao.delete] Marked userArticle as deleted: " + userArticleFromDb);
    }

    /**
     * Returns an active user article.
     *
     * @param id     User article ID
     * @param userId User ID
     * @return UserArticle or null if not found
     */
    public UserArticle getUserArticle(String id, String userId) {
        logger.info("[UserArticleDao.getUserArticle] Called with id: " + id + ", userId: " + userId);
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select ua from UserArticle ua where ua.id = :id and ua.userId = :userId and ua.deleteDate is null")
                .setParameter("id", id)
                .setParameter("userId", userId);
        try {
            UserArticle result = (UserArticle) q.getSingleResult();
            logger.info("[UserArticleDao.getUserArticle] Retrieved: " + result);
            return result;
        } catch (NoResultException e) {
            logger.info("[UserArticleDao.getUserArticle] No record found for id: " + id + " and userId: " + userId);
            return null;
        }
    }

    /**
     * Debug helper method: retrieves a user article by primary key and logs its state.
     *
     * @param id User article primary key
     */
    public void printUserArticle(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select ua from UserArticle ua where ua.id = :id")
                .setParameter("id", id);
        try {
            UserArticle ua = (UserArticle) q.getSingleResult();
            logger.info("[UserArticleDao.printUserArticle] UserArticle: " + ua);
        } catch (NoResultException e) {
            logger.info("[UserArticleDao.printUserArticle] No UserArticle found with id: " + id);
        }
    }

    /**
     * Debug helper method: retrieves all user articles and logs them.
     */
    /**
 * Debug helper method: retrieves all user articles and logs all columns.
 */
public void printAllUserArticles() {
    EntityManager em = ThreadLocalContext.get().getEntityManager();
    Query q = em.createQuery("select ua from UserArticle ua");
    List<UserArticle> articles = q.getResultList();
    logger.info("[UserArticleDao.printAllUserArticles] All UserArticles:");
    for (UserArticle ua : articles) {
        String logMessage = String.format(
            "ID: %s, UserID: %s, ArticleID: %s, CreateDate: %s, ReadDate: %s, StarredDate: %s, DeleteDate: %s, CuratedFeedNames: %s",
            ua.getId(),
            ua.getUserId(),
            ua.getArticleId(),
            ua.getCreateDate(),
            ua.getReadDate(),
            ua.getStarredDate(),
            ua.getDeleteDate(),
            ua.getCuratedFeedNames()
        );
        logger.info(logMessage);
    }
}

}
