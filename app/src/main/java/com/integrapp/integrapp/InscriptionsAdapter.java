package com.integrapp.integrapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class InscriptionsAdapter extends BaseAdapter {

    Context contexto;
    List<DataInscription> objectList;

    public InscriptionsAdapter(Context contexto, List<DataInscription> objectList) {
        this.contexto = contexto;
        this.objectList = objectList;
    }

    @Override
    public int getCount() {
        return objectList.size();
    }

    @Override
    public Object getItem(int i) {
        return objectList.get(i);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(objectList.get(position).getId());
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vista;
        LayoutInflater inflate = LayoutInflater.from(contexto);
        vista = inflate.inflate(R.layout.activity_item_inscription, null);

        TextView username = vista.findViewById(R.id.textViewUsername);
        TextView advertTitle = vista.findViewById(R.id.textViewAdvertTitle);
        TextView state = vista.findViewById(R.id.textViewState);

        username.setText(objectList.get(i).getUsernameOwner());
        advertTitle.setText(objectList.get(i).getAdvertTitle());
        state.setText(objectList.get(i).getState());

        return vista;
    }
}

