package com.integrapp.integrapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences preferences = getSharedPreferences("fields_profile", Context.MODE_PRIVATE);
        String user = preferences.getString("username", "username");
        String name = preferences.getString("full_name", "full_name");
        String type_user = preferences.getString("type_user", "type_user");

        System.out.println("name " + name);
        System.out.println("name " + user);
        System.out.println("name " + type_user);

        /*Es mejor si hacemos una petición al servidor para obtener los datos del usuario
         * en lugar de pillarlos en el momento de hacer signup, ya que si hacemos log in con otro
         * usuario, las preferences son incorrectas porque tienen los datos del ultimo usuario
         * en registrarse, por lo que el Perfil del usuario no seria el suyo.
         * TODO: Una vez hecho el login y obtenidos los datos del servidor entonces podemos hacer preferences*/

        TextView nameTextView = findViewById(R.id.nameTextView);
        nameTextView.setText(name);

        TextView usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(user);

        TextView typeUserTextView = findViewById(R.id.typeUserTextView);
        typeUserTextView.setText(type_user);

        TextView emailTextView = findViewById(R.id.emailTextView);
        emailTextView.setText(user + "@gmail.com");
    }
}
