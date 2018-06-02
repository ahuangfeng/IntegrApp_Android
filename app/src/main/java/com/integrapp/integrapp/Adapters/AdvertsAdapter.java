package com.integrapp.integrapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Model.Advert;

import java.util.List;
import java.util.Objects;

public class AdvertsAdapter extends BaseAdapter {

    private Context context;
    private List<Advert> objectList;

    public AdvertsAdapter(Context context, List<Advert> objectList) {
        this.context = context;
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
    public long getItemId(int i) {
        return objectList.get(i).getImage();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vista;
        LayoutInflater inflate = LayoutInflater.from(context);
        vista = inflate.inflate(R.layout.activity_item_advert, null);

        ImageView imageView = vista.findViewById(R.id.imageViewAd);
        TextView date = vista.findViewById(R.id.textViewDate);
        TextView title = vista.findViewById(R.id.textViewTitle);
        TextView description = vista.findViewById(R.id.textViewDescription);
        TextView places = vista.findViewById(R.id.textViewPlaces);
        TextView type = vista.findViewById(R.id.textViewType);
        TextView state = vista.findViewById(R.id.textViewState);

        title.setText(objectList.get(i).getTitle());
        date.setText(objectList.get(i).getDate());
        description.setText(objectList.get(i).getDescription());
        places.setText(objectList.get(i).getPlaces());
        if (Objects.equals(objectList.get(i).getType(), "lookFor")) type.setText(R.string.lookfor_itemAdd);
        else if (Objects.equals(objectList.get(i).getType(), "offer")) type.setText(R.string.offer_itemAdd);

        if (Objects.equals(objectList.get(i).getState(), "opened")) state.setText(R.string.state_opened);
        else if (Objects.equals(objectList.get(i).getState(), "closed")) state.setText(R.string.state_closed);
        imageView.setImageResource(objectList.get(i).getImage());

        return vista;
    }
}
