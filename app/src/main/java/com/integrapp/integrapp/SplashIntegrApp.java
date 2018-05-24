package com.integrapp.integrapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.integrapp.integrapp.Login.LogIn;

public class SplashIntegrApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_integr_app);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                SharedPreferences preferences = getSharedPreferences("login_data", Context.MODE_PRIVATE);
                boolean isLogged = preferences.getBoolean("isLogged", false);

                if (isLogged) i = new Intent(SplashIntegrApp.this, MainActivity.class);
                else i = new Intent(SplashIntegrApp.this, LogIn.class);
                startActivity(i);
                finish();
            }
        },1000);
    }
}
