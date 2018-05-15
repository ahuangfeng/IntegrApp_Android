package com.integrapp.integrapp;

public class ForumItem {

    private long id;
    private String type;
    private String title;
    private String description;
    private String createdAt;
    private long userId;
    private float rate;

    public ForumItem (long id, String type, String title, String description, String createdAt, long userId, float rate) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.userId = userId;
        this.rate = rate;
    }

    public String getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public long getUserId() {
        return userId;
    }

    public float getRate() {
        return rate;
    }
}
