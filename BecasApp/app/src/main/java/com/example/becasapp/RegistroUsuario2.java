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

public class RegistroUsuario2 extends AppCompatActivity {

    Spinner spinner;
    Button siguiente;
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
    String email;
    String pass;
    TextInputLayout n, a, e, sx, sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario2);

        nom = findViewById(R.id.Nombre);
        ape = findViewById(R.id.Apellidos);
        ed = findViewById(R.id.Edad);
        n = findViewById(R.id.nom);
        a = findViewById(R.id.ape);
        e = findViewById(R.id.ed);
        sx = findViewById(R.id.se);
        sp = findViewById(R.id.spi);

        email = getIntent().getStringExtra("correo");
        pass = getIntent().getStringExtra("contraseña");

        spinner = findViewById(R.id.spinner);
        se = findViewById(R.id.sex);
        siguiente = findViewById(R.id.siguiente1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.estratos, R.layout.texto_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });
    }

    public void validarDatos(){

        if (nom.getText().toString().equals("") && ape.getText().toString().equals("") &&
                ed.getText().toString().equals("") && sexo.equals("Seleccione su sexo") &&
                est.equals("Seleccione su estrato")){

            n.setError("Debe rellenar todos los campos");
            a.setError("Debe rellenar todos los campos");
            e.setError("Debe rellenar todos los campos");
            sx.setError("Debe seleccionar su sexo");
            sp.setError("Debe seleccionar su estrato");
        }

        if (nom.getText().toString().equals("")){ n.setError("Debe rellenar este campo"); }

        if (ape.getText().toString().equals("")){ a.setError("Debe rellenar este campo"); }

        if (ed.getText().toString().equals("")){ e.setError("Debe rellenar este campo"); }

        if (sexo.equals("Seleccione su sexo")){ sx.setError("Debe seleccionar su sexo"); }

        if (est.equals("Seleccione su estrato")){ sp.setError("Debe seleccionar su estrato"); }

        if (!(nom.getText().toString().equals("") || ape.getText().toString().equals("") ||
                ed.getText().toString().equals("") || sexo.equals("Seleccione su sexo") ||
                est.equals("Seleccione su estrato"))){

            nombre = nom.getText().toString();
            apellido = ape.getText().toString();
            edad = Integer.parseInt(ed.getText().toString());
            estrato = Integer.parseInt(est);

            Intent intent = new Intent(getApplicationContext(), RegistroIntereses.class);
            intent.putExtra("nombre", nombre);
            intent.putExtra("apellido", apellido);
            intent.putExtra("edad", edad);
            intent.putExtra("sexo", sexo);
            intent.putExtra("estrato", estrato);
            intent.putExtra("correo", email);
            intent.putExtra("contraseña", pass);

            startActivity(intent);
        }
    }
}