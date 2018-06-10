package com.integrapp.integrapp.Model;

import android.graphics.drawable.Drawable;

import com.integrapp.integrapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Advert {
    private String date;
    private String title;
    private String description;
    private String places;
    private String type;
    private String state;
    private int image;
    private String path;
    private String id;
    private String registered;
    private UserDataAdvertiser userDataAdvertiser;

    public Advert(JSONObject dataAdvert) {
        decryptJson(dataAdvert);
    }

    private void decryptJson(JSONObject dataAdvert) {
        try {
            this.date = dataAdvert.getString("date");
            this.title = dataAdvert.getString("title");
            this.description = dataAdvert.getString("description");
            this.places = dataAdvert.getString("places");
            this.type = dataAdvert.getString("typeAdvert");
            this.state = dataAdvert.getString("state");
            this.id = dataAdvert.getString("_id");
            this.registered = dataAdvert.getString("registered");
            if(dataAdvert.has("user")){
                this.userDataAdvertiser = new UserDataAdvertiser(dataAdvert.getJSONObject("user"));
            }
            if (dataAdvert.has("imagePath")) {
                this.path = dataAdvert.getString("imagePath");
                this.image = -1;
            } else {
                this.path = "";
                this.image = R.drawable.project_preview_large_2;
            }

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

    public String getRegistered() {return registered;}

    public UserDataAdvertiser getUserDataAdvertiser() {
        return userDataAdvertiser;
    }

    public void setUserDataAdvertiser(UserDataAdvertiser userDataAdvertiser) {
        this.userDataAdvertiser = userDataAdvertiser;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
