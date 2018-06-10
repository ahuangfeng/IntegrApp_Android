package com.integrapp.integrapp.Chat;

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

import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Adapters.UsersAdapter;
import com.integrapp.integrapp.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UsersFragment extends android.support.v4.app.Fragment {


    private ChatServer chatServer;
    private ArrayList<User> users = new ArrayList<>();
    private ListView view_users;
    private UsersAdapter usersAdapter;
    private String personalUserId;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        users = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_chats_users, container, false);
        this.chatServer = ChatServer.getInstance();
        view_users = view.findViewById(R.id.list_users);
        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        personalUserId = preferences.getString("idUser", "null");
        setInfoUsers();
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    public void setInfoUsers() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return chatServer.getUsers();
            }

            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING ALL USERS")) {
                    try {
                        getInfoFromString(s);
                        usersAdapter = new UsersAdapter(getContext(), users);
                        view_users.setAdapter(usersAdapter);

                        view_users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                //crear xat en clicar a un user o obrir-lo si ja existeix
                                User user = users.get(position);
                                Fragment chat = new SingleChatFragment(user);
                                FragmentManager fragmentManager = UsersFragment.this.getActivity().getSupportFragmentManager();
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
                    Toast.makeText(UsersFragment.this.getActivity(), getString(R.string.error_SettingInfoOfUsers), Toast.LENGTH_SHORT).show();
                }
            }

        }.execute();
    }

    private void getInfoFromString(String s) throws JSONException {
        JSONArray usersList = new JSONArray(s);

        for (int i=0; i<usersList.length(); ++i) {
            JSONObject userJson = new JSONObject(usersList.getString(i));
            String id = userJson.getString("_id");
            String username = userJson.getString("username");
            String name = userJson.getString("name");
            String type = userJson.getString("type");
            String path;
            if (userJson.has("imagePath")) {
                String path2 = userJson.getString("imagePath");
                if (path2.equals("null")) {
                    path = "";
                } else {
                    path = path2;
                }
            } else {
                path = "";
            }
            User user = new User(id, username, name, type, path);
            if (!user.getId().equals(personalUserId)) {
                users.add(user);
            }
        }
    }
}