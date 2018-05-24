package com.integrapp.integrapp.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class UserDataAdvertiser {

    private String idUser;
    private String username;
    private String name;
    private String type;
    private String email = "No e-mail";
    private String phone = "No phone";

    public UserDataAdvertiser(String userInfo) {
        decryptJson(userInfo);
    }

    public UserDataAdvertiser(String id, String userInfo) {
        decryptJson2(id, userInfo);
    }

    public UserDataAdvertiser(String id, String username, String name, String type, String email, String phone) {
        idUser = id;
        this.username = username;
        this.name = name;
        this.type = "voluntary";
        if (!email.equals("")) this.email = email;
        if (!phone.equals("")) this.phone = phone;
    }

    private void decryptJson(String userInfo) {
        try {
            JSONObject myJsonObject = new JSONObject(userInfo);

            this.idUser = myJsonObject.getString("_id");
            this.username = myJsonObject.getString("username");
            this.name = myJsonObject.getString("name");
            this.type = myJsonObject.getString("type");

            if(myJsonObject.has("email")) {
                this.email = myJsonObject.getString("email");
            }
            if(myJsonObject.has("phone")) {
                this.phone = myJsonObject.getString("phone");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void decryptJson2(String id, String userInfo) {
        try {
            JSONObject myJsonObject = new JSONObject(userInfo);

            this.idUser = id;
            this.username = myJsonObject.getString("username");
            this.name = myJsonObject.getString("name");
            this.type = myJsonObject.getString("type");

            if(myJsonObject.has("email")) {
                this.email = myJsonObject.getString("email");
            }
            if(myJsonObject.has("phone")) {
                this.phone = myJsonObject.getString("phone");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
