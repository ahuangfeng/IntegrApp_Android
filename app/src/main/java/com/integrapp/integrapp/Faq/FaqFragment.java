package com.integrapp.integrapp.Faq;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.integrapp.integrapp.R;
import java.util.ArrayList;

public class FaqFragment extends android.support.v4.app.Fragment {

    private ArrayList<SingleFaqItem> faqDocu = new ArrayList<>();
    private FaqAdapter adapter;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
        ListView list = view.findViewById(R.id.llista);
        initializeItems();
        initializeAdapters();
        list.setAdapter(adapter);
        return view;
    }

    private void initializeItems() {
        for (int i=0; i<3; ++i) {
            String preparedTitle = getTitle(i);
            String preparedContent = getContent(i);
            SingleFaqItem newItem = new SingleFaqItem(preparedTitle, preparedContent);
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
        faqDocu = new ArrayList<>();
    }


}
