package com.integrapp.integrapp.Model;

public class DataInscription {
    private String id;
    private String info;
    private String idUser;
    private String idAdvert;
    private String status;

    public DataInscription(String id, String info, String status, String idUser, String idAdvert) {
        this.id = id;
        this.info = info;
        this.status = status;
        this.idUser = idUser;
        this.idAdvert = idAdvert;
    }

    public DataInscription(String id, String info, String status, String idUser) {
        this.id = id;
        this.info = info;
        this.status = status;
        this.idUser = idUser;
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
        return status;
    }

    public void setState(String state) {
        this.status = state;
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
