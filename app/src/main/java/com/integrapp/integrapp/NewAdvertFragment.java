package com.integrapp.integrapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Objects;

public class NewAdvertFragment extends Fragment {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText placesEditText;
    private String itemSelectedSpinner;
    private String element_spinner;
    private Server server;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_new_advert, container, false);

        this.server = Server.getInstance();
        Button postButton = view.findViewById(R.id.newAdvertPostButton);

        Spinner spinner = view.findViewById(R.id.newAdvertSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.advert_types, R.layout.my_spinner_advert);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                element_spinner = parent.getItemAtPosition(position).toString();
                passFromSpinner(element_spinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                titleEditText = view.findViewById(R.id.newAdvertTitleEditText);
                descriptionEditText = view.findViewById(R.id.newAdvertDescriptionEditText);
                placesEditText = view.findViewById(R.id.newAdvertPlacesEditText);

                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String places = placesEditText.getText().toString();
                if(fieldsOk(title, description, places)) {
                    Toast.makeText(getActivity(), "Connecting...", Toast.LENGTH_SHORT).show();
                    //sendDataToServer(user, pass);
                }
            }
        });

        return view;
    }

    private void passFromSpinner(String element) {
        switch (element) {
            case "LookFor":
                itemSelectedSpinner = "lookFor";
                System.out.printf("holaaaaaaaaaa: " + itemSelectedSpinner);
                break;
            case "Offer":
                itemSelectedSpinner = "offer";
                break;
            default:
        }
    }

    private boolean fieldsOk(String title, String description, String places) {
        boolean valid = true;
        if (title.isEmpty()) {
            titleEditText.setError(getString(R.string.error_title_empty));
            valid = false;
        }

        if (description.isEmpty()) {
            descriptionEditText.setError(getString(R.string.error_description_empty));
            valid = false;
        }

        if (places.isEmpty()) {
            placesEditText.setError(getString(R.string.error_places_empty));
            valid = false;
        }

        return valid;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
