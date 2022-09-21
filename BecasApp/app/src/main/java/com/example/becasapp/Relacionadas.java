package com.example.becasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Relacionadas extends AppCompatActivity {

    RecyclerView recyclerView;
    List<listaBecas> becas;
    String nombre;
    String descripcion;
    String pais;
    String entidad;
    String tipo;
    String enlace;
    String documento;
    private RequestQueue queue;
    ProgressDialog progressDialog;
    Button cancelar, reintentar;
    String correo;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relacionadas);

        obtenerPreferences();

        queue = Volley.newRequestQueue(getApplicationContext());
        correo = preferences.getString("correo", null);

        BottomNavigationView bottomNavigationView = findViewById(R.id.boton_navegar);
        bottomNavigationView.setSelectedItemId(R.id.busquedasRelacionadas);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.busquedasRelacionadas:
                        return true;
                    case R.id.busqueda:
                        startActivity(new Intent(getApplicationContext(), Busquedas.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.perfil:
                        startActivity(new Intent(getApplicationContext(), EditarPerfil.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.principal:
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.listaRelacionadasRecycler);
        servicioApiRest(correo);
    }

    public void servicioApiRest(String user){

        progressDialog = new ProgressDialog(Relacionadas.this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setContentView(R.layout.diseno_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        becas = new ArrayList<>();
        String url = "https://trabajo-de-grado-2022.herokuapp.com/";
        JSONObject object = new JSONObject();
        try {
            object.put("USUARIO", user);
            object.put("BANDERA", "u");
            object.put("BUSQUEDA", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("BECAS RELACIONADAS");

                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject beca = jsonArray.getJSONObject(i);

                                documento = beca.getString("Documento");
                                nombre = beca.getString("Nombre");
                                descripcion = beca.getString("Descripcion");
                                pais = beca.getString("Lugar");
                                entidad = beca.getString("Entidad que ofrece la beca");
                                tipo = beca.getString("Tipo de estudio");
                                enlace = beca.getString("Enlace de la convocatoria");

                                becas.add(new listaBecas(documento, nombre, descripcion, pais, entidad, tipo, enlace));
                            }

                            adaptador Adapter = new adaptador(becas, getApplicationContext(), new adaptador.OnItemClickListener() {
                                @Override
                                public void onItemClick(listaBecas item) {
                                    pasarDatos(item);
                                }
                            });
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(Adapter);

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
    }

    public void obtenerPreferences(){
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }

    public void pasarDatos(listaBecas item){
        Intent intent = new Intent(getApplicationContext(), infoBeca.class);
        intent.putExtra("datos", item);
        startActivity(intent);
    }

    public void dialogo_conexion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Relacionadas.this);
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
                servicioApiRest(correo);
                dialog.dismiss();
            }
        });
    }
}