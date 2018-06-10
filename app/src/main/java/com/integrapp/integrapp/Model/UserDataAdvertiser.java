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
    private String path;

    UserDataAdvertiser(JSONObject userInfo) {
        decryptJson(userInfo);
    }

    UserDataAdvertiser(String s) {
        try {
            JSONObject userInfo = new JSONObject(s);
            decryptJson(userInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public UserDataAdvertiser(String id, String userInfo) {
        decryptJson2(id, userInfo);
    }

    private void decryptJson(JSONObject userInfo) {
        try {
            this.idUser = userInfo.getString("_id");
            this.username = userInfo.getString("username");
            this.name = userInfo.getString("name");
            this.type = userInfo.getString("type");

            if(userInfo.has("email")) {
                this.email = userInfo.getString("email");
            }
            if(userInfo.has("phone")) {
                this.phone = userInfo.getString("phone");
            }
            if(userInfo.has("imagePath")) {
                String path2 = userInfo.getString("imagePath");
                if (path2.equals("null")) {
                    this.path = "";
                } else {
                    this.path = path2;
                }
            } else {
                this.path = "";
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
            if(myJsonObject.has("imagePath")) {
                String path2 = myJsonObject.getString("imagePath");
                if (path2.equals("null")) {
                    this.path = "";
                } else {
                    this.path = path2;
                }
            } else {
                this.path = "";
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
