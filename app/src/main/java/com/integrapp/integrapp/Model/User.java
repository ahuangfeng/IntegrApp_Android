package com.integrapp.integrapp.Model;

public class User {
    private String id;
    private String username;
    private String name;
    private String type;
    private String path;

    public User (String id, String username, String name, String type, String path) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.type = type;
        this.path = path;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
