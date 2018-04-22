package com.integrapp.integrapp;

public class DataAdvert {
    private String date;
    private String title;
    private String description;
    private String places;
    private String type;
    private String state;
    private String createdAt;
    private int image;

    public DataAdvert(String date, String title, String description, String places, String type, String state, String createdAt, int image) {
        this.date = date;
        this.title = title;
        this.description = description;
        this.places = places;
        this.type = type;
        this.state = state;
        this.createdAt = createdAt;
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaces() {
        return places;
    }

    public void setPlaces(String places) {
        this.places = places;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
