package com.integrapp.integrapp;

public class DataInscription {
    private String id;
    private String info;
    private String idUser;
    private String idAdvert;
    private String state;

    DataInscription(String id, String info, String state, String idUser, String idAdvert) {
        this.id = id;
        this.info = info;
        this.state = state;
        this.idUser = idUser;
        this.idAdvert = idAdvert;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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

    public String getIdAdvert() {
        return idAdvert;
    }

    public void setIdAdvert(String idAdvert) {
        this.idAdvert = idAdvert;
    }
}
