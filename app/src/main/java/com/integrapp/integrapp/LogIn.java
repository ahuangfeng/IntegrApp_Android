package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class LogIn extends AppCompatActivity {

    Button logInButton;
    EditText userEditText;
    EditText passEditText;
    TextView signUpTextView;
    public LogIn logInActivity;
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        logInActivity = this;
        server = Server.getInstance();
        logInButton = findViewById(R.id.logInButton);
        signUpTextView = findViewById(R.id.signUpTextView);

        logInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                userEditText = findViewById(R.id.userEditText);
                passEditText = findViewById(R.id.passEditText);

                String user = userEditText.getText().toString();
                String pass = passEditText.getText().toString();
                if(fieldsOk(user, pass)) {
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
            Intent i = new Intent(LogIn.this, Advert.class);
            startActivity(i);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "Username or password are incorrect", Toast.LENGTH_SHORT).show();
        }
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
