package com.sismics.reader.rest.resource.command;

import com.sismics.reader.core.dao.jpa.BugReportDao;
import com.sismics.reader.core.model.jpa.BugReport;
import java.util.Date;
import java.util.UUID;

/**
 * Command for creating a bug report.
 */
public class CreateBugCommand implements Command {
    private String description;
    private String username;
    private String bugStatus;
    private String deleteStatus;

    /**
     * Constructor with required parameters.
     * 
     * @param description Description of the bug
     * @param username Username reporting the bug
     * @param bugStatus Status of the bug
     * @param deleteStatus Deletion status
     */
    public CreateBugCommand(String description, String username, String bugStatus, String deleteStatus) {
        this.description = description;
        this.username = username;
        this.bugStatus = bugStatus;
        this.deleteStatus = deleteStatus;
    }

    @Override
    public BugReport execute () {
        if (description == null || description.isEmpty()) {

        }

        if (username == null || username.isEmpty()) {
            username = "anonymous";
        }

        // Create the bug report
        BugReport bugReport = new BugReport();
        String randomId = UUID.randomUUID().toString();
        bugReport.setId(randomId);
        bugReport.setUsername(username);
        bugReport.setDescription(description);
        bugReport.setDate(new Date());
        bugReport.setBugStatus(bugStatus != null ? bugStatus : "ongoing");
        bugReport.setDeleteStatus(deleteStatus != null ? deleteStatus : "false");

        BugReportDao bugReportDao = new BugReportDao();
        bugReportDao.create(bugReport);
        
        return bugReport;
    }
}