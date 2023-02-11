package com.example.mychat;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.adapter.RecyclerAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class chat extends AppCompatActivity {

    private ImageButton imgBtEnviar, imgButtonIP;
    private TextView txtMensaje, ip;
    private String ipValue;
    private ArrayList<String> list;
    private boolean listo = false;
    private Toast toastE, toastC, toastEstablished;
    RecyclerView recyclerView;
    RecyclerAdapter recAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        list = new ArrayList<>();
        toastE = Toast.makeText(getApplicationContext(), "Set a contact IP", Toast.LENGTH_SHORT);
        toastC = Toast.makeText(getApplicationContext(), "Send", Toast.LENGTH_SHORT);
        toastEstablished = Toast.makeText(getApplicationContext(), "Established IP", Toast.LENGTH_SHORT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        recyclerView = (RecyclerView) findViewById(R.id.recView);

        recAdapter = new RecyclerAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(chat.this);
        recyclerView.setAdapter(recAdapter);

        recyclerView.setLayoutManager(layoutManager);

        imgButtonIP = (ImageButton) findViewById(R.id.imgButtonIP);
        imgBtEnviar = (ImageButton) findViewById(R.id.btEnviar);
        txtMensaje = (TextView) findViewById(R.id.txtMensaje);
        ip = (TextView) findViewById(R.id.txtIP);

        imgBtEnviar.setVisibility(View.INVISIBLE);
        imgButtonIP.setVisibility(View.INVISIBLE);
        servidor();
        imgBtEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listo) {
                    enviar();
                } else {
                    toastE.show();
                }
            }
        });
        imgButtonIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ipValue = ip.getText().toString();
                listo = true;
                toastEstablished.show();
            }
        });


        ip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    imgButtonIP.setVisibility(View.VISIBLE);
                } else {
                    imgButtonIP.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Detecto cambios en el editText y cada vez que uno sea detectado verifico si la longitud del texto
        //es mayor de 0 en ese caso activo el boton, esto lo hago para controlar que no haya mensajes nulos
        //en caso de que se borre el texto vuelvo a ocultar el boton
        txtMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    imgBtEnviar.setVisibility(View.VISIBLE);
                } else {
                    imgBtEnviar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }


    public void enviar() {
        Thread hilo2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket misocket = null;
                try {
                    misocket = new Socket(ipValue, 9999);
                    DataOutputStream dos = new DataOutputStream(misocket.getOutputStream());
                    String mensaje = txtMensaje.getText().toString();
                    list.add("E" + txtMensaje.getText().toString());
                    toastC.show();
                    txtMensaje.setText("");
                    dos.writeUTF(mensaje);
                    misocket.close();
                } catch (IOException ex) {
                    list.add("E" + "Error al enviar \"" + txtMensaje.getText().toString() + "\"");
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
        recyclerView.scrollToPosition(list.size() + 1);
    }

    public void servidor() {
        Thread hilo1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket servidor = new ServerSocket(9999);
                    while (true) {
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
        recyclerView.scrollToPosition(list.size() + 1);
    }
}
