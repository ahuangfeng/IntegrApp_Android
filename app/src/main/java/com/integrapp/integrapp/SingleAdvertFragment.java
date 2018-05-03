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
    private Button inscriptionButton;

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

        System.out.println("Parametros: " +title + " " + type + " " + state + " " +places+ " "+ date+ " "+ description + " "+ userId);
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

        inscriptionButton = view.findViewById(R.id.inscriptionButton);

        if (userData.getUsername().equals(usernamePreferences)) {
            type_advert = "owner";
            inscriptionButton.setText(getString(R.string.manageInscriptions));
        }
        else {
            type_advert = "other";
            String userInscriptions = preferences.getString("inscriptions", "[]");
            String advertStatus = checkAdvertStatus(userInscriptions);
            checkInscriptionStatus(advertStatus);
        }

        inscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doServerCallForCreateInscription();
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
        TextView textViewTitle = view.findViewById(R.id.textView_title);
        TextView textViewDescription = view.findViewById(R.id.textView_description);
        TextView textViewPlaces = view.findViewById(R.id.textView_places);
        TextView textViewDate = view.findViewById(R.id.textView_date);

        textViewUsername.setText(userData.getUsername());
        textViewTitle.setText(title);
        textViewDescription.setText(description);
        String textPlaces = getString(R.string.places_advert) + places;
        textViewPlaces.setText(textPlaces);
        String textDate = getString(R.string.expectedDate_advert) + date;
        textViewDate.setText(textDate);

        ImageView imageView = view.findViewById(R.id.image_view_anunci);
        imageView.setImageResource(image);
    }

    private void checkInscriptionStatus(String advertStatus) {
        inscriptionButton.setClickable(false);
        switch (advertStatus) {
            case "pending":
                inscriptionButton.setText(getString(R.string.pendingButton_advert));
                inscriptionButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_pending_button));
                inscriptionButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
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

    private String checkAdvertStatus(String userInscriptions) {
        try {
            JSONArray myJSONArray = new JSONArray(userInscriptions);
            System.out.println("QUE HAY AQUI COÑO ---- " + myJSONArray);
            String advertId;
            for (int i = 0; i < myJSONArray.length(); ++i) {
                advertId = myJSONArray.getJSONObject(i).getString("advertId");
                if (advertId.equals(idAdvert)) {
                    String status = myJSONArray.getJSONObject(i).getString("status");
                    return status;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "canEnroll";

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

            builder.setMessage(R.string.dialog_save).setTitle(R.string.tittle_dialogSave);
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

        }
        return super.onOptionsItemSelected(item);
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
                    System.out.println("SERVER RESPONSE: " + s);
                    updateInscriptions(s);
                }
            }.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateInscriptions(String s) {
        if(!s.equals("ERROR CREATING INSCRIPTION")) {
            inscriptionButton.setText(getString(R.string.pendingButton_advert));
        }
        else {
            Toast.makeText(getActivity(), getString(R.string.inscription_error), Toast.LENGTH_SHORT).show();
        }
    }

    private String generateRequestInscription() throws JSONException {
        JSONObject oJSON = new JSONObject();

        oJSON.put("userId", userId);
        oJSON.put("advertId", idAdvert);

        return oJSON.toString(1);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
