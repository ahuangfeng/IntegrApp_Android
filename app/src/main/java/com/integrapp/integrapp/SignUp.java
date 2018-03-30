package com.integrapp.integrapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    LinearLayout layoutCIF;
    View viewCIF;
    Button signUpButton;

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
                Toast.makeText(getApplicationContext(), "Create successful", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(SignUp.this, Advert.class);
                startActivity(i);
                finish();
                LogIn.logInActivity.finish();
            }
        });
    }
}
