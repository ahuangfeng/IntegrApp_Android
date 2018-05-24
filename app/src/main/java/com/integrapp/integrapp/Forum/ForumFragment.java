package com.integrapp.integrapp.Forum;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.integrapp.integrapp.R;
import com.integrapp.integrapp.apapters.FragmentAdapter;

public class ForumFragment extends Fragment {

    private static Fragment docu;
    private static Fragment lang;
    private static Fragment entre;
    private static Fragment other;
    private FloatingActionButton fab;

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
        adapter.addFragment(other, "VARIOUS");
        viewPager.setAdapter(adapter);
    }
}