package com.integrapp.integrapp.Forum;

import com.integrapp.integrapp.UserDataAdvertiser;

public class ForumItem {

    private String id;
    private String type;
    private String title;
    private String description;
    private String miniDescription;
    private String createdAt;
    private String userId;
    private float rate;
    private UserDataAdvertiser user;

    public ForumItem (String id, String type, String title, String description, String miniDescription, String createdAt, String userId, float rate, String userObject) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.miniDescription = miniDescription;
        this.createdAt = createdAt;
        this.userId = userId;
        this.rate = rate;
        this.user = new UserDataAdvertiser(userObject);
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getMiniDescription() { return miniDescription; }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public float getRate() {
        return rate;
    }

    public UserDataAdvertiser getUser() {
        return user;
    }

    public void setUser(UserDataAdvertiser user) {
        this.user = user;
    }
}
