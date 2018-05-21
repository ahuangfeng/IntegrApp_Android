package com.integrapp.integrapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.integrapp.integrapp.FragmentAdapter;
import com.integrapp.integrapp.R;

public class MainChatsFragment extends Fragment{
    private static Fragment users;
    private static Fragment chats;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.main_chats, container, false);

        //Create fragments inside tabs
        users = new com.integrapp.integrapp.UsersFragment();
        chats = new com.integrapp.integrapp.ChatsFragment();

        // Setting ViewPager for each Tabs
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = view.findViewById(R.id.tabs_chat);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    //ADD FRAGMENTS TO TABS
    private void setupViewPager (ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getChildFragmentManager());
        adapter.addFragment(chats, "CHATS");
        adapter.addFragment(users, "USERS");
        viewPager.setAdapter(adapter);
    }

}
