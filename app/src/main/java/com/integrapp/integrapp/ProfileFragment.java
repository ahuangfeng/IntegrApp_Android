package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private TextView nameTextView;
    private TextView usernameTextView;
    private TextView typeUserTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private String typeProfile;
    private Button saveProfileButton;
    private View nameView;
    private View usernameView;
    private View emailView;
    private View phoneView;
    private EditText currentPassEditText;
    private EditText newPassEditText;
    private EditText confirmNewPassEditText;
    private Button changePasswordButton;
    private Server server;

    public ProfileFragment() {
    }

    @SuppressLint("ValidFragment")
    public ProfileFragment(String typeProfile) {
        this.typeProfile = typeProfile;
    }

    @SuppressLint("StaticFieldLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        this.server = Server.getInstance();

        nameTextView = view.findViewById(R.id.nameTextView);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        typeUserTextView = view.findViewById(R.id.typeUserTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);

        nameView = view.findViewById(R.id.viewName);
        usernameView = view.findViewById(R.id.viewUsername);
        emailView = view.findViewById(R.id.viewEmail);
        phoneView = view.findViewById(R.id.viewPhone);
        saveProfileButton = view.findViewById(R.id.saveProfileButton);

        if (Objects.equals(typeProfile, "advertiserUser")) {
            String username = getArguments() != null ? getArguments().getString("username") : "username";
            String type = getArguments() != null ? getArguments().getString("type") : "type";
            String name = getArguments() != null ? getArguments().getString("name") : "name";
            String email = getArguments() != null ? getArguments().getString("email") : "email";
            String phone = getArguments() != null ? getArguments().getString("phone") : "phone";

            System.out.println("COSIKAAS: " + username + " " + type + " " + name+ " "+ email+ " "+ phone);

            setAttributes(name, username, type, email, phone);

        }
        else {
            SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
            String username = preferences.getString("username", "username");
            String name = preferences.getString("name", "name");
            String type = preferences.getString("type", "type");
            String email = preferences.getString("email", "email");
            String phone = preferences.getString("phone", "phone");

            setAttributes(name, username, type, email, phone);
        }
      return view;
    }

    private void getIdUser(String s) {
        try {
            JSONObject myJsonjObject = new JSONObject(s);
            String id = myJsonjObject.getString("_id");
            System.out.print("myuserid"+id);
            deleteUserById(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void deleteUserById(final String id) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.deleteUserById(id);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN DELETING USER")) {
                    System.out.println("DELETE USER SUCCESSFULL RESPONSE: " +s);
                    Toast.makeText(getActivity().getApplicationContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ProfileFragment.this.getActivity(), LogIn.class);
                    startActivity(i);
                    getActivity().finish();
                }
                else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void setAttributes(String name, String username, String type, String email, String phone) {
        nameTextView.setText(name);
        usernameTextView.setText(username);
        typeUserTextView.setText(type);
        emailTextView.setText(email);
        phoneTextView.setText(phone);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.profile, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        /*Solo se muestran las opciones de delte y edit cuando se consulta el perfil del usuario
        logueado pero no si se está consultando un perfil de un anunciante */
        if (Objects.equals(typeProfile, "advertiserUser")) {
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_changePass).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {

            SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
            server.token = preferences.getString("user_token", "user_token");
            String username = preferences.getString("username", "username");

            getIdByUsername(username);
        } else if (id == R.id.action_edit) {

            setVisibility(true, View.VISIBLE);

            saveProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage(R.string.dialog_save).setTitle(R.string.tittle_dialogSave);
                    builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setVisibility(false, View.INVISIBLE);
                            saveChanges();
                            Toast.makeText(getContext(), "Changes saved correctly", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setVisibility(false, View.INVISIBLE);
                            undoChanges();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
        else if (id == R.id.action_changePass) {
            setVisibility(false, View.INVISIBLE);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();
            dialog.show();

            currentPassEditText = dialogView.findViewById(R.id.currentPassEditText);
            newPassEditText = dialogView.findViewById(R.id.newPassEditText);
            confirmNewPassEditText = dialogView.findViewById(R.id.confirmPassEditText);
            changePasswordButton = dialogView.findViewById(R.id.changePassButton);

            changePasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String currentPass = currentPassEditText.getText().toString();
                    String newPass = newPassEditText.getText().toString();
                    String confirmPass = confirmNewPassEditText.getText().toString();

                    if (fieldsOK(currentPass, newPass, confirmPass)) {
                        if (passwordsOk(currentPass, newPass, confirmPass)) {
                            Toast.makeText(getActivity(), "Password changed correctly", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean passwordsOk(String currentPass, String newPass, String confirmPass) {
        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        server.token = preferences.getString("user_token", "user_token");
        String password = preferences.getString("password", "password");

        if (!Objects.equals(newPass, confirmPass)) {
            Toast.makeText(getActivity(), "The new passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!Objects.equals(currentPass, password)) {
            Toast.makeText(getActivity(), "Invalid current password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean fieldsOK(String currentPass, String newPass, String confirmPass) {
        boolean valid = true;

        if (currentPass.isEmpty()) {
            currentPassEditText.setError(getString(R.string.error_currentPass_empty));
            valid = false;
        }

        if (newPass.isEmpty()) {
            newPassEditText.setError(getString(R.string.error_currentPass_empty));
            valid = false;
        }else if (checkInputText(newPass, 6, 128)) {
            newPassEditText.setError(getString(R.string.error_password_length));
            valid = false;
        }

        if (confirmPass.isEmpty()) {
            confirmNewPassEditText.setError(getString(R.string.error_currentPass_empty));
            valid = false;
        }else if (checkInputText(confirmPass, 6, 128)){
            confirmNewPassEditText.setError(getString(R.string.error_password_length));
            valid = false;
        }
        return valid;
    }

    private boolean checkInputText(String text, int min, int max) {
        return text.isEmpty() || text.length() < min || text.length() > max;
    }

    private void setVisibility(boolean b, int visibility) {
        nameTextView.setEnabled(b);
        usernameTextView.setEnabled(b);
        emailTextView.setEnabled(b);
        phoneTextView.setEnabled(b);

        nameView.setVisibility(visibility);
        usernameView.setVisibility(visibility);
        emailView.setVisibility(visibility);
        phoneView.setVisibility(visibility);

        saveProfileButton.setVisibility(visibility);
    }

    private void undoChanges() {
        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        server.token = preferences.getString("user_token", "user_token");
        String username = preferences.getString("username", "username");
        String name = preferences.getString("name", "name");
        String email = preferences.getString("email", "email");
        String phone = preferences.getString("phone", "phone");
        String type = preferences.getString("type", "type");

        setAttributes(name, username, type, email, phone);
    }

    private void saveChanges() {
        /*TODO: llamada a servidor para guardar los datos modificados*/
    }

    @SuppressLint("StaticFieldLeak")
    private void getIdByUsername(final String username) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getUserInfoByUsername(username);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GET INFO USER")) {
                    System.out.println("INFO USUARI RESPONSE: " +s);
                    getIdUser(s);
                }
                else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
