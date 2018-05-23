package com.integrapp.integrapp.Adverts;

import org.json.JSONException;
import org.json.JSONObject;

public class DataAdvert {
    private String date;
    private String title;
    private String description;
    private String places;
    private String type;
    private String state;
    private String userId;
    private int image;
    private String id;

    DataAdvert(String date, String title, String description, String places, String type, String state, String userId, int image, String id) {
        this.date = date;
        this.title = title;
        this.description = description;
        this.places = places;
        this.type = type;
        this.state = state;
        this.image = image;
        this.userId = userId;
        this.id = id;
    }

    public DataAdvert(JSONObject userInfo, int image) {
        decryptJson(userInfo);
        this.image = image;
    }

    private void decryptJson(JSONObject userInfo) {
        try {

            this.date = userInfo.getString("date");
            this.title = userInfo.getString("title");
            this.description = userInfo.getString("description");
            this.places = userInfo.getString("places");
            this.type = "voluntary";
            this.state = userInfo.getString("state");
            this.id = userInfo.getString("_id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
