package com.integrapp.integrapp.Faq;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.integrapp.integrapp.Model.Faq;
import com.integrapp.integrapp.R;

public class SingleFaqFragment extends Fragment {

    private Faq faq;

    public SingleFaqFragment() {
    }

    public void setFaq(Faq faq) {
        this.faq = faq;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_faq_fragment, container, false);
        TextView textViewTitle = view.findViewById(R.id.singleFaqTitle);
        TextView textViewContent = view.findViewById(R.id.singleFaqContent);
        ImageView imageView = view.findViewById(R.id.singleFaqImage);
        textViewTitle.setText(faq.getTitle());
        textViewContent.setText(faq.getContent());
        imageView.setImageDrawable(faq.getImage());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar= getActivity().findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(R.string.FAQ);
        }
    }
}