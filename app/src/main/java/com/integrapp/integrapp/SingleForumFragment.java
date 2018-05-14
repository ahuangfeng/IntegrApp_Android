package com.integrapp.integrapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SingleForumFragment extends Fragment {

    private String id;
    private String type;
    private String title;
    private String description;
    private String createdAt;
    private String userId;
    private float rate;

    public SingleForumFragment () {}

    @SuppressLint("ValidFragment")
    public SingleForumFragment(ForumItem forumItem) {
        this.id = forumItem.getId();
        this.type = forumItem.getType();
        this.title = forumItem.getTitle();
        this.description = forumItem.getDescription();
        this.createdAt = forumItem.getCreatedAt();
        this.userId = forumItem.getUserId();
        this.rate = forumItem.getRate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.single_forum_fragment, container, false);

        Button profileButton = view.findViewById(R.id.profileButton);
        Button commentButton = view.findViewById(R.id.commentButton);

        TextView title = view.findViewById(R.id.textViewTitleForum);
        TextView createdAt = view.findViewById(R.id.textViewCreatedAtForum);
        TextView description = view.findViewById(R.id.textViewDescriptionForum);

        /*
        title.setText(this.title);
        createdAt.setText(this.createdAt);*/
        description.setText("Viene esto a cuenta de una iniciativa llamada The Atavist (www.atavis.net), una idea surgida de dos periodistas, Evan Ratliff, colaborador habitual de la revista ‘Wired’, y Nicholas Thompson, ex editor de esa misma publicación y de ‘The New Yorker’, cansados ambos de escuchar que internet es el reino de lo breve y superficial. The Atavist es una plataforma digital que explora nuevas maneras de presentar contenidos de no ficción y gran formato. Aprovechando al máximo, eso sí, toda la potencialidad narrativa y multiformato que ofrece internet.\n" +
                "\n" +
                "Todo comenzó de una manera singular en 2009. Ratliff se ‘desconectó’ del todo e hizo como que desaparecía, mientras ‘Wired’ ofrecía una recompensa de 5.000 dólares a quien pudiera encontrarlo. Fue un pelotazo. Desde su puesta de largo, a final de enero, The Atavist ha publicado tres historias largas creadas específicamente para el iPad y otras tabletas como Kindle y Nook. Entre ellas, ‘Lifted’, ¡de 13.000 palabras! La narración no comienza a la manera tradicional sino con un vídeo que te mete de lleno en la trama. Periodística. Real. Llevan ya 40.000 descargas de su aplicación, a 2,99 dólares cada una.\n" +
                "\n" +
                "Lo mejor del caso es que hace un tiempo, en una mesa redonda de postín, se me ocurrió decir que eso de que el papel es para el análisis e internet para la digestión rápida de titulares era una convención sin fundamento, seguramente esgrimida por los ‘papeleros’ para defender su negociado. Claro, se me echaron encima. Pero, ¿cómo no va a servir internet para el análisis y la profundidad si, precisamente, no tiene límite? En internet uno puede acabar escribiendo todo lo largo que quiera. Como yo aquí, a pesar de los consejos de mis colegas.");

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Boton perfil clickado", Toast.LENGTH_SHORT).show();
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Boton comment clickado", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
