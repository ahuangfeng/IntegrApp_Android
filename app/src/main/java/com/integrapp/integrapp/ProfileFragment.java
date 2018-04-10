package com.integrapp.integrapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_profile, container, false);

        /*TODO DEPRECATED:
        Borrar esto luego, los datos para el perfil los obtenemos en el momento del login
        * con la nueva petición getUserInfo del servidor.*/
        SharedPreferences preferences = getActivity().getSharedPreferences("fields_profile", Context.MODE_PRIVATE);
        String user = preferences.getString("username", "username");
        String name = preferences.getString("full_name", "full_name");
        String type_user = preferences.getString("type_user", "type_user");

        System.out.println("name " + name);
        System.out.println("name " + user);
        System.out.println("name " + type_user);

        /*Es mejor si hacemos una petición al servidor para obtener los datos del usuario
         * en lugar de pillarlos en el momento de hacer signup, ya que si hacemos log in con otro
         * usuario, las preferences son incorrectas porque tienen los datos del ultimo usuario
         * en registrarse, por lo que el Perfil del usuario no seria el suyo.
         * TODO: Una vez hecho el login y obtenidos los datos del servidor entonces podemos hacer preferences*/

        /*Es necesario que en back se guarde el nombre -full_name- (email i mobil no creo que sea necesario)*/

        TextView nameTextView = view.findViewById(R.id.nameTextView);
        nameTextView.setText(name);

        TextView usernameTextView = view.findViewById(R.id.usernameTextView);
        usernameTextView.setText(user);

        TextView typeUserTextView = view.findViewById(R.id.typeUserTextView);
        typeUserTextView.setText(type_user);

        TextView emailTextView = view.findViewById(R.id.emailTextView);
        String mail = user + "@gmail.com";
        emailTextView.setText(mail);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
