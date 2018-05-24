package com.integrapp.integrapp.Forum;

import android.annotation.SuppressLint;
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

import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;
import com.integrapp.integrapp.apapters.ForumsAdapter;
import com.integrapp.integrapp.model.ForumItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LangFragment extends android.support.v4.app.Fragment {

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

                                ForumItem forumItem = threads.get(position);
                                Fragment fragment = new SingleForumFragment(forumItem);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fragmentManager.beginTransaction();
                                ft.replace(R.id.screen_area, fragment);
                                ft.addToBackStack(null);
                                ft.commit();
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

            String miniDescription = "";

            if (description.length()> 48) {
                miniDescription = description.substring(0,48) + "...";
            }
            ForumItem item = new ForumItem(id, type, title, description, miniDescription, createdAt, userId, rate, user);

            threads.add(item);
        }
    }
}
