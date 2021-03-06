package com.integrapp.integrapp.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.integrapp.integrapp.Model.ChatAppMsgDTO;
import com.integrapp.integrapp.Model.User;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SingleChatFragment extends Fragment {

    private ImageView imageView;
    private Server server;

    private User toUser;
    private String personalUserId;
    private EditText msgBox;
    private RecyclerView msgRecyclerView;
    private List<ChatAppMsgDTO> msgDtoList;
    private ChatAppMsgAdapter chatAppMsgAdapter ;
    private Emitter.Listener handleIncomingMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (isAdded()) {
                SingleChatFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        System.out.println(data);

                        try {
                            String from = data.getString("from");
                            if (from.equals(toUser.getId().toString())) {
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
        }
    };
    private Socket mSocket; {
        try {
            mSocket = IO.socket("https://integrappbackend.herokuapp.com/");
            Log.e("EVENT_CONNECT", "SUCCESS");
        } catch (URISyntaxException e) {
            Log.e("EVENT_ERROR", "ERROR CONNECTING");
        }
    }

    ///////////////////////////////methods/////////////////////////////////////////////////////////
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

        this.server = Server.getInstance();
        imageView = view.findViewById(R.id.profile_image);

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
        System.out.println("emit: "+ personalUserId +" "+ toUser.getId());
        mSocket.emit("new user", personalUserId, toUser.getId(), new Ack() {
            @Override
            public void call(Object... args) {
                Log.i("callback", "test");
                if (!args[0].toString().equals("false")) {
                    try {
                        JSONObject myJsonObject = new JSONObject(args[0].toString());
                        final JSONArray chats = myJsonObject.getJSONArray("chats");
                        System.out.println("chats: "+chats.toString());
                        SingleChatFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            addHistory(chats);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    SingleChatFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showError();
                        }
                    });
                }
            }
        });

        setImagePhoto();

        return view;
    }

    private void addHistory(JSONArray chats){
        try {
            for (int i = 0; i < chats.length(); i++) {
                String from = chats.getJSONObject(i).getString("from");
                String content = chats.getJSONObject(i).getString("content");
                if (from.equals(personalUserId)) {
                    ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_SENT, content);
                    msgDtoList.add(msgDto);
                } else {
                    ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, content);
                    msgDtoList.add(msgDto);
                }
            }
            int newMsgPosition = msgDtoList.size() - 1;
            chatAppMsgAdapter.notifyDataSetChanged();
            msgRecyclerView.scrollToPosition(newMsgPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar= getActivity().findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(R.string.MainChatsFragment_title);
        }
    }

    private void showError() {
        Toast.makeText(getActivity(), getString(R.string.error_LoadingHistory), Toast.LENGTH_SHORT).show();
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

    @SuppressLint("StaticFieldLeak")
    private void setImagePhoto() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences preferences = SingleChatFragment.this.getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.getImageUser(toUser.getId());
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING IMAGE")) {
                    System.out.println("laimagenobtenida: " + s);
                    Picasso.with(getContext()).load(s).into(imageView);
                }
            }
        }.execute();
    }

}
