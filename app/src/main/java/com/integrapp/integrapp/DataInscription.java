package com.integrapp.integrapp;

public class DataInscription {
    private String id;
    private String usernameOwner;
    private String idUser;
    private String state;

    public DataInscription(String id, String usernameOwner, String state, String idUser) {
        this.id = id;
        this.usernameOwner = usernameOwner;
        this.state = state;
        this.idUser = idUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
