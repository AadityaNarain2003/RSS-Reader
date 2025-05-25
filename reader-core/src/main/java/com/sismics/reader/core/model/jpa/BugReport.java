package com.sismics.reader.core.model.jpa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Objects;

/**
 * Bug report entity.
 * 
 * @author Yeshu
 */
@Entity
@Table(name = "T_BUG_REPORT")
public class BugReport {
    /**
     * Bug report ID.
     */
    @Id
    @Column(name = "BUG_ID_C", length = 36)
    private String id;
    
    /**
     * Username.
     */
    @Column(name = "BUG_IDUSER_C", length = 50)
    private String username;
    
    /**
     * Description.
     */
    @Column(name = "BUG_DESCRIPTION_C", length = 4000)
    private String description;
    
    /**
     * Creation date.
     */
    @Column(name = "BUG_CREATEDATE_D", nullable = false)
    private Date date;
    
    /**
     * Bug status.
     */
    @Column(name = "BUG_STATUS_D", length = 50)
    private String bugStatus;
    
    /**
     * Delete status.
     */
    @Column(name = "BUG_DELETE_STATUS", length = 50)
    private String deleteStatus;
    
    /**
     * Getter of id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter of id.
     *
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter of username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter of username.
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter of description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter of description.
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter of date.
     *
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Setter of date.
     *
     * @param date date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Getter of bugStatus.
     *
     * @return bugStatus
     */
    public String getBugStatus() {
        return bugStatus;
    }

    /**
     * Setter of bugStatus.
     *
     * @param bugStatus bugStatus
     */
    public void setBugStatus(String bugStatus) {
        this.bugStatus = bugStatus;
    }

    /**
     * Getter of deleteStatus.
     *
     * @return deleteStatus
     */
    public String getDeleteStatus() {
        return deleteStatus;
    }

    /**
     * Setter of deleteStatus.
     *
     * @param deleteStatus deleteStatus
     */
    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }
    
    @Override
    public String toString() {

        return Objects.toStringHelper(this)
                .add("id", id)
                .add("description", description)
                .add("date", date)
                .add("username", username)
                .add("bugStatus", bugStatus)
                .add("deleteStatus", deleteStatus)
                .toString();
    }
}