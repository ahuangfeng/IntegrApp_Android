package com.integrapp.integrapp.Login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.integrapp.integrapp.MainActivity;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;
import com.integrapp.integrapp.SignUp.SignUp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LogIn extends AppCompatActivity {
    
    private EditText userEditText;
    private EditText passEditText;
    private CheckBox rememberMe;
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        server = Server.getInstance();
        Button logInButton = findViewById(R.id.logInButton);
        TextView signUpTextView = findViewById(R.id.signUpTextView);
        rememberMe = findViewById(R.id.checkRememberMe);

        logInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                userEditText = findViewById(R.id.userEditText);
                passEditText = findViewById(R.id.passEditText);

                String user = userEditText.getText().toString();
                String pass = passEditText.getText().toString();
                if(fieldsOk(user, pass)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.connecting), Toast.LENGTH_SHORT).show();
                    sendDataToServer(user, pass);
                }
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogIn.this, SignUp.class);
                startActivity(i);
                finish();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void sendDataToServer(String username, String password) {
        try {
            final String json = generateRequestRegister(username, password);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    return server.login(json);
                }

                @Override
                protected void onPostExecute(String s) {
                    checkLogIn(s);
                }
            }.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkLogIn(String s) {
        if(!s.equals("ERROR IN LOGIN")) {
            Toast.makeText(getApplicationContext(), getString(R.string.success_login), Toast.LENGTH_SHORT).show();
            String token = getTokenResponse(s);
            server.token = token;
            SharedPreferences preferences = getSharedPreferences("login_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if (rememberMe.isChecked()) {
                editor.putBoolean("isLogged", true);
            }
            editor.putString("username", userEditText.getText().toString());
            editor.putString("password", passEditText.getText().toString());

            editor.putString("user_token", token);//--> Here we will save the token "DONE"
            editor.apply();

            doServerCallForSaveInfoUser();//otra async task para obtener los datos del usuario
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.login_error), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void doServerCallForSaveInfoUser() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                String username = preferences.getString("username", "username");
                return server.getUserInfoByUsername(username);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GET INFO USER")) {
                    saveInfoUser(s);
                }
                else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_GettingUserInfo), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void doServerCallForSaveInscriptions(String userId) {
        final String idUser = userId;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.getInscriptionsByUserId(idUser);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING INSCRIPTIONS")) {
                    saveInscriptions(s);
                }
                else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_GettingInscriptions), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void saveInscriptions(String inscriptions) {
        SharedPreferences preferences = getSharedPreferences("login_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("inscriptions", inscriptions);
        editor.apply();
    }

    private void saveInfoUser(String s) {
        try {
            JSONObject myJsonObject = new JSONObject(s);
            String userId = myJsonObject.getString("_id");
            String username = myJsonObject.getString("username");
            String type = myJsonObject.getString("type");
            String name = myJsonObject.getString("name");
            String email = "No e-mail";
            String phone = "No phone";
            if(myJsonObject.has("email")) {
                email = myJsonObject.getString("email");
            }
            if(myJsonObject.has("phone")) {
                phone = myJsonObject.getString("phone");
            }
            String path;
            if(myJsonObject.has("imagePath")) {
                String path2 = myJsonObject.getString("imagePath");
                if (path2.equals("null")) {
                    path = "";
                } else {
                    path = path2;
                }
            } else {
                path = "";
            }
            System.out.println("putogordomarcoval "+path);

            String rate = myJsonObject.getString("rate");
            JSONObject myJsonRate = new JSONObject(rate);
            int likes = myJsonRate.getInt("likes");
            int dislikes = myJsonRate.getInt("dislikes");

            JSONArray myJsonArrayAds = myJsonObject.getJSONArray("adverts");

            SharedPreferences preferences = getSharedPreferences("login_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("idUser", userId);
            editor.putString("username", username);
            editor.putString("type", type);
            editor.putString("name", name);
            editor.putString("email", email);
            editor.putString("phone", phone);
            editor.putInt("likes", likes);
            editor.putInt("dislikes", dislikes);
            editor.putInt("ads", myJsonArrayAds.length());
            editor.putString("path", path);
            editor.apply();

            doServerCallForSaveInscriptions(userId);

            Intent i = new Intent(LogIn.this, MainActivity.class);
            startActivity(i);
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getTokenResponse(String s) {
        String myJsonString = s;
        try {
            JSONObject myJsonObject = new JSONObject(myJsonString);
            myJsonString = myJsonObject.getString("token");
            return myJsonString;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "ERROR TO GET TOKEN";
    }

    private String generateRequestRegister(String username, String password) throws JSONException {
        JSONObject oJSON = new JSONObject();

        oJSON.put("username", username);
        oJSON.put("password", password);
        return oJSON.toString(1);
    }

    private boolean fieldsOk(String user, String pass) {
        boolean valid = true;
        if (user.isEmpty()) {
            userEditText.setError(getString(R.string.error_username_empty));
            valid = false;
        }

        if (pass.isEmpty()) {
            passEditText.setError(getString(R.string.error_password_empty));
            valid = false;
        }

        return valid;
    }
}
