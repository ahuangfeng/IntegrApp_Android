package com.integrapp.integrapp.Adverts;

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

import com.integrapp.integrapp.Inscription.InscriptionsFragment;
import com.integrapp.integrapp.MainActivity;
import com.integrapp.integrapp.Model.Advert;
import com.integrapp.integrapp.Model.UserDataAdvertiser;
import com.integrapp.integrapp.Profile.ProfileFragment;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class SingleAdvertFragment extends Fragment {

    private String title;
    private String state;
    private String places;
    private String date;
    private String description;
    private String userId;
    private int image;
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

    @SuppressLint("ValidFragment")
    public SingleAdvertFragment(Advert advert) {
        title = advert.getTitle();
        state = advert.getState();
        places = advert.getPlaces();
        date = advert.getDate();
        description = advert.getDescription();
        userId = advert.getUserDataAdvertiser().getIdUser();
        image = advert.getImage();
        registered = advert.getRegistered();
        idAdvert = advert.getId();
        this.userData = advert.getUserDataAdvertiser();
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

        updatePlaces();
        if (userData.getUsername().equals(usernamePreferences)) {
            inscriptionButton.setText(getString(R.string.wantItButton_advertOwner));
            advertStatus = "owner";
        }
        else {
            updateStatus();
        }

        inscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (advertStatus) {
                    case "owner":
                        try {
                            JSONArray inscriptions = new JSONArray(registered);
                            if(inscriptions.length() > 0){
                                Fragment fragment = new InscriptionsFragment(idAdvert, userId, getContext(), registered);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fragmentManager.beginTransaction();
                                ft.replace(R.id.screen_area, fragment);
                                ft.addToBackStack(null);
                                ft.commit();
                                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_ManageInscriptions), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.noUserInscriptions), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "canEnroll":
                        doServerCallForCreateInscription();
                        break;
                    case "pending":
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
                        break;
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
        textViewDate = view.findViewById(R.id.textView_datText);

        textViewUsername.setText(userData.getUsername());
        setEditableTexts();

        viewTitle = view.findViewById(R.id.viewTitle);
        viewDescription = view.findViewById(R.id.viewDescription);
        viewPlaces = view.findViewById(R.id.viewPlaces);
        viewDate = view.findViewById(R.id.viewDate);
        textViewState = view.findViewById(R.id.textViewState);
        if (Objects.equals(state, "opened")) textViewState.setText(getString(R.string.state_opened));
        else if (Objects.equals(state, "closed")) textViewState.setText(getString(R.string.state_closed));

        ImageView imageView = view.findViewById(R.id.image_view_anunci);
        imageView.setImageResource(image);
    }

    private void checkInscriptionStatus() {
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
                inscriptionButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.background_signup_button));
                inscriptionButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
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
                    sendInfoUserToProfile(s);
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_GettingUserInfo), Toast.LENGTH_SHORT).show();
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
        if (!advertStatus.equals("owner")) {
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_modifyAdvertState).setVisible(false);
        } else {
            menu.findItem(R.id.action_reportAdvert).setVisible(false);
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
        } else if (id == R.id.action_reportAdvert) {
            final EditText editText = new EditText(getContext());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.dialog_report).setTitle(R.string.title_dialogReportAdvert);
            builder.setView(editText);
            builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reportAdvert(editText.getText().toString());
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
                return server.deleteAdvertById(id);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN DELETING ADVERT")) {
                    Toast.makeText(getActivity(), getString(R.string.toast_AdvertDeletedSuccessfully), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), getString(R.string.error_DeletingAdvert), Toast.LENGTH_SHORT).show();
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
        if (json==null || json.equals("empty")) {
            errors = true;
            Toast.makeText(getContext(), getString(R.string.error_EmptyValuesAdded), Toast.LENGTH_SHORT).show();
        }
        else if (json.equals("places greater 0")) {
            errors = true;
            Toast.makeText(getContext(), getString(R.string.error_PlacesMustBeGreaterThatZero), Toast.LENGTH_SHORT).show();
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
                        setVisibility(false, View.INVISIBLE);
                        setAttributes();
                        Toast.makeText(getContext(), getString(R.string.toast_ChangesSaveCorrectly), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.error_SavingChangesAdvert), Toast.LENGTH_SHORT).show();
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
                return server.modifyStateAdvertById(id, json);
            }
          
            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR CHANGE ADVERT STATE")) {
                    changeState();
                    Toast.makeText(getActivity(), getString(R.string.toast_AdvertStateChangedSuccessfully), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_ChangingAdvertState), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
  
    public String generateRequestModifyStateAdvert() {
        String state_to;
        if (Objects.equals(state, "opened")) state_to = "closed";
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
        if (Objects.equals(state, "opened")) {
            textViewState.setText(getString(R.string.state_closed));
            state = "closed";
        }
        else {
            textViewState.setText(getString(R.string.state_opened));
            state = "opened";
        }
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
                    if(!s.equals("ERROR CREATING INSCRIPTION")) {
                        advertStatus = "pending";
                        updateInscriptions(s);
                        checkInscriptionStatus();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.inscription_error), Toast.LENGTH_SHORT).show();
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
                    String idInscription = getIdInscriptionToDelete(registered);
                    return server.deleteInscriptionAdvert(idInscription);
                }

                @Override
                protected void onPostExecute(String s) {
                    if(!s.equals("ERROR DELETING INSCRIPTION")) {
                        advertStatus = "canEnroll";
                        checkInscriptionStatus();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error_DeletingInscription), Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
    }

    private String getIdInscriptionToDelete(String s) {
        try {
            JSONArray myJSONArray = new JSONArray(s);
            String id;
            for (int i = 0; i < myJSONArray.length(); ++i) {
                id = myJSONArray.getJSONObject(i).getString("userId");
                if (personalUserId.equals(id)) {
                    return myJSONArray.getJSONObject(i).getString("id");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "error";
    }

    private String generateRequestInscription() throws JSONException {
        JSONObject oJSON = new JSONObject();

        oJSON.put("userId", personalUserId);
        oJSON.put("advertId", idAdvert);

        return oJSON.toString(1);
    }

    private void updatePlaces() {
        try {
            JSONArray myJSONArray = new JSONArray(registered);
            String status;
            int count = 0;
            for (int i = 0; i < myJSONArray.length(); ++i) {
                status = myJSONArray.getJSONObject(i).getString("status");
                if (status.equals("accepted")) {
                    ++count;
                }
            }
            String emptyPlaces = Integer.parseInt(places) - count + " / " + places + "";
            textViewPlaces.setText(emptyPlaces);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateStatus() {
        try {
            JSONArray myJSONArray = new JSONArray(registered);
            String id;
            advertStatus = "canEnroll";
            for (int i = 0; i < myJSONArray.length(); ++i) {
                id = myJSONArray.getJSONObject(i).getString("userId");
                if (personalUserId.equals(id)) {
                    advertStatus = myJSONArray.getJSONObject(i).getString("status");
                    break;
                }
            }
            checkInscriptionStatus();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void doServerCallForSaveInscriptions() {
        final String idUser = userId;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.getInscriptionsByUserId(personalUserId);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING INSCRIPTIONS")) {
                    saveInscriptions(s);
                }
                else {
                    Toast.makeText(getActivity(), R.string.error_GettingInscriptions, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void saveInscriptions(String inscriptions) {
        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("inscriptions", inscriptions);
        editor.apply();
    }

    private void updateInscriptions(String s) {
        doServerCallForSaveInscriptions();
        try {
            JSONArray myJSONArray = new JSONArray(registered);
            JSONObject myJSONObject =new JSONObject(s);
            myJSONObject.put("id", myJSONObject.get("_id"));
            myJSONArray.put(myJSONObject);
            registered = myJSONArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void reportAdvert(String reason) {
        try {
            final String json = generateReportData(reason);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                    server.token = preferences.getString("user_token", "user_token");
                    return server.report(json);
                }

                @Override
                protected void onPostExecute(String s) {
                    if (!s.equals("ERROR CREATING REPORT")) {
                        System.out.println("SERVER RESPONSE: " + s);
                        Toast.makeText(getActivity(), getString(R.string.advertReport_ok), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error_reportAdvert), Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String generateReportData(String reason) throws JSONException {
        JSONObject oJSON = new JSONObject();

        oJSON.put("description", reason);
        oJSON.put("type", "advert");
        oJSON.put("typeId", idAdvert);

        return oJSON.toString(1);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}