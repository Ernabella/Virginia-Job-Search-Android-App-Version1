package com.example.yiwenren.virginiajobsearch.models;

/**
 * Created by yiwenren on 11/30/16.
 */


public class SearchResult {
    private String Title;
    private String datePosted;
    private String URL;
    private String Location;
    private String education;
    private String ID;
    private String organizationName;

    public SearchResult(String ID, String Title, String datePosted, String URL, String Location, String education, String organizationName) {
        this.ID = ID;
        this.Title = Title;
        this.datePosted = datePosted;
        this.URL = URL;
        this.Location = Location;
        this.education = education;
        this.organizationName = organizationName;
    }

    public String getID() {
        return ID;
    }
    public void setID(String iD) {
        ID = iD;
    }
    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }
    public String getDatePosted() {
        return datePosted;
    }
    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }
    public String getURL() {
        return URL;
    }
    public void setURL(String uRL) {
        URL = uRL;
    }
    public String getLocation() {
        return Location;
    }
    public void setLocation(String location) {
        Location = location;
    }
    public String getEducation() {
        return education;
    }
    public void setEducation(String education) {
        this.education = education;
    }
    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

}

