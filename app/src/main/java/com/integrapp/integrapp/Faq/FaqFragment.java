package com.integrapp.integrapp.Faq;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import com.integrapp.integrapp.Adapters.FaqAdapter;
import com.integrapp.integrapp.Model.Faq;
import com.integrapp.integrapp.R;
import java.util.ArrayList;

public class FaqFragment extends android.support.v4.app.Fragment {

    private ArrayList<Faq> faqDocu = new ArrayList<>();
    private FaqAdapter adapter;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
        ListView list = view.findViewById(R.id.llista);
        initializeItems();
        initializeAdapters();
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Faq clickedFaq = adapter.getItem(position);
                SingleFaqFragment fragment = new SingleFaqFragment();
                fragment.setFaq(clickedFaq);
                FragmentManager fragmentManager = FaqFragment.this.getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.screen_area, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return view;
    }

    private void initializeItems() {
        for (int i=0; i<3; ++i) {
            String preparedTitle = getTitle(i);
            String preparedContent = getContent(i);
            Faq newItem = new Faq();
            newItem.setTitle(preparedTitle);
            newItem.setContent(preparedContent);
            if (i==0) {
                Drawable image = getResources().getDrawable(R.drawable.faqcasa);
                newItem.setImage(image);
            }
            else if (i==1) {
                Drawable image = getResources().getDrawable(R.drawable.faqempresa);
                newItem.setImage(image);
            }
            else if (i==2) {
                Drawable image = getResources().getDrawable(R.drawable.faqidiomes);
                newItem.setImage(image);
            }
            else {

            }
            faqDocu.add(newItem);
        }
    }

    private String getTitle(int i) {
        String faqTitles[] = getResources().getStringArray(R.array.faq_title_number);
        return faqTitles[i];
    }

    private String getContent(int i) {
        String faqContents[] = getResources().getStringArray(R.array.faq_content_number);
        return faqContents[i];
    }

    private void initializeAdapters() {
        adapter = new FaqAdapter(getContext(), faqDocu);
    }

    @Override
    //Clear the list to avoid duplicated content
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Toolbar toolbar= getActivity().findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(R.string.FAQ);
        }
        faqDocu = new ArrayList<>();
    }


}
