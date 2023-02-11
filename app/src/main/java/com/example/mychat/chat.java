package com.example.mychat;

import android.annotation.SuppressLint;
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
    //Declaracion
    //Elementos visuales
    private ImageButton imgBtEnviar, imgButtonIP;
    private TextView txtMensaje, ip;

    //Variables
    private String ipValue;
    private ArrayList<String> list;
    //
    private boolean listo, activo = false;
    private ServerSocket servidor;

    //Recycler
    RecyclerView recyclerView;
    RecyclerAdapter recAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Array donde se van a guardar los mensajes
        list = new ArrayList<>();
        //Llamo al constructor
        super.onCreate(savedInstanceState);
        //Le digo a la actividad cual es su layout
        setContentView(R.layout.chat);
        //Elementos para el Recycler
        recyclerView = (RecyclerView) findViewById(R.id.recView);
        recAdapter = new RecyclerAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(chat.this);
        recyclerView.setAdapter(recAdapter);
        recyclerView.setLayoutManager(layoutManager);

        //Asigno la ip de los elementos graficos a los elementos de la activity
        imgButtonIP = (ImageButton) findViewById(R.id.imgButtonIP);
        imgBtEnviar = (ImageButton) findViewById(R.id.btEnviar);
        txtMensaje = (TextView) findViewById(R.id.txtMensaje);
        ip = (TextView) findViewById(R.id.txtIP);

        //comienzan sin ser visibles porque no va a haber ningun texto para confirmar
        imgBtEnviar.setVisibility(View.INVISIBLE);
        imgButtonIP.setVisibility(View.INVISIBLE);

        //Pongo en escucha al servidor
        servidor();

        //Cuando este boton se pulse se enviará el mensaje escrito
        imgBtEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //en caso de que ya tengamos una ip asignada se envia
                if (listo) {
                    enviar();
                }
                //pero en caso contrario informo al usuario que debe de establecer una IP primero
                else {
                    toast("Set a contact IP");
                }
            }
        });
        //Boton para guardar la ip
        imgButtonIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //obtengo la ip escrita por el usuario
                ipValue = ip.getText().toString();
                //Si se ha pulsado este boton y ha ido bien, está listo para enviar mensajes
                listo = true;
                //informo de que se ha guardado correctamente
                toast("Established IP");
                imgButtonIP.setVisibility(View.INVISIBLE);
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

        //igual para establecer la ip de tu contacto
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
    }

    //metodo para escribir los toast
    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    //metodo para enviar un mensaje
    public void enviar() {
        Thread hilo2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket misocket = null;
                try {
                    //Establezco al socket la ip y un puerto que raramente se esté usando para otra actividad
                    misocket = new Socket(ipValue, 9999);
                    //Creo una instncia de 'DataOutputStream' a partir del 'OutputStream' obtenido del socket 'misocket'
                    DataOutputStream dos = new DataOutputStream(misocket.getOutputStream());
                    //Guardo el mensaje que se quiere enviar
                    String msg = txtMensaje.getText().toString();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Añado el mensaje
                            list.add("E" + msg);
                            recAdapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(list.size() - 1);
                            txtMensaje.setText("");
                        }
                    });
                    //Envio el mensaje por el stream de datos de salida
                    dos.writeUTF(msg);
                    //cierro el socket
                    misocket.close();
                } catch (IOException ex) {
                    //escribo un mensaje al emisor para informar de que no se ha enviado
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //muestro un error al usuario para que pruebe a cambiar la ip
                            toast("Error sending message. \n Possible ip Error");
                        }
                    });
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

    public void servidor() {
        Thread hilo1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //establezco un puerto al socket
                    servidor = new ServerSocket(9999);
                    activo = true;
                    while (activo) {
                        //acepta la conexion del socket con el socketservidor
                        Socket misocket = servidor.accept();
                        //un flujo de datos de entrada para el socket creado anteriormente
                        DataInputStream dis = new DataInputStream(misocket.getInputStream());
                        //el mensaje recibido
                        String msg = dis.readUTF();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //añado el mensaje al Receptor
                                list.add("R" + msg);
                                //notifico al recycler adapter que se han realizado cambios
                                recAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(list.size() - 1);
                            }
                        });
                        misocket.close();
                    }
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        hilo1.start();
    }

    @Override
    public void onBackPressed() {
        //cuando pulse para atras se cerrará la actividad entonces paro el bucle del servidor estableciendo
        //su variable activo a false
        super.onBackPressed();
        activo = false;
        try {
            //una vez hecho eso cierro el servidor
            servidor.close();
            Log.d("cerrado", "Se ha cerrado");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
