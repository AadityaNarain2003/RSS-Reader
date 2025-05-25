package com.sismics.reader.core.dao.jpa;

import com.sismics.reader.core.model.jpa.BugReport;
import com.sismics.util.context.ThreadLocalContext;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.*;

/**
 * Bug report DAO.
 * 
 * @author Yeshu
 */
public class BugReportDao {
    
    /**
     * Creates a new bug report.
     * 
     * @param bugReport Bug report to create
     * @return Created bug report ID
     */
    public String create(BugReport bugReport) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        
        // Make sure the ID is set
        if (bugReport.getId() == null) {
            bugReport.setId(UUID.randomUUID().toString());
        }
        
        em.persist(bugReport);
        
        return bugReport.getId();
    }
    
    /**
     * Updates a bug report.
     * 
     * @param bugReport Bug report to update
     * @return Updated bug report
     */
    public BugReport update(BugReport bugReport) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        return em.merge(bugReport);
    }
    

    /**
     * Finds bug reports by username.
     * 
     * @param username Username
     * @return List of bug reports
     */
    public List<BugReport> findByUsername(String username) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            Query q = em.createQuery("SELECT b FROM BugReport b WHERE b.username = :username")
                    .setParameter("username", username);
            
            @SuppressWarnings("unchecked")
            List<BugReport> bugs = q.getResultList();
            return bugs;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Finds a bug report by its ID.
     * 
     * @param id Bug report ID
     * @return Bug report if found, otherwise null
     */
    public BugReport findById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            Query q = em.createQuery("SELECT b FROM BugReport b WHERE b.id = :id")
                    .setParameter("id", id);
            
            return (BugReport) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates a bug report by its ID with the provided data.
     * 
     * @param id Bug report ID
     * @param data Map containing the fields to update and their new values
     * @return Updated BugReport object, or null if not found
     */
    public BugReport updateById(String id, Map<String, Object> data) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        BugReport bugReport = findById(id);
        if (bugReport == null) {
            return null;
        }

        // Update fields dynamically based on the provided data
        data.forEach((key, value) -> {
            switch (key) {
                case "description":
                    bugReport.setDescription((String) value);
                    break;
                case "username":
                    bugReport.setUsername((String) value);
                    break;
                case "bugStatus":
                    bugReport.setBugStatus((String) value);
                    break;
                case "deleteStatus":
                    bugReport.setDeleteStatus((String) value);
                    break;
                case "date":
                    bugReport.setDate((Date) value);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown field: " + key);
            }
        });

        return em.merge(bugReport);
    }
    
    /**
     * Finds all bug reports.
     * 
     * @return List of bug reports
     */
    public List<BugReport> findAll() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            Query q = em.createQuery("SELECT b FROM BugReport b ORDER BY b.date DESC");
            
            @SuppressWarnings("unchecked")
            List<BugReport> bugs = q.getResultList();
            return bugs;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Deletes a bug report.
     * 
     * @param id Bug report ID
     */
    public void delete(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("DELETE FROM BugReport b WHERE b.id = :id")
                .setParameter("id", id);
        q.executeUpdate();
    }
}