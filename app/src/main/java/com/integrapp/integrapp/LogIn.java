package com.integrapp.integrapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {

    Button logInButton;
    Button signUpButton;
    EditText userEditText;
    EditText passEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        logInButton = (Button) findViewById(R.id.logInButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);


        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields() == "Access") {
                    Intent i = new Intent(LogIn.this, Advert.class);
                    startActivity(i);
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields() == "Access") {
                    Intent i = new Intent(LogIn.this, Advert.class);
                    startActivity(i);
                }
            }
        });
    }

    private String checkFields() {
        userEditText = (EditText) findViewById(R.id.userEditText);
        passEditText = (EditText) findViewById(R.id.passEditText);

        String user = userEditText.getText().toString();
        String pass = passEditText.getText().toString();

        if (user.isEmpty() && pass.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username and password is needed", Toast.LENGTH_SHORT).show();
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
