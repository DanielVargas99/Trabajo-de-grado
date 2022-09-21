package com.example.becasapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class adaptador extends RecyclerView.Adapter<adaptador.ViewHolder> {

    private List<listaBecas> becas;
    private LayoutInflater inflater;
    private Context context;
    final adaptador.OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(listaBecas item);
    }

    public adaptador(List<listaBecas> lista, Context context, adaptador.OnItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.becas = lista;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return becas.size();
    }

    @Override
    public adaptador.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.formato, null);
        return new adaptador.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final adaptador.ViewHolder holder, final int position) {
        holder.bindData(becas.get(position));
    }

    public void setItems(List<listaBecas> items){
        becas = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView campo1, campo2, campo3;
        ImageView imagen;

        ViewHolder(View itemView){
            super(itemView);

            campo1 = itemView.findViewById(R.id.campoNombre);
            campo2 = itemView.findViewById(R.id.campoPais);
            campo3 = itemView.findViewById(R.id.campoTipo);
            imagen = itemView.findViewById(R.id.icono);
        }

        void bindData(final listaBecas item){

            Random random = new Random();

            campo1.setText(item.getNombre());
            campo2.setText(item.getPais());
            campo3.setText(item.getTipo());
            int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
            imagen.setBackgroundTintList(ColorStateList.valueOf(color));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }
}
