package com.example.becasapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class infoBeca extends AppCompatActivity {

    TextView nombre, descripcion, lugar, entidad, tipo;
    TextView textView;
    Button enlace;
    String enlaceBeca;
    String documento;
    String correo;
    RatingBar ratingBar;
    SharedPreferences preferences;
    SharedPreferences preferences2;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_beca);

        obtenerPreferences();
        obtenerPreferences2();
        queue = Volley.newRequestQueue(getApplicationContext());

        listaBecas datos = (listaBecas) getIntent().getSerializableExtra("datos");

        correo = preferences.getString("correo", null);

        nombre = findViewById(R.id.nombreBeca);
        descripcion = findViewById(R.id.descripcionBeca);
        lugar = findViewById(R.id.lugarBeca);
        entidad = findViewById(R.id.entidadBeca);
        tipo = findViewById(R.id.tipoBeca);
        enlace = findViewById(R.id.enlaceBeca);
        ratingBar = findViewById(R.id.rating);
        textView = findViewById(R.id.agradecimiento);

        enlaceBeca = datos.getEnlace();
        documento = datos.getDocumento();

        nombre.setText(datos.getNombre());
        descripcion.setText(datos.getDescripcion());
        lugar.setText(datos.getPais());
        entidad.setText(datos.getEntidad());
        tipo.setText(datos.getTipo());

        if (preferences2.contains(datos.getNombre())){
            ratingBar.setRating(preferences2.getInt(datos.getNombre(), 0));
            textView.setVisibility(View.VISIBLE);
        }

        enlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(enlaceBeca));
                startActivity(intent);
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                calificar(Math.round(rating));
            }
        });
    }

    public void obtenerPreferences(){
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
    }

    public void obtenerPreferences2(){
        preferences2 = getSharedPreferences("Preferences2", MODE_PRIVATE);
    }

    public void calificar(int rating){

        String url = "https://trabajo-de-grado-2022.herokuapp.com/addrating";
        JSONObject object = new JSONObject();
        try {
            object.put("CORREO", correo);
            object.put("DOCUMENTO", documento);
            object.put("RATING", rating);
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
                                textView.setVisibility(View.VISIBLE);
                            }

                            SharedPreferences.Editor editor = preferences2.edit();
                            editor.putInt(nombre.getText().toString(), rating);
                            editor.commit();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(jsonObjectRequest);
    }
}