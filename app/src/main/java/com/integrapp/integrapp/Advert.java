package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Advert extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert);
        this.server = Server.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getAllAdverts(); //Show adverts


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressLint("StaticFieldLeak")
    private void getAllAdverts() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                System.out.println("TOKEN :" + server.token); //correcto
                SharedPreferences preferences = getSharedPreferences("login_data", Context.MODE_PRIVATE);
                String token = preferences.getString("user_token", "user_token");
                server.token = token;
                System.out.println("TOKEN PREFERENCES: " + token);
                return server.getAllAdverts();
            }

            @Override
            protected void onPostExecute(String s) {
                System.out.println("ADVERTS : " +s);
                if (!s.equals("ERROR IN GETTING ALL ADVERTS")) {
                    //Anuncios en formato Json en el primer textView (advertTextView)
                    //TextView advertTextView = findViewById(R.id.advertTextView);
                    //advertTextView.setText(s);
                    //TODO: Mostrarlos para el usuario (diseño)
                    /*Cada posición de 'atributes' contiene un array de 5 posiciones por cada
                    uno de los atributos añadidos en la funcion getAttributesAdvert, si se
                    necesitan más atributos solo hay que agregarlos en dicha funcion de la misma
                    manera que estos 5.*/
                    ArrayList<ArrayList<String>> attributes = getAttributesAllAdverts(s);
                    //Anuncios en el formato que queremos listo para ponerlos en el diseño
                    putAttributesInTextView(attributes);

                    //Preparación del diseño

                    LinearLayout contentAdvert = findViewById(R.id.includeContentAdvert);
                    ListView list;
                    list = contentAdvert.findViewById(R.id.sampleListView);
                    ArrayAdapter<String> adapter;
                    ArrayList<String> adverts = new ArrayList<String>();

                    ////////////////////////////////////////////////////////////////
                    String advert;
                    for (int i=0; i< attributes.size(); ++i) {
                        advert = "";

                        advert += adverts.add(attributes.get(i).get(0));
                        advert += adverts.add(attributes.get(i).get(1));
                        advert += adverts.add(attributes.get(i).get(2));
                        advert += adverts.add(attributes.get(i).get(3));
                        advert += adverts.add(attributes.get(i).get(4));
                        adverts.add(advert);
                        advert = "";
                    }

                    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, adverts);
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    ////////////////////////////////////////////////////////////////

                }
                else {
                    //TODO: Hacer que el token se mantenga. Solo funciona con el remember pero no si matamos la aplicacion.
                    Toast.makeText(getApplicationContext(), "Your token has expired, please login", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Advert.this, LogIn.class);
                    startActivity(i);
                    finish();
                }
            }
        }.execute();
    }

    /*En esta funcion se tienen que ir poniendo en la interfaz todos los anuncios
    * con sus respectivos atributos de la forma que se decida.*/
    private void putAttributesInTextView(ArrayList<ArrayList<String>> attributes) {
        /*TextView advertTextView2 = findViewById(R.id.advertTextView2);
        advertTextView2.setText("Atributos de los anuncios" + Html.fromHtml("<br />"));
        for(int i  = 0;i < attributes.size(); ++i) {
            advertTextView2.setText(advertTextView2.getText() + "Advert " + i + Html.fromHtml("<br />"));
            for (int j = 0; j < attributes.get(i).size(); ++j) {
                advertTextView2.setText(advertTextView2.getText() + attributes.get(i).get(j) + " / ");
            }
            advertTextView2.setText(advertTextView2.getText() + " " +Html.fromHtml("<br />"));
        }*/
    }

    private ArrayList<ArrayList<String>> getAttributesAllAdverts(String stringJson) {
        ArrayList<ArrayList<String>> attributes = new ArrayList<>();
        try {
            JSONArray myJsonjArray = new JSONArray(stringJson);
            for (int i = 0; i < myJsonjArray.length(); ++i) {
                System.out.println("json: " + i + " "+ myJsonjArray.getString(i));
                ArrayList<String> attributesAdd = getAttributesAdvert(myJsonjArray, i); //atributos de un anuncio
                attributes.add(attributesAdd);
            }
            return attributes;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<String> getAttributesAdvert(JSONArray myJsonjArray, int index) throws JSONException {
        ArrayList<String> attributesAdd = new ArrayList<>();
        String advertString = myJsonjArray.getString(index);
        JSONObject myJsonjObject = new JSONObject(advertString);

        /*Aqui puedes añadir otro atributo de la respuesta Json del servidor
        si es necesario para mostrarlo en el diseño. De momento solo estan estos
        5 atributos, (date, tittle, description, places, typeAdvert*/
        attributesAdd.add(myJsonjObject.getString("date"));
        attributesAdd.add(myJsonjObject.getString("title"));
        attributesAdd.add(myJsonjObject.getString("description"));
        attributesAdd.add(myJsonjObject.getString("places"));
        attributesAdd.add(myJsonjObject.getString("typeAdvert"));

        return attributesAdd;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.advert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent i = new Intent(Advert.this, Profile.class);
            startActivity(i);

        } else if (id == R.id.nav_adverts) {

        } else if (id == R.id.nav_forum) {

        } else if (id == R.id.nav_chats) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_aboutUs) {

        } else if (id == R.id.nav_logOut) {
            SharedPreferences preferences = getSharedPreferences("login_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}


