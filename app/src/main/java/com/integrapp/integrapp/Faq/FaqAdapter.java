package com.integrapp.integrapp.Faq;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.integrapp.integrapp.R;
import java.util.ArrayList;

public class FaqAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SingleFaqItem> faqs = new ArrayList<>();

    public FaqAdapter (Context context, ArrayList<SingleFaqItem> faqs) {
        this.context = context;
        this.faqs = faqs;
    }

    public FaqAdapter (Context context) {
        this.context = context;
    }

    public void setFaqs(ArrayList<SingleFaqItem> faqs) {
        this.faqs = faqs;
    }

    public void addfaq(SingleFaqItem faq) {
        this.faqs.add(faq);
    }

    @Override
    public int getCount() {
        return faqs.size();
    }

    @Override
    public SingleFaqItem getItem(int position) {
        return faqs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 1L;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vista;
        LayoutInflater inflate = LayoutInflater.from(context);
        vista = inflate.inflate(R.layout.faq_item_list, null);

        ImageView image = vista.findViewById(R.id.faq_list_image);
        TextView title = vista.findViewById(R.id.faq_list_title);

        title.setText(faqs.get(position).getTitle());
        Drawable photo = null;

        if (position == 0) {
            photo = ContextCompat.getDrawable(context, R.drawable.faqcasa);
        }
        else if (position == 1) {
            photo = ContextCompat.getDrawable(context, R.drawable.faqempresa);
        }
        else if (position == 2) {
            photo = ContextCompat.getDrawable(context, R.drawable.faqidiomes);
        }

        image.setImageDrawable(photo);

//        if (threads.get(position).getMiniDescription().length()>0) {
//            description.setText(threads.get(position).getMiniDescription());
//        }
//        else {
//            description.setText(threads.get(position).getDescription());
//        }

        return vista;
    }
}
