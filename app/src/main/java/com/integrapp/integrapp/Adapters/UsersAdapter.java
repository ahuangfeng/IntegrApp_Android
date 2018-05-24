package com.integrapp.integrapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.integrapp.integrapp.R;
import com.integrapp.integrapp.model.ForumItem;
import com.integrapp.integrapp.model.User;

import java.util.ArrayList;


public class UsersAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<User> users = new ArrayList<>();

    public UsersAdapter (Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    public void setUsers(ArrayList<ForumItem> threads) {
        this.users = users;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 1L;
    }

    public String getUserId(int position) {
        return users.get(position).getId();
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vista;
        LayoutInflater inflate = LayoutInflater.from(context);
        vista = inflate.inflate(R.layout.user_item, null);

        TextView name = vista.findViewById(R.id.user_name);
        TextView username = vista.findViewById(R.id.user_username);
        TextView type = vista.findViewById(R.id.user_type);
        //ImageView image = vista.findViewById(R.id.user_image);

        name.setText(users.get(position).getName());
        username.setText(users.get(position).getUsername());
        type.setText(users.get(position).getType());

        return vista;
    }
}
