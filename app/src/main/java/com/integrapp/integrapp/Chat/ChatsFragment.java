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
import com.integrapp.integrapp.Adapters.ChatsAdapter;
import com.integrapp.integrapp.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ChatsFragment extends android.support.v4.app.Fragment {

    private static ChatServer chatServer;
    private ArrayList<Chat> chats = new ArrayList<>();
    private ListView listViewChats;
    private ChatsAdapter chatsAdapter;
    private String personalId;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chats = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_chats_chats, container, false);
        chatServer = ChatServer.getInstance();
        listViewChats = view.findViewById(R.id.list_chats);
        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        personalId = preferences.getString("idUser", "null");
        setInfoChats();

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    public void setInfoChats() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return chatServer.getChats(personalId);
            }

            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING CHATS")) {
                    try {
                        Toast.makeText(getActivity(), "Go to users tab to open a new chat!", Toast.LENGTH_LONG).show();
                        getInfoFromString(s);
                        chatsAdapter = new ChatsAdapter(getContext(), chats);
                        listViewChats.setAdapter(chatsAdapter);

                        listViewChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                //obrir xat en clicar a un chat
                                Chat chat = chats.get(position);
                                User user = chat.getUser();
                                Fragment chatFrag = new SingleChatFragment(user);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fragmentManager.beginTransaction();
                                ft.replace(R.id.screen_area, chatFrag);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error setting info of chats", Toast.LENGTH_SHORT).show();
                }
            }

        }.execute();

    }

    private void getInfoFromString(String s) throws JSONException {
        JSONArray chatsList = new JSONArray(s);

        for (int i=0; i<chatsList.length(); ++i) {
            JSONObject chatJson = new JSONObject(chatsList.getString(i));
            String id = chatJson.getString("_id");
            String username = chatJson.getString("username");
            String name = chatJson.getString("name");
            String type = chatJson.getString("type");

            User user = new User(id, username, name, type);

            Chat chat = new Chat(user);
            chats.add(chat);
        }
    }

}
