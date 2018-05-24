package com.integrapp.integrapp.Chat;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.integrapp.integrapp.Server;

class ChatServer {
    private static final ChatServer ourInstance = new ChatServer();

    private Server server;
    private String RAWUsers = "";
    private String RAWChats = "";
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

    public String getChats(String personalId){
        if(this.RAWChats.isEmpty()){
            this.RAWChats = this.server.getChats(personalId);
            return this.RAWChats;
        }else{
            this.updateChats(personalId);
            return this.RAWChats;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateChats(final String personalId){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getChats(personalId);
            }

            protected void onPostExecute(String s) {
                RAWChats = s;
            }

        }.execute();
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
