package com.integrapp.integrapp.Adverts;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.integrapp.integrapp.MainActivity;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class NewAdvertFragment extends Fragment {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText placesEditText;
    private EditText timeText;
    private EditText dateText;
    private String itemSelectedSpinner;
    private Server server;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_new_advert, container, false);

        this.server = Server.getInstance();
        Button postButton = view.findViewById(R.id.newAdvertPostButton);
        Button setDate = view.findViewById(R.id.setDateButton);
        Button setTime = view.findViewById(R.id.setTimeButton);
        dateText = view.findViewById(R.id.dateText);
        timeText = view.findViewById(R.id.timeText);

        Spinner spinner = view.findViewById(R.id.newAdvertSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.advert_types, R.layout.my_spinner_advert);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                passFromSpinner(position);
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
                String date = dateText.getText().toString();
                String time = timeText.getText().toString();
                if(fieldsOk(title, description, places, date, time)) {
                    Toast.makeText(getActivity(), getString(R.string.newAdvert_creating), Toast.LENGTH_SHORT).show();
                    sendDataToServer(title, description, places);
                }
            }
        });

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentDate = Calendar.getInstance();
                int cYear = currentDate.get(Calendar.YEAR);
                int cMonth = currentDate.get(Calendar.MONTH);
                int cDay = currentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog DatePicker;
                DatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                        selectedMonth = selectedMonth + 1;
                        String month = String.valueOf(selectedMonth);
                        String day = String.valueOf(selectedDay);
                        if (selectedMonth < 10)
                            month = "0" + month;
                        if (selectedDay < 10)
                            day = "0" + day;
                        String concatenatedDate = selectedYear + "-" + month + "-" + day;
                        dateText.setText(concatenatedDate);
                    }
                }, cYear, cMonth, cDay);
                DatePicker.show();
            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int cHour = currentTime.get(Calendar.HOUR_OF_DAY);
                int cMinute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog TimePicker;
                TimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String hour = String.valueOf(selectedHour);
                        String minute = String.valueOf(selectedMinute);
                        if (selectedHour < 10)
                            hour = "0" + hour;
                        if (selectedMinute < 10)
                            minute = "0" + minute;
                        String concatenatedTime = hour + ":" + minute;
                        timeText.setText(concatenatedTime);
                    }
                }, cHour, cMinute, true);
                TimePicker.show();
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
                    checkNewAdvert(s);
                }
            }.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void passFromSpinner(int position) {
        switch (position) {
            case 0:
                itemSelectedSpinner = "lookFor";
                break;
            case 1:
                itemSelectedSpinner = "offer";
                break;
            default:
        }
    }

    private void checkNewAdvert(String s) {
        if (!s.equals("ERROR CREATING ADVERT")) {
            Toast.makeText(getActivity(), getString(R.string.newAdvert_success), Toast.LENGTH_SHORT).show();

            SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            int ads = preferences.getInt("ads", 0);
            editor.putInt("ads", ads+1);
            editor.apply();

            Intent i = new Intent(this.getActivity(), MainActivity.class);
            startActivity(i);
            this.getActivity().finish();
        }
        else {
            Toast.makeText(getActivity(), getString(R.string.newAdvert_error), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean fieldsOk(String title, String description, String places, String date, String time) {
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

        if (Integer.parseInt(places) <= 0) {
            placesEditText.setError(getString(R.string.error_places_lte0));
            valid = false;
        }

        if (date.isEmpty()) {
            dateText.setError(getString(R.string.error_date_not_selected));
            valid = false;
        }

        if (time.isEmpty()) {
            timeText.setError(getString(R.string.error_time_not_selected));
            valid = false;
        }
        return valid;
    }

    private String generateRequestNewAdvert(String title, String description, String places) throws JSONException {
        JSONObject oJSON = new JSONObject();

        String expectedDate = dateText.getText().toString() + " " + timeText.getText().toString() + ":00";

        oJSON.put("date", expectedDate);
        oJSON.put("title", title);
        oJSON.put("description", description);
        oJSON.put("places", places);
        oJSON.put("typeAdvert", itemSelectedSpinner);

        return oJSON.toString(1);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
