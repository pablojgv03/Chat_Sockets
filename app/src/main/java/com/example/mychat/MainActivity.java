package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnEnviar;
    private TextView txtMensaje, ip;
    private ArrayList<String> list;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = new ArrayList<>();
        btnEnviar = (Button) findViewById(R.id.btEnviar);
        txtMensaje= (TextView) findViewById(R.id.txtMensaje);
        ip= (TextView) findViewById(R.id.txtIP);
        servidor();
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviar();
            }
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

                    list.add("1"+txtMensaje.getText().toString());

                    dos.writeUTF(mensaje);
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
                        list.add("2" + dis.readUTF());
                        misocket.close();
                    }
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        hilo1.start();
    }
}
