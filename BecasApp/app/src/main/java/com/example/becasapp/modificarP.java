package com.example.becasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

public class modificarP extends AppCompatActivity {

    SharedPreferences preferences;
    String correo;
    Spinner spinner;
    EditText nom;
    EditText ape;
    EditText ed;
    Spinner se;
    String est;
    String nombre;
    String apellido;
    int edad;
    String sexo;
    int estrato;
    String nombreP;
    String apellidoP;
    int edadP;
    String sexoP;
    int estratoP;
    TextInputLayout n, a, e, sx, sp;
    ProgressDialog progressDialog;
    private RequestQueue queue;
    TextView textView;
    Button actualizar;
    int aux, aux1;
    Button cancelar, reintentar;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario2);

        obtenerPreferences();

        layout = findViewById(R.id.progress_layout2);
        layout.setVisibility(View.INVISIBLE);

        nom = findViewById(R.id.Nombre);
        ape = findViewById(R.id.Apellidos);
        ed = findViewById(R.id.Edad);
        n = findViewById(R.id.nom);
        a = findViewById(R.id.ape);
        e = findViewById(R.id.ed);
        sx = findViewById(R.id.se);
        sp = findViewById(R.id.spi);

        spinner = findViewById(R.id.spinner);
        se = findViewById(R.id.sex);
        estratoP = preferences.getInt("estrato", 0);
        sexoP = preferences.getString("sexo", null);
        aux = 0;
        aux1 = 0;

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.estratos, R.layout.texto_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (aux == 0){
                    for (int i = 0; i < 7; i++){
                        if (Integer.toString(estratoP).equals(parent.getItemAtPosition(i).toString())){
                            parent.setSelection(i);
                            aux = 1;
                        }
                    }
                }

                sp.setError(null);
                est = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.sexos, R.layout.texto_spinner);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        se.setAdapter(adapter2);

        se.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (aux1 == 0){
                    for (int i = 0; i < 4; i++){
                        if (sexoP.equals(parent.getItemAtPosition(i).toString())){
                            parent.setSelection(i);
                            aux1 = 1;
                        }
                    }
                }

                sx.setError(null);
                sexo = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        nom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { n.setError(null); }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        ape.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { a.setError(null); }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { e.setError(null); }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        queue = Volley.newRequestQueue(getApplicationContext());

        nombreP = preferences.getString("nombre", null);
        apellidoP = preferences.getString("apellido", null);
        edadP = preferences.getInt("edad", 0);
        correo = preferences.getString("correo", null);

        nom.setText(nombreP);
        ape.setText(apellidoP);
        ed.setText(Integer.toString(edadP));

        textView = findViewById(R.id.Registro);
        textView.setText("Modificar datos");

        actualizar = findViewById(R.id.siguiente1);
        actualizar.setText("Actualizar");
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarDatosP();
            }
        });
    }

    public void obtenerPreferences(){
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }

    public void cambiarDatosP() {

        if (nom.getText().toString().equals("") && ape.getText().toString().equals("") &&
                ed.getText().toString().equals("") && sexo.equals("Seleccione su sexo") &&
                est.equals("Seleccione su estrato")) {

            n.setError("Debe rellenar todos los campos");
            a.setError("Debe rellenar todos los campos");
            e.setError("Debe rellenar todos los campos");
            sx.setError("Debe seleccionar su sexo");
            sp.setError("Debe seleccionar su estrato");
        }

        if (nom.getText().toString().equals("")) { n.setError("Debe rellenar este campo"); }

        if (ape.getText().toString().equals("")) { a.setError("Debe rellenar este campo"); }

        if (ed.getText().toString().equals("")) { e.setError("Debe rellenar este campo"); }

        if (sexo.equals("Seleccione su sexo")) { sx.setError("Debe seleccionar su sexo"); }

        if (est.equals("Seleccione su estrato")) { sp.setError("Debe seleccionar su estrato"); }

        if (!(nom.getText().toString().equals("") || ape.getText().toString().equals("") ||
                ed.getText().toString().equals("") || sexo.equals("Seleccione su sexo") ||
                est.equals("Seleccione su estrato"))) {

            nombre = nom.getText().toString();
            apellido = ape.getText().toString();
            edad = Integer.parseInt(ed.getText().toString());
            estrato = Integer.parseInt(est);

            progressDialog = new ProgressDialog(modificarP.this);
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setContentView(R.layout.dialog_actualizar);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            String url = "https://trabajo-de-grado-2022.herokuapp.com/modificarp";
            JSONObject object = new JSONObject();
            try {
                object.put("CORREO", correo);
                object.put("NOMBRES", nombre);
                object.put("APELLIDOS", apellido);
                object.put("EDAD", edad);
                object.put("SEXO", sexo);
                object.put("ESTRATO", estrato);
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
                                    editor.putString("nombre", nombre);
                                    editor.putString("apellido", apellido);
                                    editor.putInt("edad", edad);
                                    editor.putString("sexo", sexo);
                                    editor.putInt("estrato", estrato);
                                    editor.commit();

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
                    dialogo_conexion();
                }
            });

            queue.add(jsonObjectRequest);
        }
    }

    public void dialogo_conexion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(modificarP.this);
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
                cambiarDatosP();
                dialog.dismiss();
            }
        });
    }

    public void dialogo_error(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(modificarP.this);
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
                cambiarDatosP();
            }
        });
    }

    public void dialogo_actualizado(){
        TextView textView;

        AlertDialog.Builder builder = new AlertDialog.Builder(modificarP.this);
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