package com.example.becasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class infoPersonal extends AppCompatActivity {

    SharedPreferences preferences;
    String nombreP;
    String apellidoP;
    int edadP;
    String sexoP;
    int estratoP;
    TextView nom;
    TextView ape;
    TextView ed;
    TextView sex;
    TextView est;
    Button editar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_personal);

        obtenerPreferences();

        nombreP = preferences.getString("nombre", null);
        apellidoP = preferences.getString("apellido", null);
        edadP = preferences.getInt("edad", 0);
        estratoP = preferences.getInt("estrato", 0);
        sexoP = preferences.getString("sexo", null);

        nom = findViewById(R.id.nombreUser);
        ape = findViewById(R.id.apellidoUser);
        ed = findViewById(R.id.edadUser);
        sex = findViewById(R.id.sexoUser);
        est = findViewById(R.id.estratoUser);

        nom.setText(nombreP);
        ape.setText(apellidoP);
        ed.setText(Integer.toString(edadP));
        sex.setText(sexoP);
        est.setText(Integer.toString(estratoP));

        editar = findViewById(R.id.editarP);
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), modificarP.class));
            }
        });
    }

    public void obtenerPreferences(){
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }
}