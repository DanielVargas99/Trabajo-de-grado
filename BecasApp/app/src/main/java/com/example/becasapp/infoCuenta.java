package com.example.becasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class infoCuenta extends AppCompatActivity {

    SharedPreferences preferences;
    String correoP;
    String contraseñaP;
    String password;
    TextView email;
    TextView pass;
    Button editar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_cuenta);

        obtenerPreferences();

        correoP = preferences.getString("correo", null);
        contraseñaP = preferences.getString("contraseña", null);
        password = "*";

        email = findViewById(R.id.correoUser);
        pass = findViewById(R.id.passUser);

        for (int i = 1; i < contraseñaP.length(); i++){
            password = password.concat("*");
        }

        email.setText(correoP);
        pass.setText(password);

        editar = findViewById(R.id.editarC);
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), modificarC.class));
            }
        });
    }

    public void obtenerPreferences(){
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }
}