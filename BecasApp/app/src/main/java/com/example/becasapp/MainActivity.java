package com.example.becasapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button iniciosesion;
    Button registrarse;
    TextView contraseña;
    EditText email;
    EditText password;
    private RequestQueue queue;
    ProgressDialog progressDialog;
    String nombre;
    String apellido;
    int edad;
    String sexo;
    int estrato;
    String correo;
    String Contraseña;
    String interes1;
    String interes2;
    String interes3;
    String interes4;
    String interes5;
    SharedPreferences preferences;
    TextInputLayout user , pass;
    Button cancelar, reintentar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciarPreferences();
        validarSesion();

        queue = Volley.newRequestQueue(getApplicationContext());

        iniciosesion = findViewById(R.id.inicioS);
        registrarse = findViewById(R.id.crear);
        contraseña = findViewById(R.id.boton_contraseña);
        email = findViewById(R.id.Usuario);
        password = findViewById(R.id.Contraseña);
        user = findViewById(R.id.user);
        pass = findViewById(R.id.password);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { user.setError(null); }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { pass.setError(null); }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        iniciosesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistroUsuario.class));
            }
        });

        contraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), set_password.class));
            }
        });
    }

    public void validarDatos(){

        correo = email.getText().toString();
        Contraseña = password.getText().toString();

        if (correo.isEmpty() && Contraseña.isEmpty()){
            user.setError("Debe rellenar todos los campos");
            pass.setError("Debe rellenar todos los campos");
        } else if (correo.isEmpty()){
            user.setError("Este campo es obligatorio");
        } else if (Contraseña.isEmpty()){
            pass.setError("Este campo es obligatorio");
        } else if (Patterns.EMAIL_ADDRESS.matcher(correo).matches()){

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setContentView(R.layout.dialog_inicio);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            String url = "https://trabajo-de-grado-2022.herokuapp.com/sesion";
            JSONObject object = new JSONObject();
            try {
                object.put("USUARIO", correo);
                object.put("CONTRASEÑA", Contraseña);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                String respuesta = response.getString("RESPUESTA");

                                if (respuesta.equals("s")){
                                    JSONObject usuario = response.getJSONObject("DATOS");

                                    nombre = usuario.getString("nombre");
                                    apellido = usuario.getString("apellido");
                                    edad = usuario.getInt("edad");
                                    sexo = usuario.getString("sexo");
                                    estrato = usuario.getInt("estrato");
                                    interes1 = usuario.getString("interes1");
                                    interes2 = usuario.getString("interes2");
                                    interes3 = usuario.getString("interes3");
                                    interes4 = usuario.getString("interes4");
                                    interes5 = usuario.getString("interes5");

                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("nombre", nombre);
                                    editor.putString("apellido", apellido);
                                    editor.putInt("edad", edad);
                                    editor.putString("sexo", sexo);
                                    editor.putInt("estrato", estrato);
                                    editor.putString("correo", correo);
                                    editor.putString("contraseña", Contraseña);
                                    editor.putString("interes1", interes1);
                                    editor.putString("interes2", interes2);
                                    editor.putString("interes3", interes3);
                                    editor.putString("interes4", interes4);
                                    editor.putString("interes5", interes5);
                                    editor.commit();

                                    Intent intent = new Intent(getApplicationContext(), HomePage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                } else {
                                        dialogo_cuenta();
                                }

                                progressDialog.dismiss();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    dialogo_conexion();
                }
            });

            queue.add(jsonObjectRequest);
        } else {
            user.setError("Correo no valido");
        }
    }

    public void iniciarPreferences(){
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }

    public void validarSesion(){
        String correo = preferences.getString("correo", null);
        String contraseña = preferences.getString("contraseña", null);

        if (correo != null && contraseña != null){
            Intent intent = new Intent(getApplicationContext(), HomePage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    public void dialogo_conexion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_conexion, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        cancelar = view.findViewById(R.id.cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        reintentar = view.findViewById(R.id.reintentar);
        reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
                dialog.dismiss();
            }
        });
    }

    public void dialogo_cuenta(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_adicional, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        textView = view.findViewById(R.id.textoDialog2);
        textView.setText("Esta cuenta no existe");

        reintentar = view.findViewById(R.id.botonEntendido);
        reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}