package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class SingleAdvertFragment extends Fragment {

    private String tittle;
    private String type;
    private String state;
    private String places;
    private String date;
    private String description;
    private String userId;
    private int image;

    UserDataAdvertiser userData;
    private Server server;

    public SingleAdvertFragment() {
    }

    @SuppressLint({"ValidFragment", "SetTextI18n"})
    public SingleAdvertFragment(DataAdvert dataAdvert, UserDataAdvertiser userData) {
        tittle = dataAdvert.getTitle();
        type = dataAdvert.getType();
        state = dataAdvert.getState();
        places = dataAdvert.getPlaces();
        date = dataAdvert.getDate();
        description = dataAdvert.getDescription();
        userId = dataAdvert.getUserId();
        image = dataAdvert.getImage();

        this.userData = userData;

        System.out.println("Parametros: " +tittle + " " + type + " " + state + " " +places+ " "+ date+ " "+ description + " "+ userId);
    }

    @SuppressLint("StaticFieldLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        server = Server.getInstance();
        View view = inflater.inflate(R.layout.single_advert_fragment, container, false);

        TextView textViewUsername = view.findViewById(R.id.textView_username);
        TextView textViewTitle = view.findViewById(R.id.textView_title);
        TextView textViewDescription = view.findViewById(R.id.textView_description);
        TextView textViewPlaces = view.findViewById(R.id.textView_places);
        TextView textViewDate = view.findViewById(R.id.textView_date);

        textViewUsername.setText(userData.getUsername());
        textViewTitle.setText(tittle);
        textViewDescription.setText(description);
        textViewPlaces.setText("Places: "+ places);
        textViewDate.setText("Expected date: "+date);

        ImageView imageView = view.findViewById(R.id.image_view_anunci);
        imageView.setImageResource(image);

        /*TODO: Implementar boton "I want it!"*/

        Button profileButton = view.findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfoUser(userData.getUsername());
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void getInfoUser(final String username) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.getUserInfoByUsername(username);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GET INFO USER")) {
                    System.out.println("INFO USUARI RESPONSE: " +s);
                    sendInfoUserToProfile(s);
                }
            }
        }.execute();
    }

    private void sendInfoUserToProfile(String s) {
        //para saber que viene de aqui y no del perfil del navigation
        Fragment fragment = new ProfileFragment("advertiserUser");
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        //paso de parametros al ProfileFragment
        Bundle args = new Bundle();
        args.putString("idUser", userData.getIdUser());
        args.putString("username", userData.getUsername());
        args.putString("name", userData.getName());
        args.putString("type", userData.getType());
        args.putString("email", userData.getEmail());
        args.putString("phone", userData.getPhone());

        try {
            JSONObject myJsonObject = new JSONObject(s);
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
