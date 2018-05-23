package com.integrapp.integrapp.Inscription;

public class DataInscription {
    private String id;
    private String advertTitle;
    private String usernameOwner;
    private String state;

    public DataInscription(String id, String advertTitle, String usernameOwner, String state) {
        this.id = id;
        this.advertTitle = advertTitle;
        this.usernameOwner = usernameOwner;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdvertTitle() {
        return advertTitle;
    }

    public void setAdvertTitle(String advertTitle) {
        this.advertTitle = advertTitle;
    }

    public String getUsernameOwner() {
        return usernameOwner;
    }

    public void setUsernameOwner(String usernameOwner) {
        this.usernameOwner = usernameOwner;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
