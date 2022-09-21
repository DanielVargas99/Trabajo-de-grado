package com.example.becasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class EditarPerfil extends AppCompatActivity {

    Button cerrar;
    Button eliminar;
    Button modificarP;
    Button modificarC;
    Button modificarI;
    SharedPreferences preferences;
    ProgressDialog progressDialog;
    String name, subname, correo;
    private RequestQueue queue;
    Button cancelar, reintentar;
    TextView nombres, apellidos, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        obtenerPreferences();
        queue = Volley.newRequestQueue(getApplicationContext());

        name = preferences.getString("nombre", null);
        subname = preferences.getString("apellido", null);
        correo = preferences.getString("correo", null);

        cerrar = findViewById(R.id.cerrar);
        eliminar = findViewById(R.id.eliminar);
        modificarP = findViewById(R.id.infoP);
        modificarC = findViewById(R.id.infoC);
        modificarI = findViewById(R.id.infoI);
        nombres = findViewById(R.id.nombreUsuario);
        apellidos = findViewById(R.id.apellidoUsuario);
        email = findViewById(R.id.correoUsuario);

        nombres.setText(name);
        apellidos.setText(subname);
        email.setText(correo);

        BottomNavigationView bottomNavigationView = findViewById(R.id.boton_navegar);
        bottomNavigationView.setSelectedItemId(R.id.perfil);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.busquedasRelacionadas:
                        startActivity(new Intent(getApplicationContext(), Relacionadas.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.perfil:
                        return true;
                    case R.id.busqueda:
                        startActivity(new Intent(getApplicationContext(), Busquedas.class));
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

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().clear().apply();
                Intent intent = new Intent(getApplicationContext(), paginaComenzar.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo_seguro();
            }
        });

        modificarP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), infoPersonal.class));
            }
        });

        modificarC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), infoCuenta.class));
            }
        });

        modificarI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), infoIntereses.class));
            }
        });
    }

    public void obtenerPreferences(){
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }

    public void eliminarCuenta(){

        progressDialog = new ProgressDialog(EditarPerfil.this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setContentView(R.layout.dialog_eliminar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String url = "https://trabajo-de-grado-2022.herokuapp.com/eliminar";
        JSONObject object = new JSONObject();
        try {
            object.put("CORREO", correo);
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

                                preferences.edit().clear().apply();

                                progressDialog.dismiss();
                                dialogo_eliminar();

                            } else {
                                dialogo();
                            }

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

    public void dialogo_conexion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarPerfil.this);
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
                eliminarCuenta();
                dialog.dismiss();
            }
        });
    }

    public void dialogo(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(EditarPerfil.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_conexion, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        textView = view.findViewById(R.id.textoDialog);
        textView.setText("No se pudo eliminar la cuenta");

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
                dialog.dismiss();
                eliminarCuenta();
            }
        });
    }

    public void dialogo_seguro(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(EditarPerfil.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_conexion, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        textView = view.findViewById(R.id.textoDialog);
        textView.setText("¿Está seguro que desea eliminar su cuenta?");

        cancelar = view.findViewById(R.id.cancelar);
        cancelar.setText("No");
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        reintentar = view.findViewById(R.id.reintentar);
        reintentar.setText("Si");
        reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                eliminarCuenta();
            }
        });
    }

    public void dialogo_eliminar(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(EditarPerfil.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_adicional, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        textView = view.findViewById(R.id.textoDialog2);
        textView.setText("Cuenta eliminada satisfactoriamente");

        reintentar = view.findViewById(R.id.botonEntendido);
        reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}