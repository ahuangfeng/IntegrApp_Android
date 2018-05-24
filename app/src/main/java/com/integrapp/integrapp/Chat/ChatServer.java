package com.integrapp.integrapp.Chat;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.integrapp.integrapp.Server;

/**
 * Created by alexhuang05 on 24/05/18.
 */

class ChatServer {
    private static final ChatServer ourInstance = new ChatServer();

    private Server server;
    private String RAWUsers = "";
    public static ChatServer getInstance() {
        return ourInstance;
    }

    private ChatServer() {
        this.server = Server.getInstance();
    }

    public String getUsers(){
        if(this.RAWUsers.isEmpty()){
            this.RAWUsers = this.server.getUsers();
            return this.RAWUsers;
        }else{
            this.updateUsers();
            return this.RAWUsers;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateUsers(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getUsers();
            }

            protected void onPostExecute(String s) {
                RAWUsers = s;
            }

        }.execute();
    }
}
