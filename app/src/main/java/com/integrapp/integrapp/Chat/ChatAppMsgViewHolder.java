package com.integrapp.integrapp.Chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.integrapp.integrapp.R;

public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {
// TODO: refactor amb getters
    LinearLayout leftMsgLayout;

    LinearLayout rightMsgLayout;

    TextView leftMsgTextView;

    TextView rightMsgTextView;

    public ChatAppMsgViewHolder(View itemView) {
        super(itemView);

        if(itemView!=null) {
            leftMsgLayout = itemView.findViewById(R.id.chat_left_msg_layout);
            rightMsgLayout = itemView.findViewById(R.id.chat_right_msg_layout);
            leftMsgTextView = itemView.findViewById(R.id.chat_left_msg_text_view);
            rightMsgTextView = itemView.findViewById(R.id.chat_right_msg_text_view);
        }
    }
}
