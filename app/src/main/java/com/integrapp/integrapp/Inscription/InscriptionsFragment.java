package com.integrapp.integrapp.Inscription;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;
import com.integrapp.integrapp.apapters.InscriptionsAdapter;
import com.integrapp.integrapp.model.DataAdvert;
import com.integrapp.integrapp.model.UserDataAdvertiser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InscriptionsFragment extends android.support.v4.app.Fragment {
    private Server server;
    private String idAdvert;
    private String userId;
    private Context context;

    public InscriptionsFragment() {
    }

    @SuppressLint("ValidFragment")
    public InscriptionsFragment(String idAdvert, String userId, Context context) {
        this.idAdvert = idAdvert;
        this.userId = userId;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.activity_inscription, container, false);

        this.server = Server.getInstance();

        getAllInscriptions(idAdvert, userId); //Show adverts

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("StaticFieldLeak")
    private void getAllInscriptions(final String idAdvert, final String userId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                System.out.println("TOKEN :" + server.token); //correcto (es nulo cuando entras la primera vez)
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                String token = preferences.getString("user_token", "user_token");
                server.token = token; //por eso lo volemos a guardar en el server
                System.out.println("TOKEN PREFERENCES: " + token);
                return server.getAllInscriptions(idAdvert);
            }

            @Override
            protected void onPostExecute(String s) {
                System.out.println("INSCRIPTIONS : " +s);
                if (!s.equals("ERROR IN GETTING ALL INSCRIPTIONS")) {
                    try {
                        JSONObject myJsonObject = new JSONObject(s);
                        JSONObject advert = myJsonObject.getJSONObject("advert");
                        DataAdvert dataAdvert = new DataAdvert(advert, R.drawable.project_preview_large_2);
                        JSONObject allInscriptions = myJsonObject.getJSONObject("inscriptions");

                        ArrayList<ArrayList<String>> attributes = getAttributesAllInscriptions(allInscriptions);

                        //Preparación del diseño
                        LinearLayout contentAdvert = getView().findViewById(R.id.includeContentAdvert);
                        ListView list;
                        list = contentAdvert.findViewById(R.id.sampleListView);

                        final ArrayList<DataInscription> inscriptions = new ArrayList<>();
                        DataInscription dataInscription;

                        final ArrayList<UserDataAdvertiser> usersData = new ArrayList<>();
                        UserDataAdvertiser userDataAdvertiser;

                        String username, title;

                        for (int i=0; i< attributes.size(); ++i) {
                            //Los datos del usuario que ha publicado el anuncio
                            userDataAdvertiser = new UserDataAdvertiser(attributes.get(i).get(1));
                            usersData.add(userDataAdvertiser);

                            username = userDataAdvertiser.getUsername();
                            title = dataAdvert.getTitle();
                            dataInscription = new DataInscription(attributes.get(i).get(3), title, username, attributes.get(i).get(0));
                            inscriptions.add(dataInscription);
                        }

                        InscriptionsAdapter myadapter = new InscriptionsAdapter(getView().getContext(), inscriptions);
                        list.setAdapter(myadapter);
                        myadapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Toast.makeText(getActivity(), "Error loading ads", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private ArrayList<ArrayList<String>> getAttributesAllInscriptions(JSONObject stringJson) {
        ArrayList<ArrayList<String>> attributes = new ArrayList<>();
        try {
            JSONArray myJsonjArray = new JSONArray(stringJson);
            for (int i = 0; i < myJsonjArray.length(); ++i) {
                System.out.println("json: " + i + " "+ myJsonjArray.getString(i));
                ArrayList<String> attributesAdd = getAttributesInscription(myJsonjArray, i); //atributos de un anuncio
                attributes.add(attributesAdd);
            }
            return attributes;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<String> getAttributesInscription(JSONArray myJsonjArray, int index) throws JSONException {
        ArrayList<String> attributesAdd = new ArrayList<>();
        String advertString = myJsonjArray.getString(index);
        JSONObject myJsonjObject = new JSONObject(advertString);

        /*Aqui puedes añadir otro atributo de la respuesta Json del servidor
        si es necesario para mostrarlo en el diseño. De momento solo estan estos
        5 atributos, (date, tittle, description, places, typeAdvert*/
        attributesAdd.add(myJsonjObject.getString("state"));
        attributesAdd.add(myJsonjObject.getString("user")); //el Json en string de los datos del usuario de la inscripcion
        attributesAdd.add(myJsonjObject.getString("advert")); //el Json en string de los datos del anuncio de la inscripcion
        attributesAdd.add(myJsonjObject.getString("_id"));

        return attributesAdd;
    }

}
