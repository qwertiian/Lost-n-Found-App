package com.example.lostandfound;

public class Item {
    private long id;
    private String name;
    private String description;
    private String date;
    private String location;
    private String image;
    private String status; // "lost", "claimed", "returned"
    private long reporterId;
    private long claimerId;
    private String claimProof;

    // Constructors
    public Item() {
    }

    public Item(long id, String name, String description, String date, String location,
                String image, String status, long reporterId, long claimerId, String claimProof) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.location = location;
        this.image = image;
        this.status = status;
        this.reporterId = reporterId;
        this.claimerId = claimerId;
        this.claimProof = claimProof;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getReporterId() {
        return reporterId;
    }

    public void setReporterId(long reporterId) {
        this.reporterId = reporterId;
    }

    public long getClaimerId() {
        return claimerId;
    }

    public void setClaimerId(long claimerId) {
        this.claimerId = claimerId;
    }

    public String getClaimProof() {
        return claimProof;
    }

    public void setClaimProof(String claimProof) {
        this.claimProof = claimProof;
    }
}