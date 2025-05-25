

// UpdateBugCommand.java
package com.sismics.reader.rest.resource.command;

import com.sismics.reader.core.dao.jpa.BugReportDao;
import com.sismics.reader.core.model.jpa.BugReport;

import java.util.HashMap;
import java.util.Map;

/**
 * Command for updating a bug report.
 */
public class UpdateBugCommand implements Command {
    private String id;
    private Map<String, Object> updateData;

    /**
     * Constructor with all parameters.
     * 
     * @param id Bug report ID
     * @param description Updated description
     * @param username Updated username
     * @param bugStatus Updated bug status
     * @param deleteStatus Updated delete status
     */
    public UpdateBugCommand(String id, String description, String username, String bugStatus, String deleteStatus) {
        this.id = id;
        this.updateData = new HashMap<>();
        
        if (description != null) {
            updateData.put("description", description);
        }
        
        if (username != null) {
            updateData.put("username", username);
        }
        
        if (bugStatus != null) {
            updateData.put("bugStatus", bugStatus);
        }
        
        if (deleteStatus != null) {
            updateData.put("deleteStatus", deleteStatus);
        }
    }

    @Override
    public BugReport execute() {
        if (id == null || id.isEmpty()) {
        }
        
        BugReportDao bugReportDao = new BugReportDao();
        BugReport existingBug = bugReportDao.findById(id);
        
        if (existingBug == null) {
        }
        
        // If description is provided but empty, reject the update
        if (updateData.containsKey("description") && updateData.get("description").toString().isEmpty()) {
        }
        
        // Set default username to anonymous if empty
        if (updateData.containsKey("username") && updateData.get("username").toString().isEmpty()) {
            updateData.put("username", "anonymous");
        }
        
        return bugReportDao.updateById(id, updateData);
    }
}