package com.integrapp.integrapp;

public class ForumItem {

    private long id;
    private String type;
    private String title;
    private String description;
    private String createdAt;
    private long userId;
    private float rate;
    private int total;

    public ForumItem (long id, String type, String title, String description, String createdAt, long userId, float rate) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.userId = userId;
        this.rate = 4.5f;
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

    public void vote(int rate) {
        total = total+1;
        this.rate = (this.rate + rate)/total;
    }

    public float getRate() {
        return rate;
    }
}
