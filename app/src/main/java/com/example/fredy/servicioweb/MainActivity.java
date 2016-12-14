package com.example.fredy.servicioweb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnIngresar;
    EditText txtUsu,txtPas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtUsu=(EditText)findViewById(R.id.txtusu);
        txtPas=(EditText)findViewById(R.id.txtpas);
        btnIngresar=(Button)findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        //implementar un hilo
        Thread hilo = new Thread(){



            @Override
            public void run()  {
                //método que permite ejecutar el código en el hilo
                final String resultado=enviarDatosGET(txtUsu.getText().toString(),txtPas.getText().toString());
                //trabaja con la interfaz gráfica desde el hilo
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = obtDatosJSON(resultado);
                        if (r>0){
                            Intent i=new Intent (getApplicationContext(), registro.class);
                            i.putExtra("cod",txtUsu.getText().toString());
                            startActivity(i);
                        }else{
                            Toast.makeText(getApplicationContext(), "Usuario o Contraseña Incorrectos", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        hilo.start();
    }
    public String enviarDatosGET(String usu, String pas) {
        //inicializar variables
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;

        try {
            //en android studio no funciona el localhost y se tiene que poner la ip local
            url = new URL("http://192.168.0.18/weblogin/valida.php?usu=" + usu + "&pas=" + pas);
            HttpURLConnection conection=(HttpURLConnection) url.openConnection();
            respuesta=conection.getResponseCode();

            resul=new StringBuilder();

            //la respuesta también puede ser el número 200
            if(respuesta==HttpURLConnection.HTTP_OK){
                //toma la respuesta
                InputStream in=new BufferedInputStream(conection.getInputStream());

                //una vez traída la respuesta bufferedreadeer se va a encargar de leerla
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));

                //llenar la variable resul con el dato recopilado; en este caso solo devuelve una línea
                while((linea=reader.readLine())!= null){
                    resul.append(linea);
                }
            }
        } catch (Exception e) {}
            //retornar como cadena porque el método es de tipo string
            return resul.toString();


    }

    public int obtDatosJSON(String response){
        int res=0;
            try{
             //sirve para saber si tiene datos el JSON o no y así decidir si se envía a la siguiente ventana
                JSONArray json=new JSONArray(response);
                if(json.length()>0){
                    res=1;
                }

            }catch(Exception e){}
        return res;
    }



}
