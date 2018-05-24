package com.integrapp.integrapp.Forum;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.integrapp.integrapp.Server;

class ForumServer {
    private Server server;
    private static final ForumServer ourInstance = new ForumServer();

    private String RAWForumDoc = "";
    private String RAWForumEnter = "";
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

    public String getForumDoc(){
        if(this.RAWForumDoc.isEmpty()){
            this.RAWForumDoc = this.server.getForumDocu();
            return this.RAWForumDoc;
        }else{
            updateForumDoc();
            return this.RAWForumDoc;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateForumDoc(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getForumDocu();
            }

            protected void onPostExecute(String s) {
                RAWForumDoc = s;
            }

        }.execute();
    }


    public String getForumEnter(){
        if(this.RAWForumEnter.isEmpty()){
            this.RAWForumEnter = this.server.getForumEntre();
            return this.RAWForumEnter;
        }else{
            updateForumEnter();
            return this.RAWForumEnter;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateForumEnter(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getForumEntre();
            }

            protected void onPostExecute(String s) {
                RAWForumEnter = s;
            }

        }.execute();
    }
}
