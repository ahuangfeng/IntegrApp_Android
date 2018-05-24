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
    private String RAWForumEntre = "";
    private String RAWForumLang = "";
    private String RAWForumOther = "";

    static ForumServer getInstance() {
        return ourInstance;
    }

    private ForumServer() {
        this.server = Server.getInstance();
    }

    public String getForumOther(){
        if(this.RAWForumOther.isEmpty()){
            this.RAWForumOther = this.server.getForumOther();
            return this.RAWForumOther;
        }else{
            updateForumOther();
            return this.RAWForumOther;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateForumOther(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getForumOther();
            }

            protected void onPostExecute(String s) {
                RAWForumOther = s;
            }

        }.execute();
    }

    public String getForumLang(){
        if(this.RAWForumLang.isEmpty()){
            this.RAWForumLang = this.server.getForumLang();
            return this.RAWForumLang;
        }else{
            updateForumLang();
            return this.RAWForumLang;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateForumLang(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getForumLang();
            }

            protected void onPostExecute(String s) {
                RAWForumLang = s;
            }

        }.execute();
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


    public String getForumEntre(){
        if(this.RAWForumEntre.isEmpty()){
            this.RAWForumEntre = this.server.getForumEntre();
            return this.RAWForumEntre;
        }else{
            updateForumEntre();
            return this.RAWForumEntre;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateForumEntre(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getForumEntre();
            }

            protected void onPostExecute(String s) {
                RAWForumEntre = s;
            }

        }.execute();
    }
}
