package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        final View view = inflater.inflate(R.layout.single_forum_fragment, container, false);

        Button profileButton = view.findViewById(R.id.profileButton);
        Button commentButton = view.findViewById(R.id.commentButton);

        TextView title = view.findViewById(R.id.textViewTitleForum);
        TextView createdAt = view.findViewById(R.id.textViewCreatedAtForum);
        TextView description = view.findViewById(R.id.textViewDescriptionForum);

        title.setText(this.title);
        createdAt.setText(this.createdAt);
        description.setText(this.description);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Boton perfil clickado", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
