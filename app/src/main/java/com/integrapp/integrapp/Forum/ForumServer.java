package com.integrapp.integrapp.Forum;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.integrapp.integrapp.Server;

/**
 * Created by alexhuang05 on 24/05/18.
 */

class ForumServer {
    private Server server;
    private static final ForumServer ourInstance = new ForumServer();

    private String RAWForumDocu = "";

    static ForumServer getInstance() {
        return ourInstance;
    }

    private ForumServer() {
        this.server = Server.getInstance();
    }

    public String getForumDocu(){
        if(this.RAWForumDocu.isEmpty()){
            this.RAWForumDocu = this.server.getForumDocu();
            return this.RAWForumDocu;
        }else{
            updateForumDocu();
            return this.RAWForumDocu;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateForumDocu(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getForumDocu();
            }

            protected void onPostExecute(String s) {
                RAWForumDocu = s;
            }

        }.execute();
    }
}
