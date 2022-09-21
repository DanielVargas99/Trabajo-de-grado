package com.example.becasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class infoIntereses extends AppCompatActivity {

    SharedPreferences preferences;
    String int1P;
    String int2P;
    String int3P;
    String int4P;
    String int5P;
    TextView interes1;
    TextView interes2;
    TextView interes3;
    TextView interes4;
    TextView interes5;
    Button editar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_intereses);

        obtenerPreferences();

        int1P = preferences.getString("interes1", null);
        int2P = preferences.getString("interes2", null);
        int3P = preferences.getString("interes3", null);
        int4P = preferences.getString("interes4", null);
        int5P = preferences.getString("interes5", null);

        interes1 = findViewById(R.id.int1User);
        interes2 = findViewById(R.id.int2User);
        interes3 = findViewById(R.id.int3User);
        interes4 = findViewById(R.id.int4User);
        interes5 = findViewById(R.id.int5User);

        interes1.setText(int1P);
        interes2.setText(int2P);
        interes3.setText(int3P);
        interes4.setText(int4P);
        interes5.setText(int5P);

        editar = findViewById(R.id.editarI);
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), modificarI.class));
            }
        });
    }

    public void obtenerPreferences(){
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }
}