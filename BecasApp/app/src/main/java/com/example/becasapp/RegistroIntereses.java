package com.example.becasapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegistroIntereses extends AppCompatActivity {

    String nombre;
    String apellido;
    int edad;
    String sexo;
    int estrato;
    String email;
    String pass;
    GridLayout layout;
    EditText int1;
    EditText int2;
    EditText int3;
    EditText int4;
    EditText int5;
    String interes1;
    String interes2;
    String interes3;
    String interes4;
    String interes5;
    Button finalizar;
    ProgressDialog progressDialog;
    private RequestQueue queue;
    Button cancelar, reintentar;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_intereses);

        queue = Volley.newRequestQueue(getApplicationContext());
        iniciarPreferences();

        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");
        edad = getIntent().getIntExtra("edad" , 0);
        sexo = getIntent().getStringExtra("sexo");
        estrato = getIntent().getIntExtra("estrato", 0);
        email = getIntent().getStringExtra("correo");
        pass = getIntent().getStringExtra("contraseña");

        layout = findViewById(R.id.layoutIntereses);
        int1 = findViewById(R.id.campoint1);
        int2 = findViewById(R.id.campoint2);
        int3 = findViewById(R.id.campoint3);
        int4 = findViewById(R.id.campoint4);
        int5 = findViewById(R.id.campoint5);

        obtenerElemento(layout);

        finalizar = findViewById(R.id.finalizar);
        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarDatos();
            }
        });
    }

    public void obtenerElemento(GridLayout gridLayout){

        for (int i = 0; i < gridLayout.getChildCount(); i++){

            CardView cardView = (CardView) gridLayout.getChildAt(i);
            LinearLayout cardlayout = (LinearLayout) cardView.getChildAt(1);
            TextView layoutText = (TextView) cardlayout.getChildAt(0);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (int1.getText().toString().equals(layoutText.getText().toString())) {
                        cardlayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                        int1.setText("");
                        int1.setEnabled(true);
                    } else if (int2.getText().toString().equals(layoutText.getText().toString())){
                        cardlayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                        int2.setText("");
                        int2.setEnabled(true);
                    } else if (int3.getText().toString().equals(layoutText.getText().toString())){
                        cardlayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                        int3.setText("");
                        int3.setEnabled(true);
                    } else if (int4.getText().toString().equals(layoutText.getText().toString())){
                        cardlayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                        int4.setText("");
                        int4.setEnabled(true);
                    } else if (int5.getText().toString().equals(layoutText.getText().toString())){
                        cardlayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                        int5.setText("");
                        int5.setEnabled(true);
                    } else if (int1.getText().toString().equals("")) {
                        cardlayout.setBackgroundColor(Color.parseColor("#7FE6E5EE"));
                        int1.setText(layoutText.getText().toString());
                        int1.setEnabled(false);
                    } else if (int2.getText().toString().equals("")){
                        cardlayout.setBackgroundColor(Color.parseColor("#7FE6E5EE"));
                        int2.setText(layoutText.getText().toString());
                        int2.setEnabled(false);
                    } else if (int3.getText().toString().equals("")){
                        cardlayout.setBackgroundColor(Color.parseColor("#7FE6E5EE"));
                        int3.setText(layoutText.getText().toString());
                        int3.setEnabled(false);
                    } else if (int4.getText().toString().equals("")){
                        cardlayout.setBackgroundColor(Color.parseColor("#7FE6E5EE"));
                        int4.setText(layoutText.getText().toString());
                        int4.setEnabled(false);
                    } else if (int5.getText().toString().equals("")){
                        cardlayout.setBackgroundColor(Color.parseColor("#7FE6E5EE"));
                        int5.setText(layoutText.getText().toString());
                        int5.setEnabled(false);
                    }
                }
            });
        }
    }

    public void iniciarPreferences(){
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }

    public void registrarDatos(){

        interes1 = int1.getText().toString();
        interes2 = int2.getText().toString();
        interes3 = int3.getText().toString();
        interes4 = int4.getText().toString();
        interes5 = int5.getText().toString();

        if (interes1.isEmpty() || interes2.isEmpty() || interes3.isEmpty()){
            dialogo();
        } else {

            progressDialog = new ProgressDialog(RegistroIntereses.this);
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setContentView(R.layout.dialog_registro);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            String url = "https://trabajo-de-grado-2022.herokuapp.com/registrar";
            JSONObject object = new JSONObject();
            try {
                object.put("NOMBRES", nombre);
                object.put("APELLIDOS", apellido);
                object.put("EDAD", edad);
                object.put("SEXO", sexo);
                object.put("ESTRATO", estrato);
                object.put("CORREO", email);
                object.put("CONTRASEÑA", pass);
                object.put("INTERES1", interes1);
                object.put("INTERES2", interes2);
                object.put("INTERES3", interes3);
                object.put("INTERES4", interes4);
                object.put("INTERES5", interes5);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                String respuesta = response.getString("RESPUESTA");

                                if (respuesta.equals("n")){
                                    progressDialog.dismiss();
                                    dialogo_error();
                                } else {

                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("nombre", nombre);
                                    editor.putString("apellido", apellido);
                                    editor.putInt("edad", edad);
                                    editor.putString("sexo", sexo);
                                    editor.putInt("estrato", estrato);
                                    editor.putString("correo", email);
                                    editor.putString("contraseña", pass);
                                    editor.putString("interes1", interes1);
                                    editor.putString("interes2", interes2);
                                    editor.putString("interes3", interes3);
                                    editor.putString("interes4", interes4);
                                    editor.putString("interes5", interes5);
                                    editor.commit();

                                    progressDialog.dismiss();
                                    dialogo_actualizado();

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
    }

    public void dialogo_conexion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroIntereses.this);
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
                dialog.dismiss();
                registrarDatos();
            }
        });
    }

    public void dialogo(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroIntereses.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_conexion, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        textView = view.findViewById(R.id.textoDialog);
        textView.setText("Debe escoger minimo 3 temas de interes");

        cancelar = view.findViewById(R.id.cancelar);
        cancelar.setVisibility(View.INVISIBLE);
        cancelar.setEnabled(false);

        reintentar = view.findViewById(R.id.reintentar);
        reintentar.setText("Entendido");
        reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void dialogo_error(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroIntereses.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_conexion, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        textView = view.findViewById(R.id.textoDialog);
        textView.setText("No se pudo crear la cuenta");

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
                registrarDatos();
            }
        });
    }

    public void dialogo_actualizado(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroIntereses.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_adicional, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        textView = view.findViewById(R.id.textoDialog2);
        textView.setText("Cuenta creada satisfactoriamente");

        reintentar = view.findViewById(R.id.botonEntendido);
        reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}