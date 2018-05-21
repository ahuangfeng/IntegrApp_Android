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
import android.support.v4.content.ContextCompat;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private String registered;

    private Button inscriptionButton;
    private String personalUserId;

    private EditText textViewTitle;
    private EditText textViewDescription;
    private EditText textViewPlaces;
    private EditText textViewDate;
    private View viewTitle;
    private View viewDescription;
    private View viewPlaces;
    private View viewDate;
    private TextView textViewState;
    private String advertStatus;

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
        registered = dataAdvert.getRegistered();
        idAdvert = dataAdvert.getId();
        this.userData = userData;

        System.out.println("Parametros: " +title + " " + type + " " + state + " " +places+ " "+ date+ " "+ description + " "+ userId + " " + idAdvert + " " + registered);
    }

    @SuppressLint("StaticFieldLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        server = Server.getInstance();
        View view = inflater.inflate(R.layout.single_advert_fragment, container, false);

        loadAdvertData(view);
        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        String usernamePreferences = preferences.getString("username", "username");
        personalUserId = preferences.getString("idUser", "null");

        inscriptionButton = view.findViewById(R.id.inscriptionButton);

        if (userData.getUsername().equals(usernamePreferences)) {
            type_advert = "owner";
            inscriptionButton.setText(getString(R.string.wantItButton_advertOwner));
            advertStatus = "owner";
        }
        else {
            type_advert = "other";
            updatePlacesAndStatus();
        }

        inscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (advertStatus.equals("owner")) {
                    Fragment fragment = new InscriptionsFragment(idAdvert, userId, getContext());
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.screen_area, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    // TODO: Manage inscriptions
                    Toast.makeText(getActivity().getApplicationContext(), "Manage inscriptions", Toast.LENGTH_SHORT).show();
                } else if (advertStatus.equals("canEnroll")) {
                    doServerCallForCreateInscription();
                } else if (advertStatus.equals("pending")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage(R.string.dialog_delete_inscription).setTitle(R.string.tittle_dialogDeleteInscription);
                    builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doServerCallForDeleteInscription();
                        }
                    });

                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        Button profileButton = view.findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfoUser(userData.getUsername());
            }
        });

        return view;
    }

    private void loadAdvertData(View view) {
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
        textViewState = view.findViewById(R.id.textViewState);
        textViewState.setText(state.toUpperCase());

        ImageView imageView = view.findViewById(R.id.image_view_anunci);
        imageView.setImageResource(image);
    }

    private void checkInscriptionStatus() {
        inscriptionButton.setVisibility(View.VISIBLE);
        inscriptionButton.setClickable(false);
        switch (advertStatus) {
            case "pending":
                inscriptionButton.setText(getString(R.string.pendingButton_advert));
                inscriptionButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_pending_button));
                inscriptionButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                inscriptionButton.setClickable(true);
                break;
            case "canEnroll":
                inscriptionButton.setText(getString(R.string.wantItButton_advert));
                inscriptionButton.setClickable(true);
                break;
            case "refused":
                inscriptionButton.setText(getString(R.string.refusedButton_advert));
                inscriptionButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_refused_button));
                break;
            case "completed":
                inscriptionButton.setText(getString(R.string.completedButton_advert));
                inscriptionButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_completed_button));
                break;
            case "accepted":
                inscriptionButton.setText(getString(R.string.acceptedButton_advert));
                inscriptionButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_accepted_button));
                inscriptionButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                break;
            default:
        }
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
            menu.findItem(R.id.action_modifyAdvertState).setVisible(false);
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
            inscriptionButton.setOnClickListener(new View.OnClickListener() {
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
        } else if (id == R.id.action_modifyAdvertState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.dialog_modify_state_advert).setTitle(R.string.tittle_dialogModifyStateAdvert);
            builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    modifyStateAdvertById(idAdvert);
                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setEditableTexts() {   
        textViewTitle.setText(title);
        textViewDescription.setText(description);
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
                    SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    int ads = preferences.getInt("ads", 0);
                    editor.putInt("ads", ads-1);
                    editor.apply();

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

        inscriptionButton.setText(R.string.wantItButton_editadvert);
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

    @SuppressLint("StaticFieldLeak")
    private void modifyStateAdvertById (final String id) {
        final String json = generateRequestModifyStateAdvert();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                System.out.print("myadvertid:"+id);
                return server.modifyStateAdvertById(id, json);
            }
          
            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR CHANGE ADVERT STATE")) {
                    changeState();
                    System.out.println("CHANGE ADVERT STATE SUCCESSFULL RESPONSE: " +s);
                    Toast.makeText(getActivity().getApplicationContext(), "Advert State changed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
  
    public String generateRequestModifyStateAdvert() {
        String state_to;
        if (state.equals("opened")) state_to = "closed";
        else state_to = "opened";
        try {
            JSONObject oJSON = new JSONObject();
            oJSON.put("state", state_to);
            return oJSON.toString(1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void changeState() {
        if (state.equals("opened")) state = "closed";
        else state = "opened";
        textViewState.setText(state.toUpperCase());
        System.out.print("mystate "+state);

    }

    @SuppressLint("StaticFieldLeak")
    private void doServerCallForCreateInscription() {
        try {
            final String json = generateRequestInscription();
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                    server.token = preferences.getString("user_token", "user_token");
                    return server.createInscriptionAdvert(json);
                }

                @Override
                protected void onPostExecute(String s) {
                    if (!s.equals("ERROR CREATING INSCRIPTION")) {
                        System.out.println("SERVER RESPONSE: " + s);
                        updateInscriptions(s, "pending");
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error_noPlaces), Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void doServerCallForDeleteInscription() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.deleteInscriptionAdvert();
            }

            @Override
            protected void onPostExecute(String s) {
                System.out.println("SERVER RESPONSE: " + s);
                updateInscriptions(s, "canEnroll");
            }
        }.execute();
    }
  
    private void updateInscriptions(String s, String state) {
        if(!s.equals("ERROR DELETING INSCRIPTION")) {
            if (state.equals("pending")) {
                inscriptionButton.setText(getString(R.string.pendingButton_advert));
                inscriptionButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_pending_button));
                inscriptionButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            } else {
                inscriptionButton.setText(getString(R.string.wantItButton_advertOther));
                inscriptionButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.background_signup_button));
                inscriptionButton.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteLetter));
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.inscription_error), Toast.LENGTH_SHORT).show();
        }
    }
  
    private String generateRequestInscription() throws JSONException {
        JSONObject oJSON = new JSONObject();

        oJSON.put("userId", personalUserId);
        oJSON.put("advertId", idAdvert);

        return oJSON.toString(1);
    }

    private void updatePlacesAndStatus() {
        try {
            JSONArray myJSONArray = new JSONArray(registered);
            String status;
            String id;
            advertStatus = "canEnroll";
            int count = 0;
            for (int i = 0; i < myJSONArray.length(); ++i) {
                status = myJSONArray.getJSONObject(i).getString("status");
                id = myJSONArray.getJSONObject(i).getString("userId");
                if (status.equals("accepted")) {
                    ++count;
                }
                if (personalUserId.equals(id)) {
                    advertStatus = myJSONArray.getJSONObject(i).getString("status");
                }
            }
            String emptyPlaces = Integer.parseInt(places) - count + " / " + places + "";
            textViewPlaces.setText(emptyPlaces);
            checkInscriptionStatus();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}