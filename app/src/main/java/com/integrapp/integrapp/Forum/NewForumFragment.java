package com.integrapp.integrapp.Forum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.integrapp.integrapp.Profile.ProfileFragment;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;

import org.json.JSONException;
import org.json.JSONObject;

public class NewForumFragment extends Fragment {

    private Server server;
    private Spinner spinner;
    private EditText title;
    private EditText content;
    private Button createButton;
    private FragmentActivity activity;
    private String itemSelectedSpinner;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.new_forum, container, false);
        this.server = Server.getInstance();
        spinner = view.findViewById(R.id.spinner_type);
        title = view.findViewById(R.id.title_forum);
        content = view.findViewById(R.id.content_forum);
        createButton = view.findViewById(R.id.create_forum);
        activity = this.getActivity();

        spinner = view.findViewById(R.id.spinner_type);
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.forum_types, R.layout.my_spinner_advert);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                passFromSpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title_forum = title.getText().toString();
                String type_forum = itemSelectedSpinner;
                String content_forum = content.getText().toString();

                if(fieldsOk(title_forum, content_forum)) {
                    Toast.makeText(NewForumFragment.this.getActivity(), getString(R.string.toast_PostingForum), Toast.LENGTH_SHORT).show();
                    sendDataToServer(title_forum, type_forum, content_forum);
                }
            }
        });

        return view;
    }

    private void passFromSpinner(int position) {
        switch (position) {
            case 0:
                itemSelectedSpinner = "documentation";
                break;
            case 1:
                itemSelectedSpinner = "language";
                break;
            case 2:
                itemSelectedSpinner = "entertainment";
                break;
            case 3:
                itemSelectedSpinner = "various";
                break;
            default:
        }
    }

    private boolean fieldsOk(String title, String content_forum) {

        if (title.length()<=0 || title.equals("My forum") || content_forum.length()<=0 || content_forum.equals("Describe your forum")) {
            Toast.makeText(getActivity(), getString(R.string.toast_FillNewForumFields), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), getString(R.string.toast_ForumCreatedSuccessfully), Toast.LENGTH_SHORT).show();
            getFragmentManager().popBackStack();
            InputMethodManager imm = (InputMethodManager)NewForumFragment.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
        }
        else {
            Toast.makeText(getActivity(), getString(R.string.error_CreatingForum), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar= getActivity().findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(R.string.new_forum);
        }
    }

}
