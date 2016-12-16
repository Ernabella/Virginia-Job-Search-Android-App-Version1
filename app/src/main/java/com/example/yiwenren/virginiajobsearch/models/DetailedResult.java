package com.example.yiwenren.virginiajobsearch.models;

/**
 * Created by yiwenren on 12/1/16.
 */

public class DetailedResult {

    private String Title;
    private String datePosted;
    private String URL;
    private String Location;
    //private String education;
    private String ID;
    private String organizationName;
    private String jobDescription;

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
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

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }



    public DetailedResult(String title, String datePosted, String URL, String location, String ID, String organizationName, String jobDescription) {
        Title = title;
        this.datePosted = datePosted;
        this.URL = URL;
        Location = location;
        this.ID = ID;
        this.organizationName = organizationName;
        this.jobDescription = jobDescription;
    }




}
