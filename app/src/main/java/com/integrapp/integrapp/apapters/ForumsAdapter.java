package com.integrapp.integrapp.apapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.integrapp.integrapp.R;
import com.integrapp.integrapp.model.ForumItem;

import java.util.ArrayList;


public class ForumsAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<ForumItem> threads = new ArrayList<>();

    public ForumsAdapter (Context context, ArrayList<ForumItem> threads) {
        this.context = context;
        this.threads = threads;
    }

    public ForumsAdapter (Context context) {
        this.context = context;
    }

    public void setThreads(ArrayList<ForumItem> threads) {
        this.threads = threads;
    }

    public void addThread(ForumItem thread) {
        this.threads.add(thread);
    }

    @Override
    public int getCount() {
        return threads.size();
    }

    @Override
    public ForumItem getItem(int position) {
        return threads.get(position);
    }

    @Override
    public long getItemId(int position) {
        Long l = 1L;
        return l;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vista;
        LayoutInflater inflate = LayoutInflater.from(context);
        vista = inflate.inflate(R.layout.forum_item, null);

        TextView title = vista.findViewById(R.id.thread_title);
        TextView description = vista.findViewById(R.id.thread_info);
        TextView createdAt = vista.findViewById(R.id.thread_createdAt);
        RatingBar rate = vista.findViewById(R.id.thread_rating);

        title.setText(threads.get(position).getTitle());

        if (threads.get(position).getMiniDescription().length()>0) {
            description.setText(threads.get(position).getMiniDescription());
        }
        else {
            description.setText(threads.get(position).getDescription());
        }
        createdAt.setText(threads.get(position).getCreatedAt());
        rate.setRating(threads.get(position).getRate());
        return vista;
    }
}