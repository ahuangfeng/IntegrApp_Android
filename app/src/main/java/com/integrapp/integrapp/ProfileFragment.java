package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private TextView nameTextView;
    private TextView usernameTextView;
    private TextView typeUserTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private Server server;

    @SuppressLint("StaticFieldLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        this.server = Server.getInstance();
        nameTextView = view.findViewById(R.id.nameTextView);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        typeUserTextView = view.findViewById(R.id.typeUserTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);

        getUserInfoByUsername();

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void getUserInfoByUsername() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                String username = preferences.getString("username", "username");
                return server.getUserInfoByUsername(username);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GET INFO USER")) {
                    System.out.println("INFO USUARI RESPONSE: " +s);
                    getInfoUser(s);
                }
                else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void getIdUser(String s) {
        try {
            JSONObject myJsonjObject = new JSONObject(s);
            String id = myJsonjObject.getString("_id");
            System.out.print("myuserid"+id);
            deleteUserById(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void deleteUserById(final String id) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    return server.deleteUserById(id);
                }

                @Override
                protected void onPostExecute(String s) {
                    if (!s.equals("ERROR IN DELETING USER")) {
                        System.out.println("DELETE USER SUCCESSFULL RESPONSE: " +s);
                        Toast.makeText(getActivity().getApplicationContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ProfileFragment.this.getActivity(), LogIn.class);
                        startActivity(i);
                        getActivity().finish();
                    }
                    else {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }

    private void getInfoUser(String s) {
        try {
            JSONObject myJsonjObject = new JSONObject(s);
            String username = myJsonjObject.getString("username");
            String type = myJsonjObject.getString("type");
            String name = myJsonjObject.getString("name");
            String email = "No e-mail";
            String phone = "No phone";
            if(myJsonjObject.has("email")) {
                email = myJsonjObject.getString("email");
            }
            if(myJsonjObject.has("phone")) {
                phone = myJsonjObject.getString("phone");
            }

            setAttributes(username, type, name, email, phone);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAttributes(String username, String type, String name, String email, String phone) {
        nameTextView.setText(name);
        usernameTextView.setText(username);
        typeUserTextView.setText(type);
        emailTextView.setText(email);
        phoneTextView.setText(phone);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.profile, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {

            SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
            server.token = preferences.getString("user_token", "user_token");
            String username = preferences.getString("username", "username");

            getIdByUsername(username);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private void getIdByUsername(final String username) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getUserInfoByUsername(username);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GET INFO USER")) {
                    System.out.println("INFO USUARI RESPONSE: " +s);
                    getIdUser(s);
                }
                else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
