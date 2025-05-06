package com.example.myapplication.adapters;

import android.content.Context;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.models.Plat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class PlatAdapter extends RecyclerView.Adapter<PlatAdapter.PlatViewHolder> {

    public interface OnPlatClickListener {
        void onPlatClick(Plat plat);
    }

    private final Context context;
    private List<Plat> plats;
    private final OnPlatClickListener listener;

    public PlatAdapter(Context context, List<Plat> plats, OnPlatClickListener listener) {
        this.context = context;
        this.plats = plats;
        this.listener = listener;
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
        holder.heurePlat.setText(plat.horaire);
        holder.prixPlat.setText(plat.prix + " €");
        holder.nomUser.setText(plat.userPrenom != null ? plat.userPrenom : "Utilisateur");
        holder.appartementUser.setText(plat.userAppartement != null ? " | " + plat.userAppartement : "");
        Glide.with(context).load(plat.imageUrl).into(holder.imagePlat);

        // 🔴 Met à jour l'icône du cœur
        holder.likeButton.setImageResource(
                plat.isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline
        );

        // ❤️ Gère le clic sur le cœur
        holder.likeButton.setOnClickListener(v -> {
            plat.isLiked = !plat.isLiked;
            notifyItemChanged(holder.getAdapterPosition());

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore favorisRef = FirebaseFirestore.getInstance();

            if (plat.isLiked) {
                favorisRef.collection("users").document(userId)
                        .collection("favoris").document(plat.documentId)
                        .set(new HashMap<>());
                Toast.makeText(context, "Ajouté aux favoris", Toast.LENGTH_SHORT).show();
            } else {
                favorisRef.collection("users").document(userId)
                        .collection("favoris").document(plat.documentId)
                        .delete();
                Toast.makeText(context, "Retiré des favoris", Toast.LENGTH_SHORT).show();
            }
        });

        // Supprimer
        holder.deleteButton.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("plats")
                    .document(plat.documentId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        plats.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        Toast.makeText(context, "Plat supprimé", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Erreur de suppression", Toast.LENGTH_SHORT).show()
                    );
        });

        holder.itemView.setOnClickListener(v -> listener.onPlatClick(plat));
    }


    @Override
    public int getItemCount() {
        return plats.size();
    }

    public void updateData(List<Plat> newPlats) {
        this.plats = newPlats;
        notifyDataSetChanged();
    }

    static class PlatViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePlat;
        TextView nomPlat, heurePlat, prixPlat, nomUser, appartementUser;
        ImageButton likeButton, deleteButton;

        PlatViewHolder(View itemView) {
            super(itemView);
            imagePlat = itemView.findViewById(R.id.imagePlat);
            nomPlat = itemView.findViewById(R.id.nomPlat);
            heurePlat = itemView.findViewById(R.id.heurePlat);
            prixPlat = itemView.findViewById(R.id.prixPlat);
            nomUser = itemView.findViewById(R.id.nomUser);
            appartementUser = itemView.findViewById(R.id.appartementUser);
            likeButton = itemView.findViewById(R.id.likeButton);
            deleteButton = itemView.findViewById(R.id.deleteButton); // ← Assure-toi que ce bouton est bien défini dans le XML
        }
    }
}
