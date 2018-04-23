package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

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
                    Toast.makeText(getActivity(), "Creating advert...", Toast.LENGTH_SHORT).show();
                    sendDataToServer(title, description, places);
                }
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void sendDataToServer(String title, String description, String places) {

        try {
            final String json = generateRequestNewAdvert(title, description, places);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                    server.token = preferences.getString("user_token", "user_token");
                    return server.setNewAdvert(json);
                }

                @Override
                protected void onPostExecute(String s) {
                    System.out.println("SERVER RESPONSE: " + s);
                    checkNewAdvert(s);
                }
            }.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkNewAdvert(String s) {
        if (!s.equals("ERROR IN CREATING ADVERT")) {
            Toast.makeText(getActivity(), "Advert created successful", Toast.LENGTH_SHORT).show();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.screen_area, new NewAdvertFragment());
            ft.addToBackStack(null);
            ft.commit();
        }
        else {
            Toast.makeText(getActivity(), "ERROR IN CREATING ADVERT", Toast.LENGTH_SHORT).show();
        }
    }

    private void passFromSpinner(String element) {
        switch (element) {
            case "LookFor":
                itemSelectedSpinner = "lookFor";
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

    private String generateRequestNewAdvert(String title, String description, String places) throws JSONException {
        JSONObject oJSON = new JSONObject();

        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDate.setTimeZone(TimeZone.getTimeZone("CET"));
        String date = currentDate.format(new Date());

        oJSON.put("date", date);
        oJSON.put("title", title);
        oJSON.put("description", description);
        oJSON.put("places", places);
        oJSON.put("type", itemSelectedSpinner);

        return oJSON.toString(1);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
