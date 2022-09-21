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
import android.widget.RelativeLayout;
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

public class modificarI extends AppCompatActivity {

    String email;
    GridLayout layout;
    EditText int1;
    EditText int2;
    EditText int3;
    EditText int4;
    EditText int5;
    String interes1P;
    String interes2P;
    String interes3P;
    String interes4P;
    String interes5P;
    String interes1;
    String interes2;
    String interes3;
    String interes4;
    String interes5;
    Button actualizar;
    ProgressDialog progressDialog;
    private RequestQueue queue;
    SharedPreferences preferences;
    TextView textView;
    TextView textView2;
    Button cancelar, reintentar;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_intereses);

        obtenerPreferences();

        relativeLayout = findViewById(R.id.progress_layout3);
        relativeLayout.setVisibility(View.INVISIBLE);

        queue = Volley.newRequestQueue(getApplicationContext());

        email = preferences.getString("correo", null);
        interes1P = preferences.getString("interes1", null);
        interes2P = preferences.getString("interes2", null);
        interes3P = preferences.getString("interes3", null);
        interes4P = preferences.getString("interes4", null);
        interes5P = preferences.getString("interes5", null);

        layout = findViewById(R.id.layoutIntereses);
        int1 = findViewById(R.id.campoint1);
        int2 = findViewById(R.id.campoint2);
        int3 = findViewById(R.id.campoint3);
        int4 = findViewById(R.id.campoint4);
        int5 = findViewById(R.id.campoint5);
        textView = findViewById(R.id.intereses);
        textView2 = findViewById(R.id.generar);

        textView.setText("Modificar intereses");
        textView2.setText("Actualizar intereses");

        interes1 = interes1P;
        interes2 = interes2P;
        interes3 = interes3P;
        interes4 = interes4P;
        interes5 = interes5P;

        organizar(layout);

        actualizar = findViewById(R.id.finalizar);
        actualizar.setText("Actualizar");

        obtenerElemento(layout);

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarDatos();
            }
        });
    }

    public void obtenerPreferences(){
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }

    public void organizar(GridLayout gridLayout){

        for (int i = 0; i < gridLayout.getChildCount(); i++){

            CardView cardView = (CardView) gridLayout.getChildAt(i);
            LinearLayout cardlayout = (LinearLayout) cardView.getChildAt(1);
            TextView layoutText = (TextView) cardlayout.getChildAt(0);

            if (interes1P.equals(layoutText.getText().toString())){
                cardlayout.setBackgroundColor(Color.parseColor("#7FE6E5EE"));
                int1.setText(interes1P);
                int1.setEnabled(false);
            } else if (int1.getText().toString().equals("")){
                int1.setText(interes1P);
            } else if (interes2P.equals(layoutText.getText().toString())){
                cardlayout.setBackgroundColor(Color.parseColor("#7FE6E5EE"));
                int2.setText(interes2P);
                int2.setEnabled(false);
            } else if (int2.getText().toString().equals("")){
                int2.setText(interes2P);
            } else if (interes3P.equals(layoutText.getText().toString())){
                cardlayout.setBackgroundColor(Color.parseColor("#7FE6E5EE"));
                int3.setText(interes3P);
                int3.setEnabled(false);
            } else if (int3.getText().toString().equals("")){
                int3.setText(interes3P);
            } else if (interes4P.equals(layoutText.getText().toString())){
                cardlayout.setBackgroundColor(Color.parseColor("#7FE6E5EE"));
                int4.setText(interes4P);
                int4.setEnabled(false);
            } else if (int4.getText().toString().equals("")){
                int4.setText(interes4P);
            } else if (interes5P.equals(layoutText.getText().toString())){
                cardlayout.setBackgroundColor(Color.parseColor("#7FE6E5EE"));
                int5.setText(interes5P);
                int5.setEnabled(false);
            } else if (int5.getText().toString().equals("")){
                int5.setText(interes5P);
            }
        }
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

    public void registrarDatos(){

        interes1 = int1.getText().toString();
        interes2 = int2.getText().toString();
        interes3 = int3.getText().toString();
        interes4 = int4.getText().toString();
        interes5 = int5.getText().toString();

        if (interes1.isEmpty() || interes2.isEmpty() || interes3.isEmpty()){
            dialogo();
        } else {

            progressDialog = new ProgressDialog(modificarI.this);
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setContentView(R.layout.dialog_actualizar);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            String url = "https://trabajo-de-grado-2022.herokuapp.com/modificari";
            JSONObject object = new JSONObject();
            try {
                object.put("CORREO", email);
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

                                if (respuesta.equals("s")) {

                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("interes1", interes1);
                                    editor.putString("interes2", interes2);
                                    editor.putString("interes3", interes3);
                                    editor.putString("interes4", interes4);
                                    editor.putString("interes5", interes5);
                                    editor.commit();

                                    progressDialog.dismiss();
                                    dialogo_actualizado();

                                } else {
                                    progressDialog.dismiss();
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
                    dialogo_conexion();
                }
            });

            queue.add(jsonObjectRequest);
        }
    }

    public void dialogo_conexion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(modificarI.this);
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
                registrarDatos();
                dialog.dismiss();
            }
        });
    }

    public void dialogo(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(modificarI.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_adicional, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        textView = view.findViewById(R.id.textoDialog2);
        textView.setText("Debe escoger minimo 3 temas de interes");

        reintentar = view.findViewById(R.id.botonEntendido);
        reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void dialogo_error(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(modificarI.this);
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
                registrarDatos();
            }
        });
    }

    public void dialogo_actualizado(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(modificarI.this);
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
        reintentar.setText("Entendido");
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