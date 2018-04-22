package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdvertsFragment extends Fragment {

    private Server server;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_advert, container, false);

        this.server = Server.getInstance();

        getAllAdverts(); //Show adverts

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("StaticFieldLeak")
    private void getAllAdverts() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                System.out.println("TOKEN :" + server.token); //correcto
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                String token = preferences.getString("user_token", "user_token");
                server.token = token;
                System.out.println("TOKEN PREFERENCES: " + token);
                return server.getAllAdverts();
            }

            //funció a modificar --funcion asíncrona
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
                    //putAttributesInTextView(attributes);

                    //Preparación del diseño
                    LinearLayout contentAdvert = getView().findViewById(R.id.includeContentAdvert);
                    ListView list;
                    list = contentAdvert.findViewById(R.id.sampleListView);
                    final ArrayList<DataAdvert> adverts = new ArrayList<>();
                    DataAdvert dataAdvert;
                    /*Imagen fija*/
                    int image = R.drawable.project_preview_large_2;

                    for (int i=0; i< attributes.size(); ++i) {
                        dataAdvert = new DataAdvert(attributes.get(i).get(0), attributes.get(i).get(1),
                                attributes.get(i).get(2), attributes.get(i).get(3), attributes.get(i).get(4),
                                attributes.get(i).get(5), attributes.get(i).get(6), image);
                        adverts.add(dataAdvert);

                    }

                    AdvertsAdapter myadapter = new AdvertsAdapter(getView().getContext(), adverts);
                    list.setAdapter(myadapter);
                    myadapter.notifyDataSetChanged();


                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            DataAdvert dataAdvert = adverts.get(position);
                            Fragment fragment = new SingleAdvertFragment(dataAdvert);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
                            ft.replace(R.id.screen_area, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    });

                }
                else {
                    //TODO: Back quitará el time-out del token, por lo que esto se eliminaria en un futuro
                    Toast.makeText(getActivity(), "Your token has expired, please login", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(), LogIn.class);
                    startActivity(i);
                    getActivity().finish();
                }
            }
        }.execute();
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
        attributesAdd.add(myJsonjObject.getString("state"));
        attributesAdd.add(myJsonjObject.getString("createdAt"));

        return attributesAdd;
    }

}