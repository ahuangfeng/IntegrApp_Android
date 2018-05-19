package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

public class InscriptionsAdapter extends BaseAdapter {

    Context contexto;
    List<DataInscription> objectList;
    private Server server;
    FragmentActivity activity;
    private String idAdvert;

    public InscriptionsAdapter(Context contexto, List<DataInscription> objectList, FragmentActivity activity, String idAdvert) {
        this.contexto = contexto;
        this.objectList = objectList;
        this.activity = activity;
        this.idAdvert = idAdvert;
    }

    @Override
    public int getCount() {
        return objectList.size();
    }

    @Override
    public Object getItem(int i) {
        return objectList.get(i);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(objectList.get(position).getId());
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        server = Server.getInstance();
        final String idUser = objectList.get(i).getIdUser();
        View vista;
        LayoutInflater inflate = LayoutInflater.from(contexto);
        vista = inflate.inflate(R.layout.activity_item_inscription, null);

        final TextView usernameTextView = vista.findViewById(R.id.textViewUsername);
        final String username = objectList.get(i).getUsernameOwner();
        usernameTextView.setText(objectList.get(i).getUsernameOwner());
        usernameTextView.setClickable(true);
        usernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfoUser(username, idUser);
            }
        });

        TextView stateTextView = vista.findViewById(R.id.textViewState);
        final String status = objectList.get(i).getState();
        stateTextView.setText(status);
        stateTextView.setClickable(true);
        stateTextView.setFocusable(true);
        stateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("pending")) {
                    Fragment fragment = new SingleInscriptionFragment(idAdvert, idUser);
                    android.support.v4.app.FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.screen_area, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

        return vista;
    }

    @SuppressLint("StaticFieldLeak")
    private void getInfoUser(final String username, final String idUser) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = contexto.getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.getUserInfoByUsername(username);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GET INFO USER")) {
                    System.out.println("INFO USUARI RESPONSE: " +s);
                    sendInfoUserToProfile(s, idUser);
                }
            }
        }.execute();
    }

    private void sendInfoUserToProfile(String s, String idUser) {
        //para saber que viene de aqui y no del perfil del navigation
        Fragment fragment = new ProfileFragment("advertiserUser");
        android.support.v4.app.FragmentManager fragmentManager = activity.getSupportFragmentManager();

        //paso de parametros al ProfileFragment
        Bundle args = new Bundle();

        try {
            JSONObject myJsonObject = new JSONObject(s);
            args.putString("idUser", idUser);
            args.putString("username", myJsonObject.getString("username"));
            args.putString("name", myJsonObject.getString("name"));
            args.putString("type", myJsonObject.getString("type"));
            if (myJsonObject.has("email")) {
                args.putString("email", myJsonObject.getString("email"));
            }
            if (myJsonObject.has("phone")) {
                args.putString("phone", myJsonObject.getString("phone"));
            }
            int likes = myJsonObject.getJSONObject("rate").getInt("likes");
            int dislikes = myJsonObject.getJSONObject("rate").getInt("dislikes");
            args.putInt("likes", likes);
            args.putInt("dislikes", dislikes);
            int ads = myJsonObject.getJSONArray("adverts").length();
            args.putInt("ads", ads);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fragment.setArguments(args);

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.screen_area, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}

