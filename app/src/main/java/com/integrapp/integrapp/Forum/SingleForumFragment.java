package com.integrapp.integrapp.Forum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.integrapp.integrapp.Model.DataComment;
import com.integrapp.integrapp.Profile.ProfileFragment;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;
import com.integrapp.integrapp.Model.ForumItem;
import com.integrapp.integrapp.Model.UserDataAdvertiser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class SingleForumFragment extends Fragment {

    private String idForum;
    private String type;
    private String title;
    private String description;
    private String createdAt;
    private String userId;
    private float rate;
    private UserDataAdvertiser user;
    private Server server;
    private List<DataComment> comments;

    private TextView commentTextView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextInputLayout textInputLayout;
    private Button commentButton;
    private Button saveForumButton;
    private View viewFinishComments;
    private View titleView;
    private View descriptionView;

    private RatingBar rate_forum;

    public SingleForumFragment () {}

    @SuppressLint("ValidFragment")
    public SingleForumFragment(ForumItem forumItem, List<DataComment> comments) {
        this.idForum = forumItem.getId();
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
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.single_forum_fragment, container, false);
        server = Server.getInstance();

        Button profileButton = view.findViewById(R.id.profileButton);
        commentButton = view.findViewById(R.id.commentButton);
        saveForumButton = view.findViewById(R.id.saveForumButton);
        
        TextView username = view.findViewById(R.id.textViewUsernameForum);
        titleTextView = view.findViewById(R.id.textViewTitleForum);
        TextView createdAt = view.findViewById(R.id.textViewCreatedAtForum);
        descriptionTextView = view.findViewById(R.id.textViewDescriptionForum);
        
        titleView = view.findViewById(R.id.viewTitleForum);
        descriptionView = view.findViewById(R.id.viewDescriptionForum);

        textInputLayout = view.findViewById(R.id.textInputLayoutComment);
        
        username.setText(this.user.getUsername());
        titleTextView.setText(this.title);
        createdAt.setText(this.createdAt);
        descriptionTextView.setText(this.description);

        rate_forum = view.findViewById(R.id.rate_forum);
        rate_forum.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                System.out.println("rate1: " + Float.toString(rate_forum.getRating()));
                vote(rate_forum.getRating(), idForum);

            }
        });

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

                if (Objects.equals(commentButton.getText().toString(), getString(R.string.commentButton_forum))) {
                    textInputLayout.setVisibility(View.VISIBLE);
                    commentButton.setText(getString(R.string.postCommentButton_forum));
                    scrollView(view, ScrollView.FOCUS_DOWN);
                }
                else {
                    commentTextView = view.findViewById(R.id.contentCommentEditText);
                    String comment = commentTextView.getText().toString();
                    if (checkContentCommentField(comment)) {
                        createComment(comment);
                    }
                }
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void vote(final float rating, final String idForum) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return server.voteForum(idForum, generateRequestVote(rating));
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR VOTING FORUM")) {
                    Toast.makeText(getContext(), getString(R.string.toast_thanksForYourVote), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.toast_ForumCouldNotBeVoted), Toast.LENGTH_SHORT).show();
                }
            }

        }.execute();
    }

    private void scrollView(View view, final int focus) {
        final ScrollView scrollView = view.findViewById(R.id.scrollViewComment);

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(focus);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void createComment(String comment) {
        final String json = generateRequestCreateComment(comment);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.createCommentForum(json);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN COMMENTING FORUM")) {
                    showNewComment(s);
                    /*Cerrar teclado*/
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(commentTextView.getWindowToken(), 0);
                    Toast.makeText(getContext(), getString(R.string.toast_CommentCreatedCorrectly), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.error_PostingComment), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void showNewComment(String s) {
        try {
            JSONObject myJsonObject = new JSONObject(s);
            String idComment = myJsonObject.getString("_id");
            String userId = myJsonObject.getString("userId");
            String username = myJsonObject.getString("username");
            String createdAt = myJsonObject.getString("createdAt");
            String content = myJsonObject.getString("content");
            String forumId = myJsonObject.getString("forumId");

            DataComment newComment = new DataComment(idComment, userId, username, createdAt, content, forumId);
            comments.add(0, newComment);

            commentButton.setText(getString(R.string.commentButton_forum));
            textInputLayout.setVisibility(View.GONE);
            commentTextView.setText("");

            showComments(getView());
            scrollView(getView(), ScrollView.FOCUS_UP);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String generateRequestCreateComment(String comment) {

        try {
            JSONObject oJSON = new JSONObject();
            oJSON.put("forumId", this.idForum);
            oJSON.put("content", comment);
            return oJSON.toString(1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateRequestVote(Float rating) {
        int r = Math.round(rating);
        try {
            JSONObject oJSON = new JSONObject();
            oJSON.put("rate", r);
            return oJSON.toString(1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean checkContentCommentField(String comment) {
        boolean valid = true;

        if (comment.isEmpty()) {
            commentTextView.setError(getString(R.string.error_changePassField_empty));
            valid = false;
        }

        return valid;
    }

    private void showComments(View view) {
        LinearLayout commentLayout = view.findViewById(R.id.layoutComments);
        commentLayout.removeAllViews();

        viewFinishComments = view.findViewById(R.id.viewFinishComments);
        if (comments.isEmpty()) viewFinishComments.setVisibility(View.GONE);
        else viewFinishComments.setVisibility(View.VISIBLE);

        for (int i = 0; i < comments.size(); ++i) {

            final int finalI = i;

            LinearLayout horizontalLayout = new LinearLayout(getActivity());
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0, 0, 20);
            horizontalLayout.setLayoutParams(layoutParams);

            LinearLayout commentItemVertical = new LinearLayout(getActivity());
            commentItemVertical.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1000);
            commentItemVertical.setLayoutParams(layoutParams2);

            TextView commentUsername = new TextView(getActivity());
            commentUsername.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            commentUsername.setText(comments.get(i).getUsername());
            commentUsername.setTextSize(15);
            commentUsername.setTypeface(commentUsername.getTypeface(), Typeface.BOLD);
            
            commentUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getInfoUserComment(comments.get(finalI).getUsername());
                }
            });
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
                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                layoutParams3.setMargins(0,20, 0, 0);
                imageButton.setLayoutParams(layoutParams3);

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        builder.setMessage(R.string.dialog_delete_comment).setTitle(R.string.tittle_dialogDeleteComment);
                        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCommentById(comments.get(finalI).getId(), finalI);
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
                });

                horizontalLayout.addView(imageButton);
            }
            commentLayout.addView(horizontalLayout);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getInfoUserComment(final String username) {
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
                    sendInfoUserCommentToProfile(s);
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_GettingUserInfo), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void sendInfoUserCommentToProfile(String s) {

        try {
            //para saber que viene de aqui y no del perfil del navigation
            Fragment fragment = new ProfileFragment("advertiserUser");
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            JSONObject myJsonObject = new JSONObject(s);
            String userId = myJsonObject.getString("_id");
            String username = myJsonObject.getString("username");
            String type = myJsonObject.getString("type");
            String name = myJsonObject.getString("name");
            String email = "No e-mail";
            String phone = "No phone";
            if (myJsonObject.has("email")) {
                email = myJsonObject.getString("email");
            }
            if (myJsonObject.has("phone")) {
                phone = myJsonObject.getString("phone");
            }

            String rate = myJsonObject.getString("rate");
            JSONObject myJsonRate = new JSONObject(rate);
            int likes = myJsonRate.getInt("likes");
            int dislikes = myJsonRate.getInt("dislikes");
            int ads = myJsonObject.getJSONArray("adverts").length();

            Bundle args = new Bundle();
            args.putString("idUser", userId);
            args.putString("username", username);
            args.putString("name", name);
            args.putString("type", type);
            args.putString("email", email);
            args.putString("phone", phone);
            args.putInt("likes", likes);
            args.putInt("dislikes", dislikes);
            args.putInt("ads", ads);

            fragment.setArguments(args);

            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.screen_area, fragment);
            ft.addToBackStack(null);
            ft.commit();

        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void deleteCommentById(final String id, final int index) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.deleteCommentById(id);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN DELETING COMMENT")) {
                    comments.remove(index);
                    showComments(getView());
                    Toast.makeText(getContext(), getString(R.string.toast_CommentDeletedSuccessfully), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_DeletingComment), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.forum, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        String usernamePreferences = preferences.getString("username", "username");
        if (Objects.equals(user.getUsername(), usernamePreferences)) {
            menu.findItem(R.id.action_reportForum).setVisible(false);
            menu.findItem(R.id.action_editForum).setVisible(true);
        }
        else {
            menu.findItem(R.id.action_reportForum).setVisible(true);
            menu.findItem(R.id.action_editForum).setVisible(false);
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
        if (id == R.id.action_reportForum) {
            final EditText editText = new EditText(getContext());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.dialog_report).setTitle(R.string.title_dialogReportForum);
            builder.setView(editText);
            builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reportForum(editText.getText().toString());
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
        else if (id == R.id.action_editForum) {
            setVisibility(true, View.VISIBLE);
            commentButton.setVisibility(View.GONE);
            textInputLayout.setVisibility(View.GONE);
            commentButton.setText(getString(R.string.commentButton_forum));
            
            saveForumButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newTitle = titleTextView.getText().toString();
                    String newDescription = descriptionTextView.getText().toString();

                    if (fieldsSaveForumOk(newTitle, newDescription)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        builder.setMessage(R.string.dialog_save).setTitle(R.string.tittle_dialogSave);
                        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveChanges();
                            }
                        });

                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setVisibility(false, View.INVISIBLE);
                                commentButton.setVisibility(View.VISIBLE);
                                undoChanges(title,description);
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void undoChanges(String title, String description) {
        titleTextView.setText(title);
        descriptionTextView.setText(description);
    }

    @SuppressLint("StaticFieldLeak")
    private void saveChanges() {
        final String json = generateRequestModifyForum();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.modifyForumById(idForum, json);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR MODIFY FORUM")) {
                    setVisibility(false, View.INVISIBLE);
                    commentButton.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), getString(R.string.toast_ChangesSaveCorrectly), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), R.string.error_ModifyingForum, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private String generateRequestModifyForum() {
        String title = titleTextView.getText().toString();
        String description = descriptionTextView.getText().toString();

        try {
            JSONObject oJSON = new JSONObject();
            oJSON.put("title", title);
            oJSON.put("description", description);

            return oJSON.toString(1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean fieldsSaveForumOk(String newTitle, String newDescription) {
        boolean valid = true;

        if (newTitle.isEmpty()) {
            titleTextView.setError(getString(R.string.error_changePassField_empty));
            valid = false;
        }

        if (newDescription.isEmpty()) {
            descriptionTextView.setError(getString(R.string.error_changePassField_empty));
            valid = false;
        }

        return valid;
    }

    private void setVisibility(boolean b, int visibility) {
        titleTextView.setEnabled(b);
        descriptionTextView.setEnabled(b);

        titleView.setVisibility(visibility);
        descriptionView.setVisibility(visibility);

        saveForumButton.setVisibility(visibility);
    }

    @SuppressLint("StaticFieldLeak")
    private void reportForum(String reason) {
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
                        Toast.makeText(getActivity(), getString(R.string.forumReport_ok), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error_reportForum), Toast.LENGTH_SHORT).show();
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
        oJSON.put("type", "forum");
        oJSON.put("typeId", idForum);

        return oJSON.toString(1);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar= getActivity().findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(R.string.menu_forum);
        }
    }

}
