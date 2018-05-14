package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SingleForumFragment extends Fragment {

    private String id;
    private String type;
    private String title;
    private String description;
    private String createdAt;
    private String userId;
    private float rate;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_forum_fragment, container, false);

        TextView textView = view.findViewById(R.id.textViewProba);
        textView.setText("Parametros pasados: "+id+" "+type+" "+title+" "+description+" "+createdAt+" "+userId+" "+rate);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
