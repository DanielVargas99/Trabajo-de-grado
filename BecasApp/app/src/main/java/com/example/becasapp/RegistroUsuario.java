package com.example.becasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class RegistroUsuario extends AppCompatActivity {

    Button siguiente;
    EditText correo;
    EditText contraseña;
    EditText recontraseña;
    String email;
    String pass;
    String repass;
    ProgressDialog progressDialog;
    private RequestQueue queue;
    TextInputLayout c, ct, rc;
    Button cancelar, reintentar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        queue = Volley.newRequestQueue(getApplicationContext());

        correo = findViewById(R.id.correo);
        contraseña = findViewById(R.id.contraseña);
        recontraseña = findViewById(R.id.recontraseña);
        c = findViewById(R.id.cor);
        ct = findViewById(R.id.con);
        rc = findViewById(R.id.recon);

        correo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { c.setError(null); }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        contraseña.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { ct.setError(null); }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        recontraseña.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { rc.setError(null); }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        siguiente = findViewById(R.id.siguiente2);

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarDatos();
            }
        });
    }

    public void verificarDatos(){

        email = correo.getText().toString();
        pass = contraseña.getText().toString();
        repass = recontraseña.getText().toString();

        if (email.isEmpty() && pass.isEmpty() && repass.isEmpty()){
            c.setError("Debe rellenar todos los campos");
            ct.setError("Debe rellenar todos los campos");
            rc.setError("Debe rellenar todos los campos");
        } else if (email.isEmpty()){
            c.setError("Debe rellenar este campo");
        } else if (pass.isEmpty() && repass.isEmpty()){
            ct.setError("Debe rellenar este campo");
            rc.setError("Debe rellenar este campo");
        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            if (pass.equals(repass)){

                c.setError(null);
                ct.setError(null);
                rc.setError(null);

                progressDialog = new ProgressDialog(RegistroUsuario.this);
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setContentView(R.layout.dialog_inicio);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                String url = "https://trabajo-de-grado-2022.herokuapp.com/validar";
                JSONObject object = new JSONObject();
                try {
                    object.put("CORREO", email);
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
                                        progressDialog.dismiss();
                                        c.setError("Este correo ya esta asociado a una cuenta");
                                    } else {

                                        progressDialog.dismiss();

                                        Intent intent = new Intent(getApplicationContext(), RegistroUsuario2.class);
                                        intent.putExtra("correo", email);
                                        intent.putExtra("contraseña", pass);

                                        startActivity(intent);
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

            } else {
                ct.setError("Las contraseñas no coinciden");
                rc.setError("Las contraseñas no coinciden");
            }
        } else {
            c.setError("El correo no es valido");
        }
    }

    public void dialogo_conexion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroUsuario.this);
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
                verificarDatos();
                dialog.dismiss();
            }
        });
    }
}