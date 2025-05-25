package com.sismics.reader.core.dao.jpa.criteria;

/**
 * Bug report criteria.
 * 
 * @author yeshu
 */
public class BugReportCriteria {
    /**
     * Bug report ID.
     */
    private String id;
    
    /**
     * Username who reported the bug.
     */
    private String userName;
    
    /**
     * Application version.
     */
    private String version;
    
    /**
     * Minimum creation date.
     */
    private Long createDate;
    
    /**
     * Getter for id.
     * 
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for id.
     * 
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for userName.
     * 
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Setter for userName.
     * 
     * @param userName userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Getter for version.
     * 
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Setter for version.
     * 
     * @param version version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Getter for createDateMin.
     * 
     * @return createDateMin
     */
    public Long getCreateDate() {
        return createDate;
    }

    /**
     * Setter for createDateMin.
     * 
     * @param createDate createDateMin
     */
    public void setCreateDate(Long createDateMin) {
        this.createDate = createDateMin;
    }

}