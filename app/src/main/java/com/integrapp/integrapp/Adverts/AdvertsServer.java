package com.integrapp.integrapp.Adverts;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.integrapp.integrapp.Server;

class AdvertsServer {
    private static final AdvertsServer ourInstance = new AdvertsServer();

    private Server server;
    private String RAWAdverts = "";
    private String RAWAllUsersAdverts = "";
//    private String RAWUser = "";

    static AdvertsServer getInstance() {
        return ourInstance;
    }

    private AdvertsServer() {
        this.server = Server.getInstance();
    }

//    public String getUserInfoById(String id){
//        if(this.RAWUser.isEmpty()){
//            this.RAWUser = this.server.getUserInfoById(id);
//            return this.RAWUser;
//        }else{
//            updateUser(id);
//            return this.RAWUser;
//        }
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    private void updateUser(String id){
//        final String idFinal = id;
//        new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... voids) {
//                return server.getUserInfoById(idFinal);
//            }
//
//            protected void onPostExecute(String s) {
//                RAWUser = s;
//            }
//        }.execute();
//    }

    public String getAllUserAdverts(String getType){
        if(this.RAWAllUsersAdverts.isEmpty()){
            this.RAWAllUsersAdverts = this.server.getAllUserAdverts(getType);
            return this.RAWAllUsersAdverts;
        }else{
            updateAllUserAdverts(getType);
            return this.RAWAllUsersAdverts;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateAllUserAdverts(String getType){
        final String types = getType;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getAllUserAdverts(types);
            }

            protected void onPostExecute(String s) {
                RAWAllUsersAdverts = s;
            }
        }.execute();
    }

    public String getAllAdverts(String getType){
        if(this.RAWAdverts.isEmpty()){
            this.RAWAdverts = this.server.getAllAdverts(getType);
            return this.RAWAdverts;
        }else{
            updateAdverts(getType);
            return this.RAWAdverts;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateAdverts(String getType){
        final String types = getType;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getAllAdverts(types);
            }

            protected void onPostExecute(String s) {
                RAWAdverts = s;
            }
        }.execute();
    }

}
