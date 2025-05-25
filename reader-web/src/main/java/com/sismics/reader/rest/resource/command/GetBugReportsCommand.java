
// GetBugReportsCommand.java
package com.sismics.reader.rest.resource.command;

import com.sismics.reader.core.dao.jpa.BugReportDao;
import com.sismics.reader.core.model.jpa.BugReport;

import java.util.List;

/**
 * Command for retrieving bug reports.
 */
public class GetBugReportsCommand implements Command {
    private String username;
    private boolean isAdmin;

    /**
     * Constructor for getting bug reports.
     * 
     * @param username Username to filter by
     * @param isAdmin Whether the user is an admin
     */
    public GetBugReportsCommand(String username, boolean isAdmin) {
        this.username = username;
        this.isAdmin = isAdmin;
    }

    @Override
    public List<BugReport> execute() {
        BugReportDao bugReportDao = new BugReportDao();
        
        if (isAdmin) {
            return bugReportDao.findAll();
        } else {
            return bugReportDao.findByUsername(username);
        }
    }
}