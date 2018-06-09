package com.integrapp.integrapp.Forum;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Adapters.FragmentAdapter;

public class ForumFragment extends Fragment {

    private static Fragment doc;
    private static Fragment lang;
    private static Fragment enter;
    private static Fragment other;
    private FloatingActionButton fab;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ViewPager viewPager;
    private TabLayout tabs;

    private int position;

    @SuppressLint("ValidFragment")
    public ForumFragment(int position) {
        this.position = position;
    }

    public ForumFragment() {
        position = -1;
    }

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tabs_fragment_forum, container, false);

        //Create fragments inside tabs
        doc = new DocumentationFragment();
        lang = new LanguageFragment();
        enter = new EntertainmentFragment();
        other = new OtherFragment();

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.screen_area, new NewForumFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        // Setting ViewPager for each Tabs
        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        if (position != -1) {
            selectPage(position);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Fragment fragment = new ForumFragment(tabs.getSelectedTabPosition());
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
        adapter.addFragment(doc, getString(R.string.titleTab_Documentation));
        adapter.addFragment(lang, getString(R.string.titleTab_Languages));
        adapter.addFragment(enter, getString(R.string.titleTab_Entertainment));
        adapter.addFragment(other, getString(R.string.titleTab_Various));
        viewPager.setAdapter(adapter);
    }

    void selectPage(int pageIndex){
        tabs.setScrollPosition(pageIndex,0f,true);
        viewPager.setCurrentItem(pageIndex);
    }
}