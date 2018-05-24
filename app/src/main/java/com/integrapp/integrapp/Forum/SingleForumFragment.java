package com.integrapp.integrapp.Forum;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.integrapp.integrapp.Profile.ProfileFragment;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;
import com.integrapp.integrapp.model.ForumItem;
import com.integrapp.integrapp.model.UserDataAdvertiser;

import org.json.JSONException;
import org.json.JSONObject;

public class SingleForumFragment extends Fragment {

    private String id;
    private String type;
    private String title;
    private String description;
    private String createdAt;
    private String userId;
    private float rate;
    UserDataAdvertiser user;
    private Server server;

    public SingleForumFragment () {}

    @SuppressLint("ValidFragment")
    public SingleForumFragment(ForumItem forumItem) {
        this.id = forumItem.getId();
        this.type = forumItem.getType();
        this.title = forumItem.getTitle();
        this.description = forumItem.getDescription();
        this.createdAt = forumItem.getCreatedAt();
        this.userId = forumItem.getUserId();
        this.rate = forumItem.getRate();
        this.user = forumItem.getUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.single_forum_fragment, container, false);
        server = Server.getInstance();

        Button profileButton = view.findViewById(R.id.profileButton);
        Button commentButton = view.findViewById(R.id.commentButton);

        TextView username = view.findViewById(R.id.textViewUsernameForum);
        TextView title = view.findViewById(R.id.textViewTitleForum);
        TextView createdAt = view.findViewById(R.id.textViewCreatedAtForum);
        TextView description = view.findViewById(R.id.textViewDescriptionForum);
        
        username.setText(this.user.getUsername());
        title.setText(this.title);
        createdAt.setText(this.createdAt);
        description.setText(this.description);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfoUser(user.getUsername());
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Boton comment clickado", Toast.LENGTH_SHORT).show();
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
        args.putString("idUser", user.getIdUser());
        args.putString("username", user.getUsername());
        args.putString("name", user.getName());
        args.putString("type", user.getType());
        args.putString("email", user.getEmail());
        args.putString("phone", user.getPhone());

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
