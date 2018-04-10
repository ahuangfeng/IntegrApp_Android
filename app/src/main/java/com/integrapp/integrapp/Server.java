package com.integrapp.integrapp;

import org.apache.http.client.methods.HttpGet;
    import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;


class Server {
    private static final Server serverInstance = new Server();
    private static final String API_URI = "https://integrappbackend.herokuapp.com/api";

    public String token; //NO privado porque no podemos acceder a el desde los metodos doInBackground y onPostExecute de las AsynTasck

    static Server getInstance() {
        return serverInstance;
    }

    private Server() {
    }

    public String register(String json) {
        HttpPost post = new HttpPost(API_URI+"/register");
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");

            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(post, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN SIGN UP";
    }

    public String login(String json) {
        HttpPost post = new HttpPost(API_URI+"/login");
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");

            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(post, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN LOGIN";
    }

    public String getAllAdverts() {
        HttpGet get = new HttpGet(API_URI+"/advert?type=");
        try {
            get.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GET ALL ADVERTS";
    }

    public String getUserInfoByUsername(String username) {
        HttpGet get = new HttpGet(API_URI+"/user?username="+username);
        try {
            get.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GET INFO USER";
    }
}
