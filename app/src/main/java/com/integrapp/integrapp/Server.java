package com.integrapp.integrapp;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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
        return "ERROR IN GETTING ALL ADVERTS";
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

    public String setNewAdvert(String json) {
        HttpPost post = new HttpPost(API_URI+"/advert");
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("x-access-token", token);
            post.setHeader("Content-type", "application/json");

            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(post, handler);
        
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR CREATING ADVERT";
    }

    public String deleteUserById(String id) {
        HttpDelete delete = new HttpDelete(API_URI+"/user/"+id);
        try {
            delete.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(delete, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN DELETING USER";
    }

    public String deleteAdvertById(String id) {
        HttpDelete delete = new HttpDelete(API_URI+"/advert/"+id);
        try {
            delete.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(delete, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN DELETING ADVERT";
    }

    public String modifyProfileById(String id, String json) {
        HttpPut modify = new HttpPut(API_URI+"/user/"+id);
        try {
            StringEntity entity = new StringEntity(json);
            modify.setEntity(entity);
            modify.setHeader("x-access-token", token);
            modify.setHeader("Content-type", "application/json");

            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(modify, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR MODIFY PROFILE";
    }

    public String voteLikeUser(String idUser) {
        HttpPost post = new HttpPost(API_URI+"/like/"+idUser);
        try {
            post.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(post, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN LIKE VOTE";
    }

    public String voteDislikeUser(String idUser) {
        HttpPost post = new HttpPost(API_URI+"/dislike/"+idUser);
        try {
            post.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(post, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN DISLIKE VOTE";
    }

    public String inscriptionAdvert(String json) {
        HttpPost post = new HttpPost(API_URI+"/inscription");
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("x-access-token", token);
            post.setHeader("Content-type", "application/json");

            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(post, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR CREATING ADVERT";
    }

    /*public String getUserInfoByUsername(String username) {
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
    }*/

}
