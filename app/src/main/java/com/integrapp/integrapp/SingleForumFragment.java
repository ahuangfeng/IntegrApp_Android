package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

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
    private List<DataComment> comments;

    public SingleForumFragment () {}

    @SuppressLint("ValidFragment")
    public SingleForumFragment(ForumItem forumItem, List<DataComment> comments) {
        this.id = forumItem.getId();
        this.type = forumItem.getType();
        this.title = forumItem.getTitle();
        this.description = forumItem.getDescription();
        this.createdAt = forumItem.getCreatedAt();
        this.userId = forumItem.getUserId();
        this.rate = forumItem.getRate();
        this.user = forumItem.getUser();
        this.comments = comments;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.single_forum_fragment, container, false);
        server = Server.getInstance();

        Button profileButton = view.findViewById(R.id.profileButton);
        final Button commentButton = view.findViewById(R.id.commentButton);

        TextView username = view.findViewById(R.id.textViewUsernameForum);
        TextView title = view.findViewById(R.id.textViewTitleForum);
        TextView createdAt = view.findViewById(R.id.textViewCreatedAtForum);
        TextView description = view.findViewById(R.id.textViewDescriptionForum);
        
        username.setText(this.user.getUsername());
        title.setText(this.title);
        createdAt.setText(this.createdAt);
        description.setText(this.description);

        showComments(view);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfoUser(user.getUsername());
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextInputLayout textInputLayout = view.findViewById(R.id.textInputLayoutComment);
                if (Objects.equals(commentButton.getText().toString(), getString(R.string.commentButton_forum))) {
                    textInputLayout.setVisibility(View.VISIBLE);
                    final ScrollView scrollView = view.findViewById(R.id.scrollViewComment);
                    commentButton.setText(getString(R.string.postCommentButton_forum));

                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
                else {
                    textInputLayout.setVisibility(View.GONE);
                    commentButton.setText(getString(R.string.commentButton_forum));
                }
            }
        });

        return view;
    }

    private void showComments(View view) {
        LinearLayout commentLayout = view.findViewById(R.id.layoutComments);
        commentLayout.removeAllViews();

        for (int i = 0; i < comments.size(); ++i) {

            LinearLayout horizontalLayout = new LinearLayout(getActivity());
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0, 0, 20);
            horizontalLayout.setLayoutParams(layoutParams);

            LinearLayout commentItemVertical = new LinearLayout(getActivity());
            commentItemVertical.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(575, LinearLayout.LayoutParams.WRAP_CONTENT);
            commentItemVertical.setLayoutParams(layoutParams2);

            TextView commentUsername = new TextView(getActivity());
            commentUsername.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            commentUsername.setText(comments.get(i).getUsername());
            commentUsername.setTextSize(15);
            commentUsername.setTypeface(commentUsername.getTypeface(), Typeface.BOLD);
            commentItemVertical.addView(commentUsername);

            TextView commentDate = new TextView(getActivity());
            commentDate.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            commentDate.setText(comments.get(i).getCreatedAt());
            commentDate.setTextSize(12);
            commentDate.setTextColor(Color.BLUE);
            commentItemVertical.addView(commentDate);

            TextView commentContent = new TextView(getActivity());
            commentContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            commentContent.setText(comments.get(i).getContent());
            commentContent.setTextSize(15);
            commentItemVertical.addView(commentContent);


            horizontalLayout.addView(commentItemVertical);

            SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
            String userPref = preferences.getString("username", "username");

            /*Solo mostramos la opcion de eliminar un comentario si:
            *   - El usuario logueado es el mismo usuario que ha comentado*/
            if (Objects.equals(userPref, comments.get(i).getUsername())) {
                ImageButton imageButton = new ImageButton(getActivity());
                imageButton.setBackground(getResources().getDrawable(R.drawable.baseline_remove_circle_black_18));
                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams3.setMargins(0,20, 0, 0);
                imageButton.setLayoutParams(layoutParams3);

                horizontalLayout.addView(imageButton);
            }
            commentLayout.addView(horizontalLayout);
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
