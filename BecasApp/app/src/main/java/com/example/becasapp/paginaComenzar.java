package com.example.becasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class paginaComenzar extends AppCompatActivity {

    VideoView videoView;
    Button comenzar;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_comenzar);

        iniciarPreferences();
        validarSesion();

        videoView = findViewById(R.id.video);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_comenzar));
        videoView.start();

        comenzar = findViewById(R.id.comenzar);
        comenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    public void iniciarPreferences(){
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }

    public void validarSesion(){
        String correo = preferences.getString("correo", null);
        String contraseña = preferences.getString("contraseña", null);

        if (correo != null && contraseña != null){
            Intent intent = new Intent(getApplicationContext(), HomePage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}