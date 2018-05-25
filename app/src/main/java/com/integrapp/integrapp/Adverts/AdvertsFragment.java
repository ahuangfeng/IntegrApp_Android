package com.integrapp.integrapp.Adverts;

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
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.integrapp.integrapp.Adapters.AdvertsAdapter;
import com.integrapp.integrapp.Model.DataAdvert;
import com.integrapp.integrapp.Model.UserDataAdvertiser;
import com.integrapp.integrapp.R;
import com.integrapp.integrapp.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdvertsFragment extends Fragment {

    private Server server;
    private AdvertsServer advertsServer;
    private String SearchType;
    private View view;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
        view = inflater.inflate(R.layout.activity_advert, container, false);

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        this.server = Server.getInstance();
        this.advertsServer = AdvertsServer.getInstance();

        if (SearchType.equals("all")) getAllAdverts("");
        else getAllUserAdverts(SearchType);

        FloatingActionButton fab = view.findViewById(R.id.fab);

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
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return advertsServer.getAllAdverts(getType);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING ALL ADVERTS")) {

                    ArrayList<ArrayList<String>> attributes = getAttributesAllAdverts(s, getType);
                    if (attributes==null || attributes.isEmpty()) {
                        switch (getType) {
                            case "lookFor":
                                Toast.makeText(getActivity(), getString(R.string.noLookForAdverts), Toast.LENGTH_SHORT).show();
                                break;
                            case "offer":
                                Toast.makeText(getActivity(), getString(R.string.noOfferAdverts), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getActivity(), getString(R.string.noAdverts), Toast.LENGTH_SHORT).show();
                                break;
                        }
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.popBackStackImmediate();
                    } else {

                        //Preparación del diseño
                        LinearLayout contentAdvert = view.findViewById(R.id.includeContentAdvert);
                        ListView list;
                        list = contentAdvert.findViewById(R.id.sampleListView);
                        final ArrayList<DataAdvert> adverts = new ArrayList<>();
                        DataAdvert dataAdvert;
                        /*Imagen fija*/
                        int image = R.drawable.project_preview_large_2;

                        final ArrayList<UserDataAdvertiser> usersData = new ArrayList<>();
                        UserDataAdvertiser userDataAdvertiser;

                        for (int i = 0; i < attributes.size(); ++i) {
                            dataAdvert = new DataAdvert(attributes.get(i).get(0), attributes.get(i).get(1),
                                    attributes.get(i).get(2), attributes.get(i).get(3), attributes.get(i).get(4),
                                    attributes.get(i).get(5), attributes.get(i).get(6), image, attributes.get(i).get(8),
                                    attributes.get(i).get(9));
                            adverts.add(dataAdvert);

                            //Los datos del usuario que ha publicado el anuncio
                            userDataAdvertiser = new UserDataAdvertiser(attributes.get(i).get(7));
                            usersData.add(userDataAdvertiser);
                        }

                        AdvertsAdapter myAdapter = new AdvertsAdapter(view.getContext(), adverts);
                        list.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged();


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

                        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                getAllAdverts(getType);
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_LoadingAds), Toast.LENGTH_SHORT).show();
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
                SharedPreferences preferences = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
                server.token = preferences.getString("user_token", "user_token");
                return server.getAllUserAdverts(getType);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!s.equals("ERROR IN GETTING ALL ADVERTS")) {
                    getInfoUserById(getType, s);
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_LoadingAds), Toast.LENGTH_SHORT).show();
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
                    getInfoUser(s, id, arguments);
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_GettingUserInfo), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void getInfoUser(String s, String id, String arguments) {
         UserDataAdvertiser uda = new UserDataAdvertiser(id, s);
         setViewAdvertsOfUser(arguments, uda, id);
    }

    private void setViewAdvertsOfUser(String s, UserDataAdvertiser uda, String id) {
        final String getId = id;
        ArrayList<ArrayList<String>> attributes = getAttributesAllAdverts(s, id);
        if (attributes==null || attributes.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.noAdvertsOfUser), Toast.LENGTH_SHORT).show();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStackImmediate();
        } else {
            //Preparación del diseño
            LinearLayout contentAdvert = view.findViewById(R.id.includeContentAdvert);
            ListView list;
            list = contentAdvert.findViewById(R.id.sampleListView);
            final ArrayList<DataAdvert> adverts = new ArrayList<>();
            DataAdvert dataAdvert;
            /*Imagen fija*/
            int image = R.drawable.project_preview_large_2;

            final ArrayList<UserDataAdvertiser> usersData = new ArrayList<>();

            for (int i = 0; i < attributes.size(); ++i) {
                dataAdvert = new DataAdvert(attributes.get(i).get(0), attributes.get(i).get(1),
                        attributes.get(i).get(2), attributes.get(i).get(3), attributes.get(i).get(4),
                        attributes.get(i).get(5), attributes.get(i).get(6), image, attributes.get(i).get(7),
                        attributes.get(i).get(8));
                adverts.add(dataAdvert);

                //Los datos del usuario que ha publicado el anuncio
                usersData.add(uda);
            }

            AdvertsAdapter myAdapter = new AdvertsAdapter(view.getContext(), adverts);
            list.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();


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

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getAllUserAdverts(getId);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private ArrayList<ArrayList<String>> getAttributesAllAdverts(String stringJson, String getType) {
        ArrayList<ArrayList<String>> attributes = new ArrayList<>();
        try {
            JSONArray myJsonArray = new JSONArray(stringJson);
            for (int i = 0; i < myJsonArray.length(); ++i) {
                ArrayList<String> attributesAdd = getAttributesAdvert(myJsonArray, i, getType); //atributos de un anuncio
                attributes.add(attributesAdd);
            }
            return attributes;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<String> getAttributesAdvert(JSONArray myJsonArray, int index, String getType) throws JSONException {
        ArrayList<String> attributesAdd = new ArrayList<>();
        String advertString = myJsonArray.getString(index);
        JSONObject myJsonObject = new JSONObject(advertString);

        /*Aqui puedes añadir otro atributo de la respuesta Json del servidor
        si es necesario para mostrarlo en el diseño. De momento solo estan estos
        5 atributos, (date, tittle, description, places, typeAdvert*/
        attributesAdd.add(myJsonObject.getString("date"));
        attributesAdd.add(myJsonObject.getString("title"));
        attributesAdd.add(myJsonObject.getString("description"));
        attributesAdd.add(myJsonObject.getString("places"));
        attributesAdd.add(myJsonObject.getString("typeAdvert"));
        attributesAdd.add(myJsonObject.getString("state"));
        attributesAdd.add(myJsonObject.getString("userId"));

        if (getType.equals("") || getType.equals("offer") || getType.equals("lookFor"))
        attributesAdd.add(myJsonObject.getString("user")); //el Json en string de los datos del usuario
        attributesAdd.add(myJsonObject.getString("_id"));
        attributesAdd.add(myJsonObject.getJSONArray("registered").toString());

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
            Toast.makeText(getActivity(), getString(R.string.toast_LookForAds), Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.action_offer) {
            getAllAdverts("offer");
            Toast.makeText(getActivity(), getString(R.string.toast_OfferAds), Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.action_all) {
            getAllAdverts("");
            Toast.makeText(getActivity(), getString(R.string.toast_AllAds), Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}