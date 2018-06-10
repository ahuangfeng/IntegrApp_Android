package com.integrapp.integrapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.integrapp.integrapp.Model.Chat;
import com.integrapp.integrapp.R;
import java.util.ArrayList;

public class ChatsAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<Chat> chats = new ArrayList<>();

    public ChatsAdapter (Context context, ArrayList<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    public void addChat(Chat chat) {
        this.chats.add(chat);
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Chat getItem(int position) {
        return chats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 1L;
    }

    public String getUserIdOfChat(int position) {
        return chats.get(position).getChatToId();
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vista;
        LayoutInflater inflate = LayoutInflater.from(context);
        vista = inflate.inflate(R.layout.chat_item, null);

        TextView name = vista.findViewById(R.id.talking_to_name);
        TextView username = vista.findViewById(R.id.talking_to_username);
        //TextView type = vista.findViewById(R.id.talking_to_type);
        //ImageView image = vista.findViewById(R.id.user_image);

        name.setText(chats.get(position).getToName());
        System.out.println(name.getText().toString());
        username.setText(chats.get(position).getToUsername());
        System.out.println(username.getText().toString());
        return vista;
    }
}