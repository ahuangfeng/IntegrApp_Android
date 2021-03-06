package com.integrapp.integrapp;

import com.android.internal.http.multipart.MultipartEntity;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final Server serverInstance = new Server();
    private static final String API_URI = "https://integrappbackend.herokuapp.com/api";

    public String token; //NO privado porque no podemos acceder a el desde los metodos doInBackground y onPostExecute de las AsynTasck

    private DefaultHttpClient client;

    public static Server getInstance() {
        return serverInstance;
    }

    private Server() {
        client = new DefaultHttpClient();
    }

    public String register(String json) {
        HttpPost post = new HttpPost(API_URI+"/register");
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");

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
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GET INFO USER";
    }

    public String getAdvertInfoById(String id) {
        HttpGet get = new HttpGet(API_URI+"/advert/"+id);
        try {
            get.setHeader("x-access-token", token);
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GET INFO ADVERT";
    }

    public String setNewAdvert(String json) {
        HttpPost post = new HttpPost(API_URI+"/advert");
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("x-access-token", token);
            post.setHeader("Content-type", "application/json");

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
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(delete, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "ERROR IN DELETING USER";
    }

    public String deleteImageUserById(String id) {
        HttpDelete delete = new HttpDelete(API_URI+"/image/"+id);
        try {
            delete.setHeader("x-access-token", token);
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(delete, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "ERROR IN DELETING IMAGE USER";
    }

    public String deleteImageAdvertById(String id) {
        HttpDelete delete = new HttpDelete(API_URI+"/advert/image/"+id);
        try {
            delete.setHeader("x-access-token", token);
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(delete, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "ERROR IN DELETING IMAGE ADVERT";
    }

    public String deleteAdvertById(String id) {
        HttpDelete delete = new HttpDelete(API_URI+"/advert/"+id);
        try {
            delete.setHeader("x-access-token", token);
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
            System.out.println("IDINSCRIPTION " + idInscription + " .... + ");
            delete.setHeader("x-access-token", token);
            BasicResponseHandler handler = new BasicResponseHandler();
            DefaultHttpClient client = new DefaultHttpClient();
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

            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(modify, handler);
          
         } catch (IOException e) {
            e.printStackTrace();
         }
         return "ERROR IN SET STATUS INSCRIPTION";
    }
  
    public String getCommentsForum(String id) {
        HttpGet get = new HttpGet(API_URI+"/fullForum/"+id);
        try {
            get.setHeader("x-access-token", token);
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GETTING COMMENTS FORUM";
    }
  
    public String createCommentForum(String json) {
        HttpPost post = new HttpPost(API_URI+"/commentForum");
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("x-access-token", token);
            post.setHeader("Content-type", "application/json");

            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(post, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN COMMENTING FORUM";
    }

    public String postNewForum(String json) {
        HttpPost post = new HttpPost(API_URI+"/forum");
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("x-access-token", token);
            post.setHeader("Content-Type", "application/json");
          
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(post, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
      return "ERROR CREATING FORUM";
    }

    public String deleteCommentById(String id) {
        HttpDelete delete = new HttpDelete(API_URI+"/commentForum/"+id);
        try {
            delete.setHeader("x-access-token", token);
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(delete, handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN DELETING COMMENT";
    }

    public String getAllUserInscriptions(String userId) {
        HttpGet get = new HttpGet(API_URI+"/inscriptionsUser/"+userId);
        try {
            get.setHeader("x-access-token", token);
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GETTING ALL INSCRIPTIONS";
    }

    public String getUsers() {
        HttpGet get = new HttpGet(API_URI+"/users");
        try {
            get.setHeader("x-access-token", token);
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GETTING ALL USERS";
    }

    public String getChats(String userId) {
        HttpGet get = new HttpGet(API_URI+"/chat/"+userId);
        try {
            get.setHeader("x-access-token", token);
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GETTING CHATS";
    }

    public String report(String json) {
        HttpPost post = new HttpPost(API_URI+"/report");
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("x-access-token", token);
            post.setHeader("Content-type", "application/json");

            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(post, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR CREATING REPORT";
    }

    public String modifyForumById(String id, String json) {
        HttpPut modify = new HttpPut(API_URI+"/forum/"+id);
        try {
            StringEntity entity = new StringEntity(json);
            modify.setEntity(entity);
            modify.setHeader("x-access-token", token);
            modify.setHeader("Content-type", "application/json");

            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(modify, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "ERROR MODIFY FORUM";
    }

    public String voteForum(String forumId, String json) {
        HttpPut put = new HttpPut(API_URI+"/forum/" + forumId + "/vote");
        System.out.println("json: " + json);
        try {
            StringEntity entity = new StringEntity(json);
            put.setEntity(entity);
            put.setHeader("x-access-token", token);
            put.setHeader("Content-type", "application/json");
            BasicResponseHandler handler = new BasicResponseHandler();

            return client.execute(put, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR VOTING FORUM";
    }


    public String getNewMessages(String userId) {
        HttpGet get = new HttpGet(API_URI+"/newChats/"+userId);
        try {
            get.setHeader("x-access-token", token);
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GETTING NUMBER OF NEW MESSAGES";
    } 

    public String getImageUser(String userId) {
        HttpGet get = new HttpGet(API_URI+"/image/"+userId);
        try {
            get.setHeader("x-access-token", token);
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GETTING IMAGE";
    }

    public String addPhotoUser(File f) {
        System.out.println("fileeee: " + f.getAbsoluteFile());
        Map config = new HashMap();
        config.put("cloud_name", "hlcivcine");
        config.put("api_key", "158434689396546");
        config.put("api_secret", "LfgGuWmq3OGj-2HmYTo0p7Xa5CE");

        Map args = new HashMap();
        args.put("folder", "/users");

        Cloudinary cloudinary = new Cloudinary(config);
        try {
            Map map = cloudinary.uploader().upload(f.getAbsolutePath(), args);
            Object o = map.get("public_id");
            String public_id = o.toString();
            o = map.get("url");
            String url = o.toString();
            System.out.println("public_iddddd: " + public_id + " urlllllll: " + url);

            HttpPost post = new HttpPost(API_URI+"/imageUpload");
            JSONObject oJSON = new JSONObject();
            oJSON.put("public_id", public_id);
            oJSON.put("url", url);
            String json = oJSON.toString(1);
            System.out.println("json: " + json);

            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("x-access-token", token);
            post.setHeader("Content-type", "application/json");
            BasicResponseHandler handler = new BasicResponseHandler();

            return client.execute(post, handler);

        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return "ERROR UPDATING PHOTO";
    }

    public String addPhotoAdvert(File f, String advertid) {
        System.out.println("fileeee: " + f.getAbsoluteFile());
        Map config = new HashMap();
        config.put("cloud_name", "hlcivcine");
        config.put("api_key", "158434689396546");
        config.put("api_secret", "LfgGuWmq3OGj-2HmYTo0p7Xa5CE");

        Map args = new HashMap();
        args.put("folder", "/adverts");

        Cloudinary cloudinary = new Cloudinary(config);
        try {
            Map map = cloudinary.uploader().upload(f.getAbsolutePath(), args);
            Object o = map.get("public_id");
            String public_id = o.toString();
            o = map.get("url");
            String url = o.toString();
            System.out.println("public_iddddd: " + public_id + " urlllllll: " + url);

            HttpPost post = new HttpPost(API_URI+"/advert/imageUpload/"+advertid);
            JSONObject oJSON = new JSONObject();
            oJSON.put("public_id", public_id);
            oJSON.put("url", url);
            String json = oJSON.toString(1);
            System.out.println("json: " + json);

            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("x-access-token", token);
            post.setHeader("Content-type", "application/json");
            BasicResponseHandler handler = new BasicResponseHandler();

            return client.execute(post, handler);

        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return "ERROR UPDATING PHOTO";
    }


    public String getImageAdvert(String advertId) {
        HttpGet get = new HttpGet(API_URI+"/advert/image/"+advertId);
        try {
            get.setHeader("x-access-token", token);
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(get, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN GETTING IMAGE";
    }
}
