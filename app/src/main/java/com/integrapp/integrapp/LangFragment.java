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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LangFragment extends android.support.v4.app.Fragment {

    private Server server;
    private ArrayList<ForumItem> threads = new ArrayList<>();
    private ArrayList<DataComment> comments = new ArrayList<>();
    private ListView llista;
    private ForumsAdapter forumsAdapter;
    private ForumItem forumItem;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum_lang, container, false);
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
                return server.getForumLang();
            }

            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING FORUM(LANGUAGES)")) {
                    try {
                        getInfoFromString(s);
                        forumsAdapter = new ForumsAdapter(getContext(), threads);
                        llista.setAdapter(forumsAdapter);
                        forumsAdapter.notifyDataSetChanged();

                        llista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(getActivity(), "Elemento "+ position+ " clickado", Toast.LENGTH_SHORT).show();

                                forumItem = threads.get(position);
                                getCommentsForum(forumItem.getId());
                            }
                        });

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

    @SuppressLint("StaticFieldLeak")
    private void getCommentsForum(final String id) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                System.out.println("TOKEN :" + server.token); //correcto (es nulo cuando entras la primera vez)
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                String token = preferences.getString("user_token", "user_token");
                server.token = token; //por eso lo volemos a guardar en el server
                System.out.println("TOKEN PREFERENCES: " + token);
                return server.getCommentsForum(id);
            }

            @Override
            protected void onPostExecute(String s) {
                System.out.println("FORUM WITH COMMENTS : " +s);
                if (!s.equals("ERROR IN GETTING COMMENTS FORUM")) {
                    getInfoComments(s);
                    Fragment fragment = new SingleForumFragment(forumItem, comments);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.screen_area, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                else {
                    Toast.makeText(getActivity(), "Error getting full forum", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void getInfoComments(String s) {
        try {
            JSONObject fullForum = new JSONObject(s);
            JSONArray comments = fullForum.getJSONArray("entries");
            this.comments.clear(); //para que no se acumulen los comentarios de diferentes foros

            for (int i = 0; i < comments.length(); ++i) {
                JSONObject comment = new JSONObject(comments.getString(i));
                String id = comment.getString("_id");
                String userId = comment.getString("userId");
                String username = comment.getString("username");
                String createdAt = comment.getString("createdAt");
                String content = comment.getString("content");
                String forumId = comment.getString("forumId");

                DataComment dataComment = new DataComment(id, userId, username, createdAt, content, forumId);
                this.comments.add(dataComment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getInfoFromString(String s) throws JSONException {
        JSONArray llistaForums = new JSONArray(s);

        for (int i=0; i<llistaForums.length(); ++i) {
            JSONObject forum = new JSONObject(llistaForums.getString(i));
            String id = forum.getString("_id");
            String type = forum.getString("type");
            String title = forum.getString("title");
            String description = forum.getString("description");
            String createdAt = forum.getString("createdAt");
            String userId = forum.getString("userId");
            float rate = (float) forum.getDouble("rate");
            String user = forum.getString("user");

            ForumItem item = new ForumItem(id, type, title, description, createdAt, userId, rate, user);
            threads.add(item);
        }
    }
}
