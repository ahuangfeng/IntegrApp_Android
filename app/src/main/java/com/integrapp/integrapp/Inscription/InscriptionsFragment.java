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
    private View view;

    public InscriptionsFragment() {
        idAdvert = "inscriptions";
        this.context = getContext();
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
        view = inflater.inflate(R.layout.activity_inscription, container, false);
        this.server = Server.getInstance();

        if (idAdvert.equals("inscriptions")) {
            SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
            userId = preferences.getString("idUser", "null");
        }

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
                if (idAdvert.equals("inscriptions")) {
                    return server.getAllUserInscriptions(userId);
                } else {
                    return server.getAllInscriptions(idAdvert);
                }
            }

            @Override
            protected void onPostExecute(String s) {
                System.out.println("INSCRIPTIONS : " +s);
                if (!s.equals("ERROR IN GETTING ALL INSCRIPTIONS")) {
                    try {
                        JSONArray allInscriptions = new JSONArray(s);
                        ArrayList<DataInscription> attributes = getAttributesAllInscriptions(allInscriptions);

                        //Preparación del diseño
                        ListView list;
                        LinearLayout contentInscription = view.findViewById(R.id.includeContentInscription);
                        list = contentInscription.findViewById(R.id.sampleListView);

                        InscriptionsAdapter myadapter = new InscriptionsAdapter(view.getContext(), attributes, getActivity(), idAdvert);
                        list.setAdapter(myadapter);
                        myadapter.notifyDataSetChanged();

                        list.setClickable(false);
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

    private ArrayList<DataInscription> getAttributesAllInscriptions(JSONArray stringJson) {
        ArrayList<DataInscription> attributes = new ArrayList<>();
        try {
            for (int i = 0; i < stringJson.length(); ++i) {
                System.out.println("json: " + i + " "+ stringJson.getString(i));
                DataInscription attributesAdd = getAttributesInscription(stringJson, i); //atributos de un anuncio
                attributes.add(attributesAdd);
            }
            return attributes;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DataInscription getAttributesInscription(JSONArray myJsonjArray, int index) throws JSONException {
        String advertString = myJsonjArray.getString(index);
        JSONObject myJsonjObject = new JSONObject(advertString);

        String id, info, state, userId, advertId;
        DataInscription dataInscription;

        id = myJsonjObject.getString("_id");
        if (idAdvert.equals("inscriptions")) {
            info = myJsonjObject.getString("titleAdvert");
        } else {
            info = myJsonjObject.getString("username");
        }
        state = myJsonjObject.getString("status");
        userId = myJsonjObject.getString("userId");
        advertId = myJsonjObject.getString("advertId");


        dataInscription = new DataInscription(id, info, state, userId, advertId);
        return dataInscription;
    }

}
