package com.integrapp.integrapp.Forum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewForumFragment extends Fragment {

    private Server server;
    private Spinner spinner;
    private EditText title;
    private EditText content;
    private Button createButton;
    private FragmentActivity activity;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.new_forum, container, false);
        this.server = Server.getInstance();
        spinner = view.findViewById(R.id.spinner_type);
        title = view.findViewById(R.id.title_forum);
        content = view.findViewById(R.id.content_forum);
        createButton = view.findViewById(R.id.create_forum);
        activity = this.getActivity();

        // Spinner Drop down elements
        List<String> types = new ArrayList<>();
        types.add("documentation");
        types.add("language");
        types.add("entertainment");
        types.add("various");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, types);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title_forum = title.getText().toString();
                String type_forum = spinner.getSelectedItem().toString();
                String content_forum = content.getText().toString();

                if(fieldsOk(title_forum, content_forum)) {
                    Toast.makeText(getActivity(), "Posting your new forum...", Toast.LENGTH_SHORT).show();
                    sendDataToServer(title_forum, type_forum, content_forum);
                }
            }
        });

        return view;
    }

    private boolean fieldsOk(String title, String content_forum) {

        if (title.length()<=0 || title.equals("My forum") || content_forum.length()<=0 || content_forum.equals("Describe your forum")) {
            Toast.makeText(getActivity(), "Fill in the title and the description correctly", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }

    @SuppressLint("StaticFieldLeak")
    private void sendDataToServer(String title_forum, String type_forum, String content_forum) {

        try {
            final String json = generateRequestNewForum(title_forum, type_forum, content_forum);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    SharedPreferences preferences = activity.getSharedPreferences("login_data", Context.MODE_PRIVATE);
                    server.token = preferences.getString("user_token", "user_token");
                    return server.postNewForum(json);
                }

                @Override
                protected void onPostExecute(String s) {
                    System.out.println("SERVER RESPONSE: " + s);
                    checkNewForum(s);
                }
            }.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String generateRequestNewForum(String title, String type, String content) throws JSONException {
        JSONObject oJSON = new JSONObject();

        oJSON.put("title", title);
        oJSON.put("description", content);
        oJSON.put("type", type);

        return oJSON.toString(1);
    }

    private void checkNewForum(String s) {
        if (!s.equals("ERROR CREATING FORUM")) {
            Toast.makeText(getActivity(), "New forum created successfully", Toast.LENGTH_SHORT).show();
            getFragmentManager().popBackStack();
        }
        else {
            Toast.makeText(getActivity(), "The forum could not be created", Toast.LENGTH_SHORT).show();
        }
    }

}
