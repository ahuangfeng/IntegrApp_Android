package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SingleChatFragment extends Fragment {

    private User toUser;
    private String personalUserId;
    private EditText msgBox;
    private RecyclerView msgRecyclerView;
    private List<ChatAppMsgDTO> msgDtoList;
    private ChatAppMsgAdapter chatAppMsgAdapter ;

    private Emitter.Listener handleIncomingMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject)args[0];
                    System.out.println(data);

                    try {
                        String from = data.getString("from");
                        if (!from.equals(personalUserId)) {
                            String message = data.getString("content");
                            ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, message);
                            msgDtoList.add(msgDto);
                            int newMsgPosition = msgDtoList.size() - 1;
                            chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
                            msgRecyclerView.scrollToPosition(newMsgPosition);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://integrappbackend.herokuapp.com/");
            Log.e("EVENT_CONNECT", "SUCCESS");
        } catch (URISyntaxException e) {
            Log.e("EVENT_ERROR", "ERROR CONNECTING");
        }
    }

    public SingleChatFragment() {
    }

    @SuppressLint({"ValidFragment", "SetTextI18n"})
    public SingleChatFragment(User user) {
        this.toUser = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mSocket.on("new message", handleIncomingMessage);
        mSocket.on("new message", handleIncomingMessage);
        mSocket.connect();
    }

    @SuppressLint("StaticFieldLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.single_chat_fragment, container, false);
        msgBox = view.findViewById(R.id.write_message);
        TextView user_name = view.findViewById(R.id.chat_with);
        Button send = view.findViewById(R.id.send_button);
        user_name.setText(toUser.getName());
        // Get RecyclerView object.
        msgRecyclerView = view.findViewById(R.id.messages);
        //Set recyclerView layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        //Create the initial data list
        msgDtoList = new ArrayList<>();
        //Create the data adapter with the above data list
        chatAppMsgAdapter  = new ChatAppMsgAdapter(msgDtoList);
        //Set data adapter to the RecycleView
        msgRecyclerView.setAdapter(chatAppMsgAdapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        personalUserId = preferences.getString("idUser", "null");

        mSocket.emit("new user", personalUserId, toUser.getId(),new Ack() {
            @Override
            public void call(Object... args) {
                Log.i("callback","test");
                System.out.println("RETURNING EVENT CALLBACK " + args[0]);
                try {
                    JSONObject myJsonjObject = new JSONObject(args[0].toString());
                    System.out.println("CHATS: " + myJsonjObject.getString("chats"));
                    JSONArray chats = myJsonjObject.getJSONArray("chats");
                    for (int i = 0; i < chats.length(); i++) {
                        System.out.println("Chat numero " + i+" : "+ chats.get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        return view;
    }


    private void sendMessage() {
        String msgContent = msgBox.getText().toString();
        if(!TextUtils.isEmpty(msgContent))
        {
            // Add a new sent message to the list.
            ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_SENT, msgContent);
            msgDtoList.add(msgDto);
            int newMsgPosition = msgDtoList.size() - 1;
            // Notify recycler view insert one new data.
            chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
            // Scroll RecyclerView to the last message.
            msgRecyclerView.scrollToPosition(newMsgPosition);
            // Empty the input edit text box.
            msgBox.setText("");
            mSocket.emit("send message", msgContent, toUser.getId());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

}
