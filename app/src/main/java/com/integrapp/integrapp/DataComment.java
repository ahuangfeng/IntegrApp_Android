package com.integrapp.integrapp;

public class DataComment {

    private String id;
    private String userId;
    private String username;
    private String createdAt;
    private String content;
    private String forumId;

    public DataComment (String id, String userId, String username, String createdAt, String content, String forumId) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.createdAt = createdAt;
        this.content = content;
        this.forumId = forumId;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getContent() {
        return content;
    }

    public String getForumId() {
        return forumId;
    }
}
