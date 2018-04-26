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
        /*tabLayout.addTab(tabLayout.newTab().setText("Documentation"));
        tabLayout.addTab(tabLayout.newTab().setText("Languages"));
        tabLayout.addTab(tabLayout.newTab().setText("Entretaintment"));
        tabLayout.addTab(tabLayout.newTab().setText("Others"));*/

        /*lang = new LangFragment();
        entre = new EntrFragment();
        other = new OtherFragment();

        pagerAdapter = new FragmentAdapter(getChildFragmentManager());

        viewPager.setAdapter(pagerAdapter);*/

    }

    /*@Override
    public void onTabSelected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }*/

    //ADD FRAGMENTS TO TABS
    private void setupViewPager (ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getChildFragmentManager());
        adapter.addFragment(docu, "DOCUMENTATION");
        adapter.addFragment(lang, "LANGUAGES");
        adapter.addFragment(entre, "ENTERTAINMENT");
        adapter.addFragment(other, "OTHER");
        viewPager.setAdapter(adapter);
    }

    private static class FragmentAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}