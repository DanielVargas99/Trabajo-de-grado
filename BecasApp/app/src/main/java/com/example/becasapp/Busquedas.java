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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Busquedas extends AppCompatActivity {

    RecyclerView recyclerView;
    List<listaBecas> becas;
    String nombre;
    String descripcion;
    String pais;
    String entidad;
    String tipo;
    String enlace;
    String documento;
    Button buscar;
    EditText busqueda;
    String terminos;
    private RequestQueue queue;
    ProgressDialog progressDialog;
    Button cancelar, reintentar;
    String correo;
    SharedPreferences preferences;
    TextInputLayout b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busquedas);

        iniciarPreferences();

        queue = Volley.newRequestQueue(getApplicationContext());
        correo = preferences.getString("correo", null);
        b = findViewById(R.id.busq);

        BottomNavigationView bottomNavigationView = findViewById(R.id.boton_navegar);
        bottomNavigationView.setSelectedItemId(R.id.busqueda);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.busquedasRelacionadas:
                        startActivity(new Intent(getApplicationContext(), Relacionadas.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.principal:
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.perfil:
                        startActivity(new Intent(getApplicationContext(), EditarPerfil.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.busqueda:
                        return true;
                }
                return false;
            }
        });

        busqueda = findViewById(R.id.busquedaUser);

        busqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                b.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buscar = findViewById(R.id.buscar);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminos = busqueda.getText().toString();

                if (terminos.equals("")){
                    b.setError("Debe insertar una busqueda");
                } else {
                    servicioApiRest(correo, terminos);
                }
            }
        });

        recyclerView = findViewById(R.id.listaBusquedaRecycler);
    }

    public void servicioApiRest(String user, String busqueda){

        progressDialog = new ProgressDialog(Busquedas.this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setContentView(R.layout.diseno_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        b.setError(null);

        becas = new ArrayList<>();
        String url = "https://trabajo-de-grado-2022.herokuapp.com/";
        JSONObject object = new JSONObject();
        try {
            object.put("USUARIO", user);
            object.put("BANDERA", "b");
            object.put("BUSQUEDA", busqueda);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("BECAS");

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

    public void iniciarPreferences(){
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }

    public void pasarDatos(listaBecas item){
        Intent intent = new Intent(getApplicationContext(), infoBeca.class);
        intent.putExtra("datos", item);
        startActivity(intent);
    }

    public void dialogo_conexion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Busquedas.this);
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
                servicioApiRest(correo, terminos);
                dialog.dismiss();
            }
        });
    }
}