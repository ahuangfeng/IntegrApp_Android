package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LogIn extends AppCompatActivity {

    Button logInButton;
    EditText userEditText;
    EditText passEditText;
    TextView signUpTextView;
    public static LogIn logInActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        logInActivity = this;

        logInButton = (Button) findViewById(R.id.logInButton);
        signUpTextView = (TextView) findViewById(R.id.signUpTextView);
        
        logInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                userEditText = (EditText) findViewById(R.id.userEditText);
                passEditText = (EditText) findViewById(R.id.passEditText);

                String user = userEditText.getText().toString();
                String pass = passEditText.getText().toString();
                if(checkFields(user, pass) == "Access") {
                    sendDataToServer(user, pass);
                }
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogIn.this, SignUp.class);
                startActivity(i);
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
                    return getServerResponse(json);
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
            Intent i = new Intent(LogIn.this, Advert.class);
            startActivity(i);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "Username or password are incorrect", Toast.LENGTH_SHORT).show();
        }
    }

    private String getServerResponse(String json) {
        HttpPost post = new HttpPost("https://integrappbackend.herokuapp.com/api/login");
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

    private String generateRequestRegister(String username, String password) throws JSONException {
        JSONObject oJSON = new JSONObject();

        oJSON.put("username", username);
        oJSON.put("password", password);
        return oJSON.toString(1);
    }

    private String checkFields(String user, String pass) {

        if (user.isEmpty() && pass.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username and password are needed", Toast.LENGTH_SHORT).show();
            return "Not access";
        }
        else if (!user.isEmpty() && pass.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Password is needed", Toast.LENGTH_SHORT).show();
            return "Not access";
        }
        else if (user.isEmpty() && !pass.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username is needed", Toast.LENGTH_SHORT).show();
            return "Not access";
        }
        else {
            Toast.makeText(getApplicationContext(), "Access successful", Toast.LENGTH_SHORT).show();
            return "Access";
        }
    }
}
