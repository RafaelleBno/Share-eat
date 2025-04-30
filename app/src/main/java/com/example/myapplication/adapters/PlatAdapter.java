package com.example.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.models.Plat;

import java.util.List;

public class PlatAdapter extends RecyclerView.Adapter<PlatAdapter.PlatViewHolder> {

    private final Context context;
    private final List<Plat> plats;

    public PlatAdapter(Context context, List<Plat> plats) {
        this.context = context;
        this.plats = plats;
    }

    @NonNull
    @Override
    public PlatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plat, parent, false);
        return new PlatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlatViewHolder holder, int position) {
        Plat plat = plats.get(position);
        holder.nomPlat.setText(plat.nom);
        holder.prixPlat.setText(plat.prix + " €");
        Glide.with(context).load(plat.imageUrl).into(holder.imagePlat);
    }

    @Override
    public int getItemCount() {
        return plats.size();
    }

    static class PlatViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePlat;
        TextView nomPlat, prixPlat;

        PlatViewHolder(View itemView) {
            super(itemView);
            imagePlat = itemView.findViewById(R.id.imagePlat);
            nomPlat = itemView.findViewById(R.id.nomPlat);
            prixPlat = itemView.findViewById(R.id.prixPlat);
        }
    }
}
