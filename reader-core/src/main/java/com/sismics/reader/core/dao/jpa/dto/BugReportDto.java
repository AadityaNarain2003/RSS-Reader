package com.sismics.reader.core.dao.jpa.dto;

// /**
//  * Bug report DTO.
//  * 
//  * @author yeshu
//  */
// public class BugReportDto {
//     /**
//      * Bug report ID.
//      */
//     private String id;
    
//     /**
//      * Username who reported the bug.
//      */
//     private String username;
    
//     /**
//      * Bug description.
//      */
//     private String description;
    
//     /**
//      * Creation date.
//      */
//     private Long createDate;
    
//     private String bugStatus;

//     private String deleteStatus;

//     /**
//      * Getter for id.
//      * 
//      * @return id
//      */
//     public String getBugId() {
//         return id;
//     }

//     /**
//      * Setter for id.
//      * 
//      * @param id id
//      */
//     public void setId(String id) {
//         this.id = id;
//     }

//     /**
//      * Getter for username.
//      * 
//      * @return username
//      */
//     public String getUsername() {
//         return username;
//     }

//     /**
//      * Setter for username.
//      * 
//      * @param username username
//      */
//     public void setUsername(String username) {
//         this.username = username;
//     }

//     /**
//      * Getter for description.
//      * 
//      * @return description
//      */
//     public String getDescription() {
//         return description;
//     }

//     /**
//      * Setter for description.
//      * 
//      * @param description description
//      */
//     public void setDescription(String description) {
//         this.description = description;
//     }

//     /**
//      * Getter for createDate.
//      * 
//      * @return createDate
//      */
//     public Long getDate() {
//         return createDate;
//     }

//     /**
//      * Setter for createDate.
//      * 
//      * @param createDate createDate
//      */
//     public void setDate(Long createDate) {
//         this.createDate = createDate;
//     }

//     /**
//      * Getter for bugStatus.
//      * 
//      * @return bugStatus
//      */
//     public String getBugStatus() {
//         return bugStatus;
//     }

//     /**
//      * Setter for bugStatus.
//      * 
//      * @param bugStatus bugStatus
//      */
//     public void setBugStatus(String bugStatus) {
//         this.bugStatus = bugStatus;
//     }

//     /**
//      * Getter for deleteStatus.
//      * 
//      * @return deleteStatus
//      */
//     public String getDeleteStatus() {
//         return deleteStatus;
//     }

//     /**
//      * Setter for deleteStatus.
//      * 
//      * @param deleteStatus deleteStatus
//      */
//     public void setDeleteStatus(String deleteStatus) {
//         this.deleteStatus = deleteStatus;
//     }
// }


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Bug report entity.
 * 
 * @author Yeshu
 */
@Entity
@Table(name = "T_BUG_REPORT")
public class BugReportDto {
    /**
     * Bug report ID.
     */
    @Id
    @Column(name = "BUG_ID_C")
    private String id;
    
    /**
     * User ID.
     */
    @Column(name = "BUG_IDUSER_C")
    private String username;
    
    /**
     * Bug description.
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
    @Column(name = "BUG_STATUS_C")
    private String bugStatus;
    
    /**
     * Delete status.
     */
    @Column(name = "BUG_DELETE_STATUS_B")
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
}