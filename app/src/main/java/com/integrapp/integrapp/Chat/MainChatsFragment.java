package com.integrapp.integrapp.Chat;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Adapters.FragmentAdapter;

import java.util.ArrayList;

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
        users = new UsersFragment();
        chats = new ChatsFragment();

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
        adapter.addFragment(chats, getString(R.string.titleTab_Chats));
        adapter.addFragment(users, getString(R.string.titleTab_Users));
        viewPager.setAdapter(adapter);
    }

    @Override
    //Clear the list to avoid duplicated content
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Toolbar toolbar= getActivity().findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(R.string.MainChatsFragment_title);
        }
    }
}
