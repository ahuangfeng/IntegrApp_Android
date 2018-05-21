package com.integrapp.integrapp;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class UsersFragment extends android.support.v4.app.Fragment {

    private Server server;
    private ArrayList<User> users = new ArrayList<>();
    private ListView view_users;
    private UsersAdapter usersAdapter;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats_users, container, false);
        this.server = Server.getInstance();
        view_users = view.findViewById(R.id.list_users);
        setInfoUsers();
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    public void setInfoUsers() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return server.getUsers();
            }

            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING ALL USERS")) {
                    try {
                        getInfoFromString(s);
                        usersAdapter = new UsersAdapter(getContext(), users);
                        view_users.setAdapter(usersAdapter);
                        //usersAdapter.notifyDataSetChanged();

                        view_users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(getActivity(), "Elemento "+ position+ " clickado", Toast.LENGTH_SHORT).show();

                                //crear xat en clicar a un user o obrir-lo si ja existeix
                                User user = users.get(position);
                                Fragment chat = new SingleChatFragment(user);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fragmentManager.beginTransaction();
                                ft.replace(R.id.screen_area, chat);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Error setting info of users", Toast.LENGTH_SHORT).show();
                }
            }

        }.execute();
    }

    private void getInfoFromString(String s) throws JSONException {
        JSONArray llistaUsers = new JSONArray(s);

        for (int i=0; i<llistaUsers.length(); ++i) {
            JSONObject userjson = new JSONObject(llistaUsers.getString(i));
            String id = userjson.getString("_id");
            String username = userjson.getString("username");
            String name = userjson.getString("name");
            String type = userjson.getString("type");

            User user = new User(id, username, name, type);

            users.add(user);
        }
    }



}
