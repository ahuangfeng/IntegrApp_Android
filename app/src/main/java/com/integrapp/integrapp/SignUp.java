package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    LinearLayout layoutCIF;
    View viewCIF;
    Button signUpButton;
    EditText nameEditText;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText cifEditText;
    private String itemSelectedSpinner;

    private static final String USERNAME_PATTERN = "^[A-z0-9_-]{3,20}$";
    private Pattern pattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        pattern = Pattern.compile(USERNAME_PATTERN);

        spinner = (Spinner) findViewById(R.id.spinnner);
        adapter = ArrayAdapter.createFromResource(this, R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String elem = parent.getItemAtPosition(position).toString();
                passFromSpinner(elem);
                layoutCIF = (LinearLayout) findViewById(R.id.layoutCIF);
                viewCIF = (View) findViewById(R.id.viewCIF);

                if(Objects.equals(elem, "Association")) {
                    layoutCIF.setVisibility(View.VISIBLE);
                    viewCIF.setVisibility(View.VISIBLE);
                }
                else {
                    layoutCIF.setVisibility(View.INVISIBLE);
                    viewCIF.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signUpButton = (Button) findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usernameEditText = (EditText) findViewById(R.id.usernameEditText);
                passwordEditText = (EditText) findViewById(R.id.passwordEditText);
                cifEditText = (EditText) findViewById(R.id.cifEditText);

                String user = usernameEditText.getText().toString();
                String pass = passwordEditText.getText().toString();
                String cif = cifEditText.getText().toString();

                if (fieldsOK(user, pass, cif)) {
                    sendDataToServer(user, pass, cif);
                }
            }
        });
    }

    private void passFromSpinner(String element) {
        switch (element) {
            case "Association":
                itemSelectedSpinner = "association";
                break;
            case "Voluntary":
                itemSelectedSpinner = "voluntary";
                break;
            case "NewComer":
                itemSelectedSpinner = "newComer";
                break;
            default:
        }
    }

    private String generateRequestRegister(String username, String password, String cif) throws JSONException {

        JSONObject oJSON = new JSONObject();

        oJSON.put("username", username);
        oJSON.put("password", password);
        oJSON.put("type", itemSelectedSpinner);

        if (itemSelectedSpinner.equals("association")) {
            oJSON.put("CIF", cif);
        }
        return oJSON.toString(1);
    }

    @SuppressLint("StaticFieldLeak")
    private void sendDataToServer(String username, String password, String cif) {

        try {
            final String json = generateRequestRegister(username, password, cif);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    return getServerResponse(json);
                }

                @Override
                protected void onPostExecute(String s) {
                    System.out.println("SERVER RESPONSE: " + s);
                    checkSignUp(s);
                }
            }.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkSignUp(String s) {
        if (!s.equals("ERROR IN SIGNUP")) {
            Toast.makeText(getApplicationContext(), "Create successful", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(SignUp.this, LogIn.class);
            startActivity(i);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();
        }
    }

    private String getServerResponse(String json) {
        HttpPost post = new HttpPost("https://integrappbackend.herokuapp.com/api/register");
        try {
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");

            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();
            return client.execute(post, handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR IN SIGNUP";
    }

    public boolean fieldsOK(String user, String pass, String cif) {

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        boolean valid = true;

        if (nameEditText.getText().toString().isEmpty()) {
            nameEditText.setError(getString(R.string.error_fullName_empty));
            valid = false;
        }

        if (checkInputText(user, 3, 20)) {
            usernameEditText.setError(getString(R.string.error_username_length));
            valid = false;
        } else {
            if (!validateUsername(user)) {
                usernameEditText.setError(getString(R.string.error_username_chars));
                valid = false;
            }
        }

        if (checkInputText(pass, 6, 128)) {
            passwordEditText.setError(getString(R.string.error_password_length));
            valid = false;
        }

        if (itemSelectedSpinner.equals("association") && checkInputText(cif, 9, 9)) {
            cifEditText.setError(getString(R.string.error_cif_empty));
            valid = false;
        }

        /*if (((user.isEmpty() || pass.isEmpty()) && !itemSelectedSpinner.equals("association"))
                || ((user.isEmpty() || pass.isEmpty() || cif.isEmpty()) && itemSelectedSpinner.equals("association"))) {
            Toast.makeText(getApplicationContext(), "Fill in all the fields, please.", Toast.LENGTH_SHORT).show();
        }*/

        return valid;
    }

    public boolean checkInputText(String text, int min, int max) {
        return text.isEmpty() || text.length() < min || text.length() > max;
    }

    public boolean validateUsername(String user) {
        Matcher matcher = pattern.matcher(user);
        return matcher.matches();
    }
}
