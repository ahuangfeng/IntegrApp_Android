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

public class SignUp extends AppCompatActivity {

    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    LinearLayout layoutCIF;
    View viewCIF;
    Button signUpButton;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText cifEditText;
    private String itemSelectedSpinner;

    private String serverResponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        spinner = (Spinner) findViewById(R.id.spinnner);
        adapter = ArrayAdapter.createFromResource(this, R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String elem = parent.getItemAtPosition(position).toString();
                passFromSpinner(elem); //pass from spinner to server format
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

                if (((user.isEmpty() || pass.isEmpty()) && !itemSelectedSpinner.equals("association"))
                        || ((user.isEmpty() || pass.isEmpty() || cif.isEmpty()) && itemSelectedSpinner.equals("association"))) {
                    Toast.makeText(getApplicationContext(), "Fill in all the fields, please.", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendDataToServer(user, pass, cif);
                    if (!serverResponse.equals("ERROR IN SIGNUP")) {
                        Toast.makeText(getApplicationContext(), "Create successful", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(SignUp.this, Advert.class);
                        startActivity(i);
                        finish();
                        LogIn.logInActivity.finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void passFromSpinner(String element) {
        if (element.equals("Association")) itemSelectedSpinner = "association";
        else if (element.equals("Voluntary")) itemSelectedSpinner = "voluntary";
        else if (element.equals("NewComer")) itemSelectedSpinner = "newComer";
        else itemSelectedSpinner = "admin";
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
                }
            }.execute();

        } catch (JSONException e) {
            e.printStackTrace();
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
}
