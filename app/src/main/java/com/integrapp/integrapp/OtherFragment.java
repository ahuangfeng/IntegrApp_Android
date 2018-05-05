package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Laura on 26/04/2018.
 */

public class OtherFragment extends android.support.v4.app.Fragment {

    private Server server;
    private ArrayList<ForumItem> threads = new ArrayList<>();
    private ListView llista;
    private ForumsAdapter forumsAdapter;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum_other, container, false);
        this.server = Server.getInstance();
        llista = view.findViewById(R.id.llista);
        setInfoForum();

        return view;
    }

    @Override
    //Clear the list to avoid duplicated content
    public void onViewCreated(View view, Bundle savedInstanceState) {
        threads = new ArrayList<>();
    }

    @SuppressLint("StaticFieldLeak")
    public void setInfoForum() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return server.getForumOther();
            }

            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING FORUM(OTHER)")) {
                    try {
                        getInfoFromString(s);
                        forumsAdapter = new ForumsAdapter(getContext(), threads);
                        llista.setAdapter(forumsAdapter);
                        forumsAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Error setting info", Toast.LENGTH_SHORT).show();
                }
            }

        }.execute();
    }

    private void getInfoFromString(String s) throws JSONException {
        JSONArray llistaForums = new JSONArray(s);

        for (int i=0; i<llistaForums.length(); ++i) {
            JSONObject forum = new JSONObject(llistaForums.getString(i));
            //Long id = forum.getLong("_id");
            String type = forum.getString("type");
            String titol = forum.getString("title");
            String description = forum.getString("description");
            String createdAt = forum.getString("createdAt");
            //Long userId = forum.getLong("userId");
            float rate = (float) forum.getDouble("rate");

            Long id = 0L;
            Long userId = 1L;

            ForumItem item = new ForumItem(id, type, titol, description, createdAt, userId, rate);
            threads.add(item);
        }
    }
}
