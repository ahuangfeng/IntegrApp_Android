package com.integrapp.integrapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.integrapp.integrapp.Adverts.SingleAdvertFragment;
import com.integrapp.integrapp.Model.DataInscription;
import com.integrapp.integrapp.Inscription.InscriptionsFragment;
import com.integrapp.integrapp.Profile.ProfileFragment;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;
import com.integrapp.integrapp.Inscription.SingleInscriptionFragment;
import com.integrapp.integrapp.Model.DataAdvert;
import com.integrapp.integrapp.Model.UserDataAdvertiser;

import java.util.List;

public class InscriptionsAdapter extends BaseAdapter {

    private Context context;
    private List<DataInscription> objectList;
    private Server server;
    private FragmentActivity activity;
    private String type;

    public InscriptionsAdapter(Context context, List<DataInscription> objectList, FragmentActivity activity, String type) {
        this.context = context;
        this.objectList = objectList;
        this.activity = activity;
        this.type = type;
    }

    @Override
    public int getCount() {
        return objectList.size();
    }

    @Override
    public Object getItem(int i) {
        return objectList.get(i);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(objectList.get(position).getId());
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        server = Server.getInstance();
        final String idUser = objectList.get(i).getIdUser();
        final String idAdvert = objectList.get(i).getIdAdvert();
        View vista;
        LayoutInflater inflate = LayoutInflater.from(context);
        vista = inflate.inflate(R.layout.activity_item_inscription, null);

        final TextView objectTextView = vista.findViewById(R.id.textViewObject);
        final String info = objectList.get(i).getInfo();
        objectTextView.setText(info);
        objectTextView.setClickable(true);
        objectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("inscriptions")) {
                    getInfoAdvert(idAdvert);
                } else {
                    getInfoUser(info, idUser);
                }
            }
        });

        TextView stateTextView = vista.findViewById(R.id.textViewState);
        final String status = objectList.get(i).getState();
        stateTextView.setText(status);
        stateTextView.setClickable(true);
        stateTextView.setFocusable(true);
        stateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("pending")) {
                    if (type.equals("inscriptions")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                        builder.setMessage(R.string.dialog_delete_inscription).setTitle(R.string.tittle_dialogDeleteInscription);
                        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doServerCallForDeleteInscription(idAdvert, idUser);
                            }
                        });

                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Fragment fragment = new SingleInscriptionFragment(idAdvert, idUser);
                        android.support.v4.app.FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.replace(R.id.screen_area, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            }
        });
        return vista;
    }

    @SuppressLint("StaticFieldLeak")
    private void getInfoUser(final String username, final String idUser) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = context.getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.getUserInfoByUsername(username);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GET INFO USER")) {
                    sendInfoUserToProfile(s, idUser);
                }
                else {
                    Toast.makeText(activity, R.string.error_GettingUserInfo, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void sendInfoUserToProfile(String s, String idUser) {
        //para saber que viene de aqui y no del perfil del navigation
        Fragment fragment = new ProfileFragment("advertiserUser");
        android.support.v4.app.FragmentManager fragmentManager = activity.getSupportFragmentManager();

        //paso de parametros al ProfileFragment
        Bundle args = new Bundle();

        try {
            JSONObject myJsonObject = new JSONObject(s);
            args.putString("idUser", idUser);
            args.putString("username", myJsonObject.getString("username"));
            args.putString("name", myJsonObject.getString("name"));
            args.putString("type", myJsonObject.getString("type"));
            if (myJsonObject.has("email")) {
                args.putString("email", myJsonObject.getString("email"));
            }
            if (myJsonObject.has("phone")) {
                args.putString("phone", myJsonObject.getString("phone"));
            }
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

    @SuppressLint("StaticFieldLeak")
    private void getInfoAdvert(final String idAdvert) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = context.getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.getAdvertInfoById(idAdvert);
        }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GET INFO ADVERT")) {
                    sendInfoAdvert(s, idAdvert);
                }
                else {
                    Toast.makeText(activity, R.string.error_GettingAdvertInfo, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void sendInfoAdvert(String s, String idAdvert) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            String date, title, description, places, typeAdvert, state, userId, id, registered;
            date = jsonObject.getString("date");
            title = jsonObject.getString("title");
            description = jsonObject.getString("description");
            places = jsonObject.getString("places");
            typeAdvert = jsonObject.getString("typeAdvert");
            state = jsonObject.getString("state");
            userId = jsonObject.getString("userId");
            id = jsonObject.getString("_id");
            registered = jsonObject.getJSONArray("registered").toString();
            int image = R.drawable.project_preview_large_2;
            DataAdvert dataAdvert = new DataAdvert(date, title, description, places, typeAdvert, state, userId, image, id, registered);
            UserDataAdvertiser userDataAdvertiser = new UserDataAdvertiser(jsonObject.getString("user"));
            Fragment fragment = new SingleAdvertFragment(dataAdvert, userDataAdvertiser);
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.screen_area, fragment);
            ft.addToBackStack(null);
            ft.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void doServerCallForDeleteInscription(final String idAdvert, final String idUser) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = activity.getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                String idInscription = getIdInscriptionToDelete(preferences.getString("inscriptions", "[]"), idAdvert);
                return server.deleteInscriptionAdvert(idInscription);
            }

            @Override
            protected void onPostExecute(String s) {
                updateInscriptions(s, idUser);
            }
        }.execute();
    }

    private String getIdInscriptionToDelete(String s, String idAdvert) {
        try {
            JSONArray myJSONArray = new JSONArray(s);
            String advertId;
            for (int i = 0; i < myJSONArray.length(); ++i) {
                advertId = myJSONArray.getJSONObject(i).getString("advertId");
                if (advertId.equals(idAdvert)) {
                    return myJSONArray.getJSONObject(i).getString("_id");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "error";
    }

    private void updateInscriptions(String s, String idUser) {
        if(!s.equals("ERROR DELETING INSCRIPTION")) {
            /*if (state == "pending") {
                inscriptionButton.setText(getString(R.string.pendingButton_advert));
                inscriptionButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_pending_button));
                inscriptionButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            } else {
                inscriptionButton.setText(getString(R.string.wantItButton_advertOther));
                inscriptionButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.background_signup_button));
                inscriptionButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            }*/
            doServerCallForSaveInscriptions(idUser);
        } else {
            Toast.makeText(activity, R.string.error_DeletingInscription, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void doServerCallForSaveInscriptions(String userId) {
        final String idUser = userId;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = activity.getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.getInscriptionsByUserId(idUser);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING INSCRIPTIONS")) {
                    saveInscriptions(s);
                }
                else {
                    Toast.makeText(activity, R.string.error_GettingInscriptions, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void saveInscriptions(String inscriptions) {
        SharedPreferences preferences = activity.getSharedPreferences("login_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("inscriptions", inscriptions);
        editor.apply();
        Fragment fragment = new InscriptionsFragment();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.screen_area, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}

