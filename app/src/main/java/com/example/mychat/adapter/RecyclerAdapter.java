package com.example.mychat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {
    List<String> lstMensajes;

    public RecyclerAdapter(List<String> lstMensajes) {
        this.lstMensajes = lstMensajes;
    }

    //Todo 2.3 Este método se encarga de crear la estructura de componentes de cada celda de la
    // lista a partir del layout creado (en este caso custom_item_list.xml)
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Todo 2.3.1 El layoutInflater podría verse como un elemento que se encarga de coger la
        // vista de la celda y anidarla en la estructura jerárquica del padre (parent) en este caso
        // responde a la pregunta. "Esta celda ¿En qué elemento gráfico de tipo lista va a
        // mostrarse? Una vez hecho eso se pasa al viewHolder para que enlace los elementos del
        // layaut con los tipos de datos de cada clase para que puedan ser manipulados en tiempo
        // de ejecución
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_list,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);

        return recyclerHolder;
    }

    //Todo 2.4 Esta vista se encarga de enlazar la información con cada celda. Este método es
    // llamado una vez se ha creado la vista de cada celda de la lista. y lo único que hay que
    // hacer es extraer la información del elemento en la lista y asígnarselo a cada elemento
    // gráfico de la celda.
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        String mensaje = lstMensajes.get(position);
        holder.txtMsgIzq.setText(mensaje);

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return lstMensajes.size();
    }


    //TODO 2.1 Primero se crea la clase que hereda de ViewHolder. Esta clase tiene como finalidad
    // recrear los elementos de la vista del layout de cada elemento de la lista acorde al modelo
    // de datos creado (en este caso la clase Pelicula)
    public class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView txtMsgIzq;
        TextView  txtMsgDer;

        //Todo 2.1.1 El constructor recibe como parámetro un tipo vista(itemView) que contiene la celda ya creada
        // a partir del layout correspondiente.
        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            txtMsgIzq = (TextView)  itemView.findViewById(R.id.mensajeIzq);
            txtMsgDer  = (TextView)  itemView.findViewById(R.id.mensajeDer);


        }
    }
}