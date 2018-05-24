package com.integrapp.integrapp.Inscription;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.integrapp.integrapp.Server;

class InscriptionServer {
    private Server server;
    private static final InscriptionServer ourInstance = new InscriptionServer();

    private String RAWAllUserInscriptions = "";
    private String RAWAllInscriptions = "";

    static InscriptionServer getInstance() {
        return ourInstance;
    }

    private InscriptionServer() {
        this.server = Server.getInstance();
    }

    public String getAllInscriptions(String idAdvert){
        if(this.RAWAllInscriptions.isEmpty()){
            this.RAWAllInscriptions = this.server.getAllInscriptions(idAdvert);
            return this.RAWAllInscriptions;
        }else{
            updateAllInscriptions(idAdvert);
            return this.RAWAllInscriptions;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateAllInscriptions(String idAdvert){
        final String id = idAdvert;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getAllInscriptions(id);
            }

            protected void onPostExecute(String s) {
                RAWAllInscriptions = s;
            }
        }.execute();
    }

    public String getAllUserInscriptions(String userId){
        if(this.RAWAllUserInscriptions.isEmpty()){
            this.RAWAllUserInscriptions = this.server.getAllUserInscriptions(userId);
            return this.RAWAllUserInscriptions;
        }else{
            updateAllUserInscriptions(userId);
            return this.RAWAllUserInscriptions;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateAllUserInscriptions(String userId){
        final String id = userId;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getAllUserInscriptions(id);
            }

            protected void onPostExecute(String s) {
                RAWAllUserInscriptions = s;
            }
        }.execute();
    }
}
