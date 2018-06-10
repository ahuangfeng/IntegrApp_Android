package com.integrapp.integrapp.Chat;

import android.os.Build;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Adapters.FragmentAdapter;

import java.util.ArrayList;

public class MainChatsFragment extends Fragment{
    private static Fragment users;
    private static Fragment chats;

    private ViewPager viewPager;
    private TabLayout tabs;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int position;

    public MainChatsFragment() {
        position = -1;
    }

    @SuppressLint("ValidFragment")
    public MainChatsFragment(int position) {
        this.position = position;
    }

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

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        // Setting ViewPager for each Tabs
        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        tabs = view.findViewById(R.id.tabs_chat);
        tabs.setupWithViewPager(viewPager);

        if (position != -1) {
            selectPage(position);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Fragment fragment = new MainChatsFragment(tabs.getSelectedTabPosition());
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.screen_area, fragment);
                ft.addToBackStack(null);
                ft.commit();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

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

    public void selectPage(int pageIndex){
        tabs.setScrollPosition(pageIndex,0f,true);
        viewPager.setCurrentItem(pageIndex);
    }
}
