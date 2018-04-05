package com.integrapp.integrapp;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by alexhuang05 on 5/04/18.
 */

class Server {
    private static final Server serverInstance = new Server();
    private static final String API_URI = "https://integrappbackend.herokuapp.com/api";

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
        return "ERROR IN SIGNUP";
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


}
