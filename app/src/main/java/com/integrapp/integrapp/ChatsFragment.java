package com.integrapp.integrapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


public class ChatsFragment extends android.support.v4.app.Fragment {

    private Server server;
    //private ArrayList<ForumItem> chats = new ArrayList<>();
    private ListView chats;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats_chats, container, false);
        this.server = Server.getInstance();
        chats = view.findViewById(R.id.list_chats);
        //setInfoChats();
        return view;
    }

}
