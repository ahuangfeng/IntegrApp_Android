package com.integrapp.integrapp.Profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.integrapp.integrapp.Adverts.AdvertsFragment;
import com.integrapp.integrapp.Login.LogIn;
import com.integrapp.integrapp.Model.UserDataAdvertiser;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import static android.app.Activity.RESULT_OK;

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
    private TextView likesTextView;
    private TextView dislikesTextView;
    private TextView adsTextView;
    private String idUser;
    private Server server;


    private SwipeRefreshLayout mSwipeRefreshLayout;

    String mCurrentPhotoPath;
    private ImageView imageView;

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;


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

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

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

        likesTextView = view.findViewById(R.id.likesTextView);
        dislikesTextView = view.findViewById(R.id.dislikesTextView);
        adsTextView = view.findViewById(R.id.adsTextView);

        imageView = view.findViewById(R.id.profile_image);

        TextView profileAds = view.findViewById(R.id.profile_adds);
        profileAds.setText(getString(R.string.adds_profile));

        if (Objects.equals(typeProfile, "advertiserUser")) {
            idUser = getArguments() != null ? getArguments().getString("idUser") : "idUser";
            String username = getArguments() != null ? getArguments().getString("username") : "username";
            String type = getArguments() != null ? getArguments().getString("type") : "type";
            String name = getArguments() != null ? getArguments().getString("name") : "name";
            String email = getArguments() != null ? getArguments().getString("email") : "email";
            String phone = getArguments() != null ? getArguments().getString("phone") : "phone";
            int likes = getArguments() != null ? getArguments().getInt("likes") : 0;
            int dislikes = getArguments() != null ? getArguments().getInt("dislikes") : 0;
            int ads = getArguments() != null ? getArguments().getInt("ads") : 0;
            String path = getArguments() != null ? getArguments().getString("imagePath") : "imagePath";

            if (!path.equals("")) Picasso.with(getContext()).load(path).into(imageView);
            else imageView.setImageResource(R.drawable.project_preview_large_2);

            setAttributes(name, username, type, email, phone);
            setRateAndAds(likes,dislikes,ads);

            SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
            String usernamePreferences = preferences.getString("username", "username");

            LinearLayout adsLayout = view.findViewById(R.id.adsLayout);
            adsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUserAdverts();
                }
            });

            if(!Objects.equals(usernamePreferences, username)) {

                LinearLayout likesLayout = view.findViewById(R.id.likesLayout);
                LinearLayout dislikesLayout = view.findViewById(R.id.dislikesLayout);

                likesLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        voteLike();
                    }
                });
                dislikesLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        voteDislike();
                    }
                });
            } else {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dispatchTakePictureIntent();
                    }
                });
            }

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //hacer llamada likes, dislikes
                    getLikesDislikes();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
        else {
            SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
            final String username = preferences.getString("username", "username");
            idUser = preferences.getString("idUser", "idUser");
            String name = preferences.getString("name", "name");
            String type = preferences.getString("type", "type");
            String email = preferences.getString("email", "email");
            String phone = preferences.getString("phone", "phone");
            int likes = preferences.getInt("likes",0);
            int dislikes = preferences.getInt("dislikes",0);
            int ads = preferences.getInt("ads", 0);

            imageView.setImageResource(R.drawable.project_preview_large_2);
            setImagePhoto();

            setAttributes(name, username, type, email, phone);
            setRateAndAds(likes, dislikes, ads);

            LinearLayout adsLayout = view.findViewById(R.id.adsLayout);
            adsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUserAdverts();
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dispatchTakePictureIntent();
                }
            });

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getLikesDislikes();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

        }

      return view;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void showUserAdverts() {
        Toast.makeText(getActivity(), getString(R.string.toast_ShowAdvertsOfUser), Toast.LENGTH_SHORT).show();
        Fragment fragment = new AdvertsFragment(idUser);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.screen_area, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @SuppressLint("StaticFieldLeak")
    private void voteDislike() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.voteDislikeUser(idUser);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN DISLIKE VOTE")) {
                    saveVote(s, "dislike");
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_VotingDislike), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void voteLike() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.voteLikeUser(idUser);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN LIKE VOTE")) {
                    saveVote(s, "like");
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_VotingLike), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void saveVote(String s, String typeOfVote) {
        try {
            JSONObject myJsonObject = new JSONObject(s);
            String rate = myJsonObject.getString("rate");
            JSONObject myJsonRate = new JSONObject(rate);
            int likes = myJsonRate.getInt("likes");
            int dislikes = myJsonRate.getInt("dislikes");

            //Para que el usuario sepa que ya ha votado en like o en dislike
            if (Objects.equals(typeOfVote, "like")) {
                if (Integer.parseInt(likesTextView.getText().toString()) == likes) {
                    Toast.makeText(getActivity(), getString(R.string.toast_AlreadyLike), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.toast_VotedLike), Toast.LENGTH_SHORT).show();
                }
            }
            else if (Objects.equals(typeOfVote, "dislike")) {
                if (Integer.parseInt(dislikesTextView.getText().toString()) == dislikes) {
                    Toast.makeText(getActivity(), getString(R.string.toast_AlreadyDislike), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.toast_VotedDislike), Toast.LENGTH_SHORT).show();
                }
            }

            String likesString = Integer.toString(likes);
            String dislikesString = Integer.toString(dislikes);
            likesTextView.setText(likesString);
            dislikesTextView.setText(dislikesString);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setRateAndAds(int likes, int dislikes, int ads) {
        String likesString = Integer.toString(likes);
        String dislikesString = Integer.toString(dislikes);
        String adsString = Integer.toString(ads);
        likesTextView.setText(likesString);
        dislikesTextView.setText(dislikesString);
        adsTextView.setText(adsString);
    }

    @SuppressLint("StaticFieldLeak")
    private void setImagePhoto() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.getImageUser(idUser);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING IMAGE")) {
                    System.out.println("laimagenobtenida: " + s);
                    Picasso.with(getContext()).load(s).into(imageView);
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void deleteUserById() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.deleteUserById(idUser);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN DELETING USER")) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_UserDeletedSuccessfully), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ProfileFragment.this.getActivity(), LogIn.class);
                    startActivity(i);
                    getActivity().finish();

                    //Ponemos islogged a false para que no entre a la pantalla de anuncios, si no que se quede en el login
                    SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isLogged", false);
                    editor.apply();
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_DeletingUser), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void deleteImageUserById() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.deleteImageUserById(idUser);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN DELETING IMAGE USER")) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_UserImageDeletedSuccessfully), Toast.LENGTH_SHORT).show();
                    imageView.setImageResource(R.drawable.project_preview_large_2);
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_DeletingUser), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void setAttributes(String name, String username, String type, String email, String phone) {
        nameTextView.setText(name);
        usernameTextView.setText(username);
        if (Objects.equals(type, "voluntary")) {
            typeUserTextView.setText(R.string.userTypeVoluntary_Profile);
        }else if (Objects.equals(type, "newComer")) {
            typeUserTextView.setText(R.string.userTypeNewComer_Profile);
        }else {
            typeUserTextView.setText(R.string.userTypeAssociation_Profile);
        }

        if (Objects.equals(email, "No-email")) {
            emailTextView.setText(getString(R.string.No_email));
        }else {
            emailTextView.setText(email);
        }

        if (Objects.equals(phone, "No phone")) {
            phoneTextView.setText(getString(R.string.No_phone));
        }else {
            phoneTextView.setText(phone);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.profile, menu);
        /*Solo se muestran las opciones de delte y edit cuando se consulta el perfil del usuario
        logueado pero no si se está consultando un perfil de un anunciante */
        if (Objects.equals(typeProfile, "advertiserUser")) {
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_changePass).setVisible(false);
        } else {
            menu.findItem(R.id.action_reportUser).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        server.token = preferences.getString("user_token", "user_token");

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.dialog_delete_user).setTitle(R.string.tittle_dialogDeleteUser);
            builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteUserById();
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

            saveProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = nameTextView.getText().toString();
                    String username = usernameTextView.getText().toString();
                    if (fieldsSaveProfileOk(name, username)) {

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
                                undoChanges();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
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
            Button changePasswordButton = dialogView.findViewById(R.id.changePassButton);

            changePasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String currentPass = currentPassEditText.getText().toString();
                    String newPass = newPassEditText.getText().toString();
                    String confirmPass = confirmNewPassEditText.getText().toString();

                    if (fieldsChangePassOK(currentPass, newPass, confirmPass)) {
                        if (passwordsOk(currentPass, newPass, confirmPass)) {
                            saveChangePassword(newPass, dialog);
                        }
                    }
                }
            });
        } else if (id == R.id.action_reportUser) {
            final EditText editText = new EditText(getContext());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.dialog_report).setTitle(R.string.title_dialogReportUser);
            builder.setView(editText);
            builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reportUser(editText.getText().toString());
                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (id == R.id.action_deleteImageUser) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.dialog_delete_image_user).setTitle(R.string.tittle_dialogDeleteImageUser);
            builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteImageUserById();
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

    private boolean fieldsSaveProfileOk(String name, String username) {
        boolean valid = true;

        if (name.isEmpty()) {
            nameTextView.setError(getString(R.string.error_changePassField_empty));
            valid = false;
        }

        if (username.isEmpty()) {
            usernameTextView.setError(getString(R.string.error_changePassField_empty));
            valid = false;
        }

        return valid;
    }

    @SuppressLint("StaticFieldLeak")
    private void saveChangePassword(String newPass, final AlertDialog dialog) {
        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password", newPass);
        editor.apply();

        final String json = generateRequestModifyProfile();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.modifyProfileById(idUser, json);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR MODIFY PROFILE")) {
                    Toast.makeText(getActivity(), getString(R.string.toast_PasswordChagedSuccessfully), Toast.LENGTH_SHORT).show();
                    /*Cerrar teclado y dialogo*/
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(confirmNewPassEditText.getWindowToken(), 0);
                    dialog.cancel();
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_ChangingPassword), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private boolean passwordsOk(String currentPass, String newPass, String confirmPass) {
        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        server.token = preferences.getString("user_token", "user_token");
        String password = preferences.getString("password", "password");

        if (!Objects.equals(newPass, confirmPass)) {
            Toast.makeText(getActivity(), getString(R.string.toast_PasswordsDoNotMatch), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!Objects.equals(currentPass, password)) {
            Toast.makeText(getActivity(), getString(R.string.toast_InvalidCurrentPassword), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean fieldsChangePassOK(String currentPass, String newPass, String confirmPass) {
        boolean valid = true;

        if (currentPass.isEmpty()) {
            currentPassEditText.setError(getString(R.string.error_changePassField_empty));
            valid = false;
        }

        if (newPass.isEmpty()) {
            newPassEditText.setError(getString(R.string.error_changePassField_empty));
            valid = false;
        }else if (checkInputText(newPass)) {
            newPassEditText.setError(getString(R.string.error_password_length));
            valid = false;
        }

        if (confirmPass.isEmpty()) {
            confirmNewPassEditText.setError(getString(R.string.error_changePassField_empty));
            valid = false;
        }else if (checkInputText(confirmPass)){
            confirmNewPassEditText.setError(getString(R.string.error_password_length));
            valid = false;
        }
        return valid;
    }

    private boolean checkInputText(String text) {
        return text.isEmpty() || text.length() < 6 || text.length() > 128;
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

    @SuppressLint("StaticFieldLeak")
    private void saveChanges() {
        final String json = generateRequestModifyProfile();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.modifyProfileById(idUser, json);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR MODIFY PROFILE")) {
                    setVisibility(false, View.INVISIBLE);
                    setNewPreferences();
                    Toast.makeText(getContext(), getString(R.string.toast_ChangesSaveCorrectly), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_ModifyingProfile), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void setNewPreferences() {
        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String username = usernameTextView.getText().toString();
        String name = nameTextView.getText().toString();
        String email = emailTextView.getText().toString();
        String phone = phoneTextView.getText().toString();
        String type = typeUserTextView.getText().toString();

        /*Si el usuario quiere hacer el email o el phone no visible*/
        if (email.isEmpty()) {
            email = getString(R.string.No_email);
            emailTextView.setText(email);
        }
        if (phone.isEmpty()) {
            phone = getString(R.string.No_phone);
            phoneTextView.setText(phone);
        }

        editor.putString("username", username);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("phone", phone);

        if (Objects.equals(type, getString(R.string.userTypeVoluntary_Profile))) type = "voluntary";
        else if (Objects.equals(type, getString(R.string.userTypeAssociation_Profile))) type = "association";
        else type = "newComer";

        editor.putString("type", type);

        editor.apply();

        setHeadersNavigation();
    }

    private void setHeadersNavigation() {
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        TextView headerName = header.findViewById(R.id.headerName);
        TextView headerEmail = header.findViewById(R.id.headerEmail);

        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        String name = preferences.getString("name", "name");
        headerName.setText(name);
        String email = preferences.getString("email", "email");
        headerEmail.setText(email);
    }

    private String generateRequestModifyProfile() {
        String username = usernameTextView.getText().toString();
        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        String password = preferences.getString("password", "password");
        String name = nameTextView.getText().toString();
        String email = emailTextView.getText().toString();
        String phone = phoneTextView.getText().toString();
        String type = typeUserTextView.getText().toString();

        try {
            JSONObject oJSON = new JSONObject();
            oJSON.put("username", username);
            oJSON.put("password", password);
            oJSON.put("name", name);
            if (!email.isEmpty() && !Objects.equals(email, getString(R.string.No_email))) oJSON.put("email", email);
            if (!phone.isEmpty() && !Objects.equals(phone, getString(R.string.No_phone))) oJSON.put("phone", phone);

            if (Objects.equals(type, getString(R.string.userTypeVoluntary_Profile))) type = "voluntary";
            else if (Objects.equals(type, getString(R.string.userTypeAssociation_Profile))) type = "association";
            else type = "newComer";

            oJSON.put("type", type);

            return oJSON.toString(1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    private void reportUser(String reason) {
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
                        Toast.makeText(getActivity(), getString(R.string.userReport_ok), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error_reportUser), Toast.LENGTH_SHORT).show();
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
        oJSON.put("type", "user");
        oJSON.put("typeId", idUser);

        return oJSON.toString(1);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar= getActivity().findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(R.string.menu_profile);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            addPhoto2(imageBitmap);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void addPhoto2(final Bitmap bitmap) {
        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        File fPath = Environment.getExternalStorageDirectory();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File f2 = new File(fPath, timeStamp);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(f2);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
            stream.close();
            addPhoto3(bitmap, f2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void addPhoto3(final Bitmap bitmap, final File f) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.addPhotoUser(f);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR UPDATING PHOTO")) {
                    imageView.setImageBitmap(bitmap);
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_UpdatingPhoto), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getLikesDislikes() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                String username = usernameTextView.getText().toString();
                return server.getUserInfoByUsername(username);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GET INFO USER")) {
                    saveLikesDislikes(s);
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_GettingUserInfo), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void saveLikesDislikes(String s) {
        try {
            JSONObject myJsonObject = new JSONObject(s);
            String rate = myJsonObject.getString("rate");
            JSONObject myJsonRate = new JSONObject(rate);
            int likes = myJsonRate.getInt("likes");
            int dislikes = myJsonRate.getInt("dislikes");
            String username = myJsonObject.getString("username");
            JSONArray jsonArray = myJsonObject.getJSONArray("adverts");
            int ads = jsonArray.length();
            String path = "";
            if (myJsonObject.has("imagePath")) {
                path = myJsonObject.getString("imagePath");
            }

            SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
            if (preferences.getString("username", "username").equals(username)) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("likes", likes);
                editor.putInt("dislikes", dislikes);
                editor.apply();
            }

            if (!path.equals("")) Picasso.with(getContext()).load(path).into(imageView);
            else
            setRateAndAds(likes, dislikes, ads);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
