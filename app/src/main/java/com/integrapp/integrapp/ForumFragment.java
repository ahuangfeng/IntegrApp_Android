package com.integrapp.integrapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ForumFragment extends Fragment {

    private static Fragment docu;
    private static Fragment lang;
    private static Fragment entre;
    private static Fragment other;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tabs_fragment_forum, container, false);

        //Create fragments inside tabs
        docu = new DocuFragment();
        lang = new LangFragment();
        entre = new EntreFragment();
        other = new OtherFragment();

        // Setting ViewPager for each Tabs
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    //ADD FRAGMENTS TO TABS
    private void setupViewPager (ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getChildFragmentManager());
        adapter.addFragment(docu, "DOCUMENTATION");
        adapter.addFragment(lang, "LANGUAGES");
        adapter.addFragment(entre, "ENTERTAINMENT");
        adapter.addFragment(other, "OTHER");
        viewPager.setAdapter(adapter);
        System.out.print("NOMBRE DE FRAGMENTS: ");
        System.out.println(adapter.getCount());
    }
}