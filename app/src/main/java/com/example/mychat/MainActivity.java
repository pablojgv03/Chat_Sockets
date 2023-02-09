package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mychat.adapter.RecyclerAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnEnviar;
    private TextView txtMensaje, ip;

    private ArrayList<String> list;

    RecyclerView recyclerView;
    RecyclerAdapter recAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        list = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recView);

        recAdapter = new RecyclerAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setAdapter(recAdapter);

        recyclerView.setLayoutManager(layoutManager);

        btnEnviar = (ImageButton) findViewById(R.id.btEnviar);
        txtMensaje= (TextView) findViewById(R.id.txtMensaje);
        ip= (TextView) findViewById(R.id.txtIP);

        btnEnviar.setVisibility(View.INVISIBLE);
        servidor();
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviar();
            }
        });


        //Detecto cambios en el editText y cada vez que uno sea detectado verifico si la longitud del texto
        //es mayor de 0 en ese caso activo el boton, esto lo hago para controlar que no haya mensajes nulos
        //en caso de que se borre el texto vuelvo a ocultar el boton
        txtMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    btnEnviar.setVisibility(View.VISIBLE);
                } else {
                    btnEnviar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


    }



    public void enviar(){
        Thread hilo2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket misocket = null;
                try {
                    misocket = new Socket(ip.getText().toString() ,9999);
                    DataOutputStream dos = new DataOutputStream(misocket.getOutputStream());
                    String mensaje =  txtMensaje.getText().toString();
                    dos.writeUTF(mensaje);
                    list.add("E"+txtMensaje.getText().toString());
                    misocket.close();
                } catch (IOException ex) {
                    Log.e("Error", "Error al enviar mensaje: " + ex.getMessage());
                } finally {
                    if (misocket != null) {
                        try {
                            misocket.close();
                        } catch (IOException ex) {
                            Log.e("Error", "Error al cerrar socket: " + ex.getMessage());
                        }
                    }
                }
            }
        });
        hilo2.start();
        recAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(list.size()+1);
    }

    public void servidor(){
        Thread hilo1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket servidor = new ServerSocket(9999);
                    while(true){
                        Socket misocket = servidor.accept();
                        DataInputStream dis = new DataInputStream(misocket.getInputStream());

                        list.add("R" + dis.readUTF());
                        misocket.close();
                    }
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        hilo1.start();
        recAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(list.size()+1);
    }
}
