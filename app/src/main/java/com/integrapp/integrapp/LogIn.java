package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;

public class LogIn extends AppCompatActivity {
    
    private EditText userEditText;
    private EditText passEditText;
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        server = Server.getInstance();
        Button logInButton = findViewById(R.id.logInButton);
        TextView signUpTextView = findViewById(R.id.signUpTextView);

        logInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                userEditText = findViewById(R.id.userEditText);
                passEditText = findViewById(R.id.passEditText);

                String user = userEditText.getText().toString();
                String pass = passEditText.getText().toString();
                if(fieldsOk(user, pass)) {
                    Toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_SHORT).show();
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
                    System.out.println("SERVER RESPONSE: " + s);
                    checkLogIn(s);
                }
            }.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkLogIn(String s) {
        if(!s.equals("ERROR IN LOGIN")) {
            Toast.makeText(getApplicationContext(), "Log in successful", Toast.LENGTH_SHORT).show();

            SharedPreferences preferences = getSharedPreferences("login_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLogged", true);
            editor.putString("username", userEditText.getText().toString());
            String token = getTokenResponse(s);
            server.token = token;
            //System.out.println("TOKEN: " + token); //Per probar. Correcto!
            editor.putString("user_token", token);//--> Here we will save the token "DONE"
            editor.apply();

            Intent i = new Intent(LogIn.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "Username or password are incorrect", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTokenResponse(String s) {
        String myJsonString = s;
        try {
            JSONObject myJsonjObject = new JSONObject(myJsonString);
            myJsonString = myJsonjObject.getString("token");
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
