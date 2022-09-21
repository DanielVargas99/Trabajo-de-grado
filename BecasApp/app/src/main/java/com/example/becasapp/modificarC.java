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
import android.widget.RelativeLayout;
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

public class modificarC extends AppCompatActivity {

    Button actualizar;
    EditText correo;
    EditText contraseña;
    EditText recontraseña;
    String email;
    String pass;
    String repass;
    String emailP;
    String passP;
    ProgressDialog progressDialog;
    private RequestQueue queue;
    TextInputLayout c, ct, rc;
    TextView textView;
    SharedPreferences preferences;
    Button cancelar, reintentar;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        obtenerPreferences();

        layout = findViewById(R.id.progress_layout);
        layout.setVisibility(View.INVISIBLE);

        queue = Volley.newRequestQueue(getApplicationContext());

        emailP = preferences.getString("correo", null);
        passP = preferences.getString("contraseña", null);

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

        correo.setText(emailP);
        contraseña.setText(passP);
        recontraseña.setText(passP);

        textView = findViewById(R.id.Registro2);
        textView.setText("Modificar datos");

        actualizar = findViewById(R.id.siguiente2);
        actualizar.setText("Actualizar");
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarDatosC();
            }
        });
    }

    public void obtenerPreferences(){
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }

    public void cambiarDatosC(){

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

                progressDialog = new ProgressDialog(modificarC.this);
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setContentView(R.layout.dialog_actualizar);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                String url = "https://trabajo-de-grado-2022.herokuapp.com/modificarc";
                JSONObject object = new JSONObject();
                try {
                    object.put("CORREO", emailP);
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

                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("correo", email);
                                        editor.putString("contraseña", pass);
                                        editor.commit();

                                        progressDialog.dismiss();
                                        dialogo_actualizado();

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

            } else {
                ct.setError("Las contraseñas no coinciden");
                rc.setError("Las contraseñas no coinciden");
            }
        } else {
            c.setError("El correo no es valido");
        }
    }

    public void dialogo_conexion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(modificarC.this);
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
                cambiarDatosC();
                dialog.dismiss();
            }
        });
    }

    public void dialogo(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(modificarC.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_conexion, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        textView = view.findViewById(R.id.textoDialog);
        textView.setText("No se pudo actualizar los datos");

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
                cambiarDatosC();
            }
        });
    }

    public void dialogo_actualizado(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(modificarC.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_adicional, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        textView = view.findViewById(R.id.textoDialog2);
        textView.setText("Datos actualizados satisfactoriamente");

        reintentar = view.findViewById(R.id.botonEntendido);
        reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), EditarPerfil.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}