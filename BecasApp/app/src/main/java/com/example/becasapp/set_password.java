package com.example.becasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class set_password extends AppCompatActivity {

    EditText correo;
    EditText contraseña;
    EditText recontraseña;
    String email;
    String pass;
    String repass;
    Button guardar;
    private RequestQueue queue;
    TextInputLayout c, ct, rc;
    ProgressDialog progressDialog;
    Button cancelar, reintentar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_password);

        queue = Volley.newRequestQueue(getApplicationContext());

        correo = findViewById(R.id.correoC);
        contraseña = findViewById(R.id.contraseñaC);
        recontraseña = findViewById(R.id.recontraseñaC);
        c = findViewById(R.id.corC);
        ct = findViewById(R.id.conC);
        rc = findViewById(R.id.reconC);

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

        guardar = findViewById(R.id.guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
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

                progressDialog = new ProgressDialog(set_password.this);
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
                                        cambiarContraseña();
                                    } else {
                                        progressDialog.dismiss();
                                        c.setError("Este correo no esta asociado a una cuenta");
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

    public void cambiarContraseña(){
        progressDialog = new ProgressDialog(set_password.this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setContentView(R.layout.dialog_actualizar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String url = "https://trabajo-de-grado-2022.herokuapp.com/modificarc";
        JSONObject object = new JSONObject();
        try {
            object.put("CORREO", email);
            object.put("CORREON", email);
            object.put("CONTRASEÑA", pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String respuesta = response.getString("RESPUESTA");

                    if (respuesta.equals("s")) {

                        progressDialog.dismiss();
                        dialogo_actualizado();

                    } else {
                        dialogo_error();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                dialogo_conexion2();
            }
        });

        queue.add(jsonObjectRequest);
    }

    public void dialogo_conexion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(set_password.this);
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

    public void dialogo_conexion2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(set_password.this);
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
                cambiarContraseña();
                dialog.dismiss();
            }
        });
    }

    public void dialogo_error(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(set_password.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_conexion, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        textView = view.findViewById(R.id.textoDialog);
        textView.setText("No se pudo cambiar la contraseña");

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
                cambiarContraseña();
            }
        });
    }

    public void dialogo_actualizado(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(set_password.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_adicional, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        textView = view.findViewById(R.id.textoDialog2);
        textView.setText("Contraseña cambiada satisfactoriamente");

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