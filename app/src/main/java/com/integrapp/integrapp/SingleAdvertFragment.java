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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class SingleAdvertFragment extends Fragment {

    private String title;
    private String type;
    private String state;
    private String places;
    private String date;
    private String description;
    private String userId;
    private int image;
    private String type_advert;
    private String idAdvert;
    private EditText textViewTitle;
    private EditText textViewDescription;
    private EditText textViewPlaces;
    private EditText textViewDate;
    private View viewTitle;
    private View viewDescription;
    private View viewPlaces;
    private View viewDate;

    private Button button;


    UserDataAdvertiser userData;
    private Server server;

    public SingleAdvertFragment() {
    }

    @SuppressLint({"ValidFragment", "SetTextI18n"})
    public SingleAdvertFragment(DataAdvert dataAdvert, UserDataAdvertiser userData) {
        title = dataAdvert.getTitle();
        type = dataAdvert.getType();
        state = dataAdvert.getState();
        places = dataAdvert.getPlaces();
        date = dataAdvert.getDate();
        description = dataAdvert.getDescription();
        userId = dataAdvert.getUserId();
        image = dataAdvert.getImage();

        idAdvert = dataAdvert.getId();
        this.userData = userData;

        System.out.println("Parametros: " + title + " " + type + " " + state + " " +places+ " "+ date+ " "+ description + " "+ userId);
    }

    @SuppressLint("StaticFieldLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        server = Server.getInstance();
        View view = inflater.inflate(R.layout.single_advert_fragment, container, false);

        TextView textViewUsername = view.findViewById(R.id.textView_username);
        textViewTitle = view.findViewById(R.id.textView_title);
        textViewDescription = view.findViewById(R.id.textView_description);
        textViewPlaces = view.findViewById(R.id.textView_places);
        textViewDate = view.findViewById(R.id.textView_date);

        textViewUsername.setText(userData.getUsername());
        setEditableTexts();

        viewTitle = view.findViewById(R.id.viewTitle);
        viewDescription = view.findViewById(R.id.viewDescription);
        viewPlaces = view.findViewById(R.id.viewPlaces);
        viewDate = view.findViewById(R.id.viewDate);

        button = view.findViewById(R.id.wantitButton);

        ImageView imageView = view.findViewById(R.id.image_view_anunci);
        imageView.setImageResource(image);

        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        String usernamePreferences = preferences.getString("username", "username");

        if (userData.getUsername().equals(usernamePreferences)) {
            type_advert = "owner";
            button.setText(R.string.wantItButton_advertOwner);
        }
        else type_advert = "other";

        /*TODO: Implementar boton "I want it!"*/

        Button profileButton = view.findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfoUser(userData.getUsername());
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void getInfoUser(final String username) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.getUserInfoByUsername(username);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GET INFO USER")) {
                    System.out.println("INFO USUARI RESPONSE: " +s);
                    sendInfoUserToProfile(s);
                }
            }
        }.execute();
    }

    private void sendInfoUserToProfile(String s) {
        //para saber que viene de aqui y no del perfil del navigation
        Fragment fragment = new ProfileFragment("advertiserUser");
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        //paso de parametros al ProfileFragment
        Bundle args = new Bundle();
        args.putString("idUser", userData.getIdUser());
        args.putString("username", userData.getUsername());
        args.putString("name", userData.getName());
        args.putString("type", userData.getType());
        args.putString("email", userData.getEmail());
        args.putString("phone", userData.getPhone());

        try {
            JSONObject myJsonObject = new JSONObject(s);
            int likes = myJsonObject.getJSONObject("rate").getInt("likes");
            int dislikes = myJsonObject.getJSONObject("rate").getInt("dislikes");
            args.putInt("likes", likes);
            args.putInt("dislikes", dislikes);
            int ads = myJsonObject.getJSONArray("adverts").length();
            args.putInt("ads", ads);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fragment.setArguments(args);

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.screen_area, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.advert, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        /*Solo se muestran las opciones de delte y edit cuando se consulta un advert del usuario
        logueado pero no si se está consultando el de algun otro */
        if (type_advert.equals("other")) {
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_edit).setVisible(false);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.dialog_delete_advert).setTitle(R.string.tittle_dialogDeleteAdvert);
            builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteAdvertById(idAdvert);
                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (id == R.id.action_edit) {
            setVisibility(true, View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage(R.string.dialog_save).setTitle(R.string.tittle_dialogSave);
                    builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveChanges(idAdvert);
                        }
                    });

                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setVisibility(false, View.INVISIBLE);
                            setEditableTexts();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void setEditableTexts() {
        textViewTitle.setText(title);
        textViewDescription.setText(description);
        textViewPlaces.setText(places);
        textViewDate.setText(date);
    }

    private void setAttributes() {
        title = textViewTitle.getText().toString();
        description = textViewDescription.getText().toString();
        places = textViewPlaces.getText().toString();
        date = textViewDate.getText().toString();
    }

    @SuppressLint("StaticFieldLeak")
    private void deleteAdvertById(final String id) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                System.out.print("myadvertid:"+id);
                return server.deleteAdvertById(id);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN DELETING ADVERT")) {
                    System.out.println("DELETE ADVERT SUCCESSFULL RESPONSE: " +s);
                    Toast.makeText(getActivity().getApplicationContext(), "Advert deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SingleAdvertFragment.this.getActivity(), MainActivity.class);
                    startActivity(i);
                    getActivity().finish();

                }
                else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void setVisibility(boolean b, int visibility) {
        textViewTitle.setEnabled(b);
        textViewDescription.setEnabled(b);
        textViewPlaces.setEnabled(b);
        textViewDate.setEnabled(b);

        viewTitle.setVisibility(visibility);
        viewDescription.setVisibility(visibility);
        viewPlaces.setVisibility(visibility);
        viewDate.setVisibility(visibility);

        button.setText(R.string.wantItButton_editadvert);
    }

    @SuppressLint("StaticFieldLeak")
    private void saveChanges(final String idAdvert) {
        final String json = generateRequestModifyAdvert();
        Boolean errors = false;
        if (json.equals("empty")) {
            errors = true;
            Toast.makeText(getContext(), "Error empty values added", Toast.LENGTH_SHORT).show();
        }
        else if (json.equals("places greater 0")) {
            errors = true;
            Toast.makeText(getContext(), "Error places must be greater than 0", Toast.LENGTH_SHORT).show();
        }

        if (!errors) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    return server.modifyAdvertById(idAdvert, json);
                }

                @Override
                protected void onPostExecute(String s) {
                    if (!s.equals("ERROR MODIFY ADVERT")) {
                        System.out.println("MODIFY RESPONSE " + s);
                        setVisibility(false, View.INVISIBLE);
                        setAttributes();
                        Toast.makeText(getContext(), "Changes saved correctly", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error saving changes, check date", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
    }

    private String generateRequestModifyAdvert() {

        String title2 = textViewTitle.getText().toString();
        String description2 = textViewDescription.getText().toString();
        String places2 = textViewPlaces.getText().toString();
        String date2 = textViewDate.getText().toString();

        try {
            JSONObject oJSON = new JSONObject();
            if (!date2.isEmpty()) {
                oJSON.put("date", date2);
            } else return "empty";

            if (!title2.isEmpty() && !description2.isEmpty()) {
                oJSON.put("title", title2);
                oJSON.put("description", description2);
            } else return "empty";

            if (!places2.isEmpty()) {
                Integer i_places = Integer.parseInt(places2);
                if (i_places <= 0) return "places greater 0";
                oJSON.put("places", places2);
            } else return "empty";

            return oJSON.toString(1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
