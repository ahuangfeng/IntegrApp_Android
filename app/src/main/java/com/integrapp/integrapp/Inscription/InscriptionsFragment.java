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
import com.integrapp.integrapp.Adapters.InscriptionsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InscriptionsFragment extends android.support.v4.app.Fragment {
    private Server server;
    private InscriptionServer inscriptionServer;
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
        this.inscriptionServer = InscriptionServer.getInstance();
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
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");

                if (idAdvert.equals("inscriptions")) {
                    return inscriptionServer.getAllUserInscriptions(userId);
                } else {
                    return inscriptionServer.getAllInscriptions(idAdvert);
                }
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING ALL INSCRIPTIONS")) {
                    try {
                        JSONArray allInscriptions = new JSONArray(s);
                        ArrayList<DataInscription> attributes = getAttributesAllInscriptions(allInscriptions);

                        //Preparación del diseño
                        ListView list;
                        LinearLayout contentInscription = view.findViewById(R.id.includeContentInscription);
                        list = contentInscription.findViewById(R.id.sampleListView);

                        InscriptionsAdapter myAdapter = new InscriptionsAdapter(view.getContext(), attributes, getActivity(), idAdvert);
                        list.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged();

                        list.setClickable(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Toast.makeText(getActivity(), "Error loading inscriptions", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private ArrayList<DataInscription> getAttributesAllInscriptions(JSONArray stringJson) {
        ArrayList<DataInscription> attributes = new ArrayList<>();
        try {
            for (int i = 0; i < stringJson.length(); ++i) {
                DataInscription attributesAdd = getAttributesInscription(stringJson, i);
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
        JSONObject myJsonObject = new JSONObject(advertString);

        String id, info, state, userId, advertId;
        DataInscription dataInscription;

        id = myJsonObject.getString("_id");
        if (idAdvert.equals("inscriptions")) {
            info = myJsonObject.getString("titleAdvert");
        } else {
            info = myJsonObject.getString("username");
        }
        state = myJsonObject.getString("status");
        userId = myJsonObject.getString("userId");
        advertId = myJsonObject.getString("advertId");


        dataInscription = new DataInscription(id, info, state, userId, advertId);
        return dataInscription;
    }

}
