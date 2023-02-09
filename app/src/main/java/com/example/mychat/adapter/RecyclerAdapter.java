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

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_list,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);

        return recyclerHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        String mesaje = lstMensajes.get(position);
        String emisor_receptor = mesaje.substring(0,1);
        mesaje = mesaje.substring(1);
        if(emisor_receptor.equals("E")){
            holder.txtMsgDer.setText(mesaje);
            holder.txtMsgIzq.setVisibility(View.GONE);
        }else {
            holder.txtMsgIzq.setText(mesaje);
            holder.txtMsgDer.setVisibility(View.GONE);
        }
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return lstMensajes.size();
    }


    public class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView txtMsgIzq;
        TextView  txtMsgDer;


        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);
            txtMsgIzq = (TextView)  itemView.findViewById(R.id.mensajeIzq);
            txtMsgDer  = (TextView)  itemView.findViewById(R.id.mensajeDer);
        }
    }
}