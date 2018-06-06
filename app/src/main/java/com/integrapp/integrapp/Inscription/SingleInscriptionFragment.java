package com.integrapp.integrapp.Inscription;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.integrapp.integrapp.Inscription.InscriptionsFragment;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("ValidFragment")
public class SingleInscriptionFragment extends Fragment {
    private String idAdvert;
    private String idUser;
    private Server server;
    private ImageView green_tickImageView;
    private ImageView red_crossImageView;
    private SharedPreferences preferences;

    @SuppressLint("ValidFragment")
    public SingleInscriptionFragment(String idAdvert, String idUser) {
        this.idAdvert = idAdvert;
        this.idUser = idUser;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        server = Server.getInstance();
        View view = inflater.inflate(R.layout.single_inscription_fragment, container, false);

        preferences = getContext().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        server.token = preferences.getString("user_token", "user_token");

        green_tickImageView = view.findViewById(R.id.imageView3);
        red_crossImageView = view.findViewById(R.id.imageView4);
        green_tickImageView.setClickable(true);
        red_crossImageView.setClickable(true);
        green_tickImageView.setFocusable(true);
        red_crossImageView.setFocusable(true);

        green_tickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String json = generateRequestModifyStateAdvert("accepted");
                changeInscriptionStatus(json);
            }
        });

        red_crossImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String json = generateRequestModifyStateAdvert("refused");
                changeInscriptionStatus(json);
            }
        });

        return view;
    }

    public String generateRequestModifyStateAdvert(String state) {
        try {
            JSONObject oJSON = new JSONObject();
            oJSON.put("userId", idUser);
            oJSON.put("status", state);
            return oJSON.toString(1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    private void changeInscriptionStatus(final String json) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                server.token = preferences.getString("user_token", "user_token");
                return server.setInscriptionStatus(idAdvert, json);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN SET STATUS INSCRIPTION")) {
                    Fragment fragment = new InscriptionsFragment(idAdvert, idUser, getContext(), null);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.screen_area, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_SettingInscriptionStatus), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
