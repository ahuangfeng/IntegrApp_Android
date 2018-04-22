package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        TextView textViewTitle = view.findViewById(R.id.textView_title);
        TextView textViewDescription = view.findViewById(R.id.textView_description);
        TextView textViewPlaces = view.findViewById(R.id.textView_places);
        TextView textViewDate = view.findViewById(R.id.textView_date);

        textViewTitle.setText(tittle);
        textViewDescription.setText(description);
        textViewPlaces.setText("Places: "+ places);
        textViewDate.setText("Expected date: "+date);

        ImageView imageView = view.findViewById(R.id.image_view_anunci);
        imageView.setImageResource(image);

        /*TODO: Obtener el username del anunciante mediante llamada a servidor (aun no implementada)
        * TODO: con el objetivo de obtener sus datos y poder consultar su perfil*/

        /*TODO: Implementar botones "Profile" y "I want it!"*/

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
