package com.integrapp.integrapp.Inscription;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.integrapp.integrapp.Adapters.InscriptionsAdapter;
import com.integrapp.integrapp.Model.DataInscription;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;

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
    private String inscriptions;
    private View view;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public InscriptionsFragment() {
        idAdvert = "inscriptions";
        this.context = getContext();
    }

    @SuppressLint("ValidFragment")
    public InscriptionsFragment(String idAdvert, String userId, Context context, String inscriptions) {
        this.idAdvert = idAdvert;
        this.userId = userId;
        this.inscriptions = inscriptions;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.activity_inscription, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        this.server = Server.getInstance();
        this.inscriptionServer = InscriptionServer.getInstance();
        if (idAdvert.equals("inscriptions")) {
            SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
            userId = preferences.getString("idUser", "null");
            getUserInscriptions();
        } else {
            try {
                JSONArray allInscriptions = new JSONArray(inscriptions);
                ArrayList<DataInscription> attributes = getAttributesAllInscriptions(allInscriptions);
                showInscriptions(attributes, "advert");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("StaticFieldLeak")
    private void getUserInscriptions() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.getAllUserInscriptions(userId);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING ALL INSCRIPTIONS")) {
                    try {
                        JSONArray allInscriptions = new JSONArray(s);
                        ArrayList<DataInscription> attributes = getAttributesAllInscriptions(allInscriptions);
                        showInscriptions(attributes, "user");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_GettingInscriptions), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void showInscriptions (ArrayList<DataInscription> attributes, String type) {
        if (attributes == null || attributes.isEmpty()) {
            if (type.equals("user")) {
                Toast.makeText(getActivity(), getString(R.string.noUserInscriptions), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.noAdvertInscriptions), Toast.LENGTH_SHORT).show();
            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStackImmediate();
        } else {
            // Preparación del diseño
            ListView list;
            LinearLayout contentInscription = view.findViewById(R.id.includeContentInscription);
            list = contentInscription.findViewById(R.id.sampleListView);

            InscriptionsAdapter myAdapter = new InscriptionsAdapter(view.getContext(), attributes, getActivity(), idAdvert);
            list.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();

            list.setClickable(false);
            if (type.equals("user")) {
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getUserInscriptions();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }
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

    private DataInscription getAttributesInscription(JSONArray myJSONArray, int index) throws JSONException {
        String advertString = myJSONArray.getString(index);
        JSONObject myJsonObject = new JSONObject(advertString);

        String id, info, status, idUser;
        DataInscription dataInscription;

        status = myJsonObject.getString("status");
        idUser = myJsonObject.getString("userId");

        if (idAdvert.equals("inscriptions")) {
            id = myJsonObject.getString("_id");
            info = myJsonObject.getString("titleAdvert");
            String advertId = myJsonObject.getString("advertId");
            dataInscription = new DataInscription(id, info, status, idUser, advertId);
        } else {
            id = myJsonObject.getString("id");
            info = myJsonObject.getString("username");
            dataInscription = new DataInscription(id, info, status, idUser);
        }

        return dataInscription;
    }

}
