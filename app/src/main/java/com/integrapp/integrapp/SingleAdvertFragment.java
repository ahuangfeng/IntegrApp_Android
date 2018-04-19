package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SingleAdvertFragment extends Fragment {

    private String tittle;
    private String type;
    private String state;
    private String places;
    private String date;
    private String description;
    private int image;

    public SingleAdvertFragment() {
    }

    @SuppressLint({"ValidFragment", "SetTextI18n"})
    public SingleAdvertFragment(DataAdvert dataAdvert) {
        tittle = dataAdvert.getTitle();
        type = dataAdvert.getType();
        state = dataAdvert.getState();
        places = dataAdvert.getPlaces();
        date = dataAdvert.getDate();
        description = dataAdvert.getDescription();
        image = dataAdvert.getImage();

        System.out.println("Parametros: " +tittle + " " + type + " " + state + " " +places+ " "+ date+ " "+ description);
    }

    @SuppressLint("StaticFieldLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.single_advert_fragment, container, false);

        TextView tv = view.findViewById(R.id.tv1);
        tv.setText(tittle + " " + type + " " + state + " " +places+ " "+ date+ " "+ description );

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
