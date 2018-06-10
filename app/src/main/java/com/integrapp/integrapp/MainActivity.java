package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.integrapp.integrapp.Adverts.AdvertsFragment;
import com.integrapp.integrapp.Chat.MainChatsFragment;
import com.integrapp.integrapp.Forum.ForumFragment;
import com.integrapp.integrapp.Inscription.InscriptionsFragment;
import com.integrapp.integrapp.Login.LogIn;
import com.integrapp.integrapp.Profile.ProfileFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences preferences;
    private TextView numberChatsShow;
    private String newMessages = null;
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        server = Server.getInstance();
        setContentView(R.layout.activity_main);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.screen_area, new AdvertsFragment());
        ft.commit();



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView headerName = header.findViewById(R.id.headerName);
        TextView headerEmail = header.findViewById(R.id.headerEmail);

        LinearLayout linearLayout = header.findViewById(R.id.headerLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLikesDislikes();
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        SharedPreferences preferences = getSharedPreferences("login_data", Context.MODE_PRIVATE);
        String name = preferences.getString("name", "name");
        headerName.setText(name);
        String email = preferences.getString("email", "email");
        if (Objects.equals(email, "No e-mail")) {
            headerEmail.setText(getString(R.string.No_email));
        }
        else headerEmail.setText(email);

        numberChatsShow =(TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_chats));
        loadNewMessages();

    }

    public void initializeCountDrawer(String number){
        //Gravity property aligns the text
        numberChatsShow.setGravity(Gravity.CENTER_VERTICAL);
        numberChatsShow.setTypeface(null, Typeface.BOLD);
        numberChatsShow.setTextColor(getResources().getColor(R.color.colorAccent));
        numberChatsShow.setText(number);

    }

    public void setNumberChats(String numberChats) {
        numberChatsShow.setText(numberChats);
    }


    @SuppressLint("StaticFieldLeak")
    private void loadNewMessages() {
        final Activity activity = this;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                preferences = getSharedPreferences("login_data", Context.MODE_PRIVATE);
                String userId = preferences.getString("idUser", "null");
                server.token = preferences.getString("user_token", "user_token");
                System.out.println("user: "+userId);
                return server.getNewMessages(userId);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING NUMBER OF NEW MESSAGES")) {
                    System.out.println("server response new messages: " + s.toString());
                    try {
                        getChatsFromString(s);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(activity, "Error checking new messages", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void getChatsFromString(String s) throws JSONException {
        //final Activity activity = this;
        JSONObject chats = new JSONObject(s);
        String number = chats.getString("new");
        System.out.println("number new: "+number);
        newMessages = number;
        initializeCountDrawer(number);
        //setNumberChats(number);

    }


    /*@Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Toast.makeText(getApplicationContext(), "Function FAQ not implemented", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile || id == R.id.headerLayout) {
            getLikesDislikes();
        } else if (id == R.id.nav_adverts) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            this.finish();
        } else if (id == R.id.nav_inscriptions) {
            fragment = new InscriptionsFragment();
        } else if (id == R.id.nav_forum) {
            fragment = new ForumFragment();
        } else if (id == R.id.nav_chats) {
            initializeCountDrawer("0");
            fragment = new MainChatsFragment();
        } else if (id == R.id.nav_FAQ) {
            Toast.makeText(getApplicationContext(), "Function FAQ not implemented", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_aboutUs) {
            fragment = new About();
        } else if (id == R.id.nav_logOut) {
            SharedPreferences preferences = getSharedPreferences("login_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            Intent i = new Intent(MainActivity.this, LogIn.class);
            startActivity(i);
            finish();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.screen_area, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private void getLikesDislikes() {
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
                    saveLikesDislikes(s);
                }
                else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_GettingUserInfo), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void saveLikesDislikes(String s) {
        try {
            JSONObject myJsonObject = new JSONObject(s);
            String rate = myJsonObject.getString("rate");
            JSONObject myJsonRate = new JSONObject(rate);
            int likes = myJsonRate.getInt("likes");
            int dislikes = myJsonRate.getInt("dislikes");

            SharedPreferences preferences = getSharedPreferences("login_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("likes", likes);
            editor.putInt("dislikes", dislikes);
            editor.apply();

            Fragment fragment = new ProfileFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.screen_area, fragment);
            ft.addToBackStack(null);
            ft.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNewMessages();
        initializeCountDrawer(newMessages);
    }

}
