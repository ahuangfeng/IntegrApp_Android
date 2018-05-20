package com.integrapp.integrapp;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;
import java.net.HttpURLConnection;

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

    public String getAllAdverts(String type) {
        HttpGet get = new HttpGet(API_URI+"/advert?type="+type);
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

    public String getAllInscriptions(String idAdvert) {
        HttpGet get = new HttpGet(API_URI+"/inscription/"+idAdvert);
        try {
            get.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GETTING ALL INSCRIPTIONS";
    }

    public String getAllUserAdverts(String type) {
        HttpGet get = new HttpGet(API_URI+"/advertsUser/"+type);
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

    public String getUserInfoById(String id) {
        HttpGet get = new HttpGet(API_URI+"/userInfoById/"+id);
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

    public String modifyAdvertById(String id, String json) {
        HttpPut modify = new HttpPut(API_URI+"/advert/"+id);
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
        return "ERROR MODIFY ADVERT";
    }

    public String modifyStateAdvertById (String id, String json) {
        HttpPut modify = new HttpPut(API_URI+"/advertState/"+id);
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
        return "ERROR CHANGE ADVERT STATE";
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


    public String createInscriptionAdvert(String json) {
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
        return "ERROR CREATING INSCRIPTION";
    }

    public String deleteInscriptionAdvert(String idInscription) {
        HttpDelete delete = new HttpDelete(API_URI+"/inscription/"+idInscription);
        try {
            delete.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(delete, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR DELETING INSCRIPTION";
    }
  
    public String getInscriptionsByUserId(String userId) {
        HttpGet get = new HttpGet(API_URI+"/inscriptionsUser/"+userId);
        try {
            get.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);
          
          } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GETTING INSCRIPTIONS";
    }
  
  public String getForumDocu() {
        HttpGet get = new HttpGet(API_URI+"/forums?type=documentation");
        try {
            get.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);
          
          } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GETTING FORUM(DOCUMENTATION)";
  }
  
  public String getForumEntre() {
        HttpGet get = new HttpGet(API_URI+"/forums?type=entertainment");
        try {
            get.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GETTING FORUM(ENTERTAINMENT)";
    }
  
  public String getForumLang() {
        HttpGet get = new HttpGet(API_URI+"/forums?type=language");

        try {
            get.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "ERROR IN GETTING FORUM(LANGUAGE)";
    }
  
  public String getForumOther() {
        HttpGet get = new HttpGet(API_URI+"/forums?type=various");
        try {
            get.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GETTING FORUM(VARIOUS)";
    }


    public String setInscriptionStatus(String idAdvert, String json) {
        HttpPut modify = new HttpPut(API_URI+"/inscription/"+idAdvert);
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
        return "ERROR IN SET STATUS INSCRIPTION";
    }

    public String getAllUserInscriptions(String userId) {
        HttpGet get = new HttpGet(API_URI+"/inscriptionsUser/"+userId);
        try {
            get.setHeader("x-access-token", token);
            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GETTING ALL INSCRIPTIONS";
    }
}
