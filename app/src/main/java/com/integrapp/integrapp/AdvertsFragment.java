package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdvertsFragment extends Fragment {

    private Server server;
    private String SearchType;

    public AdvertsFragment() {
        SearchType = "all";
    }

    @SuppressLint("ValidFragment")
    public AdvertsFragment(String userId) {
        SearchType = userId;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.activity_advert, container, false);

        this.server = Server.getInstance();

        if (SearchType.equals("all")) getAllAdverts(""); //Show adverts
        else getAllUserAdverts(SearchType); //Show user adverts
        System.out.print("primer posible error ini");

        FloatingActionButton fab = view.findViewById(R.id.fab);

        System.out.print("primer posible error fin");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.screen_area, new NewAdvertFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("StaticFieldLeak")
    private void getAllAdverts(String type) {
        final String getType = type;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                System.out.println("TOKEN :" + server.token); //correcto (es nulo cuando entras la primera vez)
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                String token = preferences.getString("user_token", "user_token");
                server.token = token; //por eso lo volemos a guardar en el server
                System.out.println("TOKEN PREFERENCES: " + token);
                return server.getAllAdverts(getType);
            }

            @Override
            protected void onPostExecute(String s) {
                System.out.println("ADVERTS : " +s);
                if (!s.equals("ERROR IN GETTING ALL ADVERTS")) {

                    ArrayList<ArrayList<String>> attributes = getAttributesAllAdverts(s, getType);

                    //Preparación del diseño
                    LinearLayout contentAdvert = getView().findViewById(R.id.includeContentAdvert);
                    ListView list;
                    list = contentAdvert.findViewById(R.id.sampleListView);
                    final ArrayList<DataAdvert> adverts = new ArrayList<>();
                    DataAdvert dataAdvert;
                    /*Imagen fija*/
                    int image = R.drawable.project_preview_large_2;

                    final ArrayList<UserDataAdvertiser> usersData = new ArrayList<>();
                    UserDataAdvertiser userDataAdvertiser;

                    for (int i=0; i< attributes.size(); ++i) {
                        dataAdvert = new DataAdvert(attributes.get(i).get(0), attributes.get(i).get(1),
                                attributes.get(i).get(2), attributes.get(i).get(3), attributes.get(i).get(4),
                                attributes.get(i).get(5), attributes.get(i).get(6), image, attributes.get(i).get(8));
                        adverts.add(dataAdvert);

                        //Los datos del usuario que ha publicado el anuncio
                        userDataAdvertiser = new UserDataAdvertiser(attributes.get(i).get(7));
                        usersData.add(userDataAdvertiser);
                    }

                    AdvertsAdapter myadapter = new AdvertsAdapter(getView().getContext(), adverts);
                    list.setAdapter(myadapter);
                    myadapter.notifyDataSetChanged();


                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            DataAdvert dataAdvert = adverts.get(position);
                            UserDataAdvertiser userData = usersData.get(position);
                            Fragment fragment = new SingleAdvertFragment(dataAdvert, userData);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
                            ft.replace(R.id.screen_area, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    });

                }
                else {
                    Toast.makeText(getActivity(), "Error loading ads", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getAllUserAdverts(String type) {
        final String getType = type;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                System.out.println("TOKEN :" + server.token); //correcto (es nulo cuando entras la primera vez)
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                String token = preferences.getString("user_token", "user_token");
                server.token = token; //por eso lo volemos a guardar en el server
                System.out.println("TOKEN PREFERENCES: " + token);
                return server.getAllUserAdverts(getType);
            }

            @Override
            protected void onPostExecute(String s) {
                System.out.println("ADVERTS : " +s);
                if (!s.equals("ERROR IN GETTING ALL ADVERTS")) {
                    getInfoUserById(getType, s);
                }
                else {
                    Toast.makeText(getActivity(), "Error loading ads", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getInfoUserById(final String id, final String arguments) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return server.getUserInfoById(id);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GET INFO USER")) {
                    System.out.println("INFO USUARI RESPONSE: " +s);
                    getInfoUser(s, id, arguments);
                }
                else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void getInfoUser(String s, String id, String arguments) {
        //try {
            /*JSONObject myJsonjObject = new JSONObject(s);
            String username = myJsonjObject.getString("username");
            String name = myJsonjObject.getString("name");
            String type = myJsonjObject.getString("type");
            String email = myJsonjObject.getString("email");
            String phone = myJsonjObject.getString("phone");*/
            UserDataAdvertiser uda = new UserDataAdvertiser(id, s);
            setViewAdvertsofUser(arguments, uda, id);
        //} catch (JSONException e) {
            //e.printStackTrace();
        //}
    }

    private void setViewAdvertsofUser(String s, UserDataAdvertiser uda, String id) {
        ArrayList<ArrayList<String>> attributes = getAttributesAllAdverts(s, id);
        //Preparación del diseño
        LinearLayout contentAdvert = getView().findViewById(R.id.includeContentAdvert);
        ListView list;
        list = contentAdvert.findViewById(R.id.sampleListView);
        final ArrayList<DataAdvert> adverts = new ArrayList<>();
        DataAdvert dataAdvert;
        /*Imagen fija*/
        int image = R.drawable.project_preview_large_2;

        final ArrayList<UserDataAdvertiser> usersData = new ArrayList<>();

        for (int i=0; i< attributes.size(); ++i) {
            dataAdvert = new DataAdvert(attributes.get(i).get(0), attributes.get(i).get(1),
                    attributes.get(i).get(2), attributes.get(i).get(3), attributes.get(i).get(4),
                    attributes.get(i).get(5), attributes.get(i).get(6), image, attributes.get(i).get(7));
            adverts.add(dataAdvert);

            //Los datos del usuario que ha publicado el anuncio
            usersData.add(uda);
        }

        AdvertsAdapter myadapter = new AdvertsAdapter(getView().getContext(), adverts);
        list.setAdapter(myadapter);
        myadapter.notifyDataSetChanged();


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataAdvert dataAdvert = adverts.get(position);
                UserDataAdvertiser userData = usersData.get(position);
                Fragment fragment = new SingleAdvertFragment(dataAdvert, userData);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.screen_area, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private ArrayList<ArrayList<String>> getAttributesAllAdverts(String stringJson, String getType) {
        ArrayList<ArrayList<String>> attributes = new ArrayList<>();
        try {
            JSONArray myJsonjArray = new JSONArray(stringJson);
            for (int i = 0; i < myJsonjArray.length(); ++i) {
                System.out.println("json: " + i + " "+ myJsonjArray.getString(i));
                ArrayList<String> attributesAdd = getAttributesAdvert(myJsonjArray, i, getType); //atributos de un anuncio
                attributes.add(attributesAdd);
            }
            return attributes;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<String> getAttributesAdvert(JSONArray myJsonjArray, int index, String getType) throws JSONException {
        ArrayList<String> attributesAdd = new ArrayList<>();
        String advertString = myJsonjArray.getString(index);
        JSONObject myJsonjObject = new JSONObject(advertString);

        /*Aqui puedes añadir otro atributo de la respuesta Json del servidor
        si es necesario para mostrarlo en el diseño. De momento solo estan estos
        5 atributos, (date, tittle, description, places, typeAdvert*/
        attributesAdd.add(myJsonjObject.getString("date"));
        attributesAdd.add(myJsonjObject.getString("title"));
        attributesAdd.add(myJsonjObject.getString("description"));
        attributesAdd.add(myJsonjObject.getString("places"));
        attributesAdd.add(myJsonjObject.getString("typeAdvert"));
        attributesAdd.add(myJsonjObject.getString("state"));
        attributesAdd.add(myJsonjObject.getString("userId"));

        if (getType.equals("") || getType.equals("offer") || getType.equals("lookFor"))
        attributesAdd.add(myJsonjObject.getString("user")); //el Json en string de los datos del usuario
        attributesAdd.add(myJsonjObject.getString("_id"));


        return attributesAdd;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.filter_advert, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_lookfor) {
            getAllAdverts("lookFor");
            Toast.makeText(getActivity(), "Look for ads", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.action_offer) {
            getAllAdverts("offer");
            Toast.makeText(getActivity(), "Offer ads", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.action_all) {
            getAllAdverts("");
            Toast.makeText(getActivity(), "All ads", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}