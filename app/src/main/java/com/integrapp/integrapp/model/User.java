package com.integrapp.integrapp.model;

public class User {
    private String id;
    private String username;
    private String name;
    private String type;

    public User (String id, String username, String name, String type) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.type = type;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
