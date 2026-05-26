package com.example.cinesound.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinesound.R;
import com.example.cinesound.models.TituloItem;

import java.util.List;

public class EmAltaAdapter extends RecyclerView.Adapter<EmAltaAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(TituloItem item);
    }

    private List<TituloItem> lista;
    private OnItemClickListener listener;

    public EmAltaAdapter(List<TituloItem> lista, OnItemClickListener listener) {
        this.lista    = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trending_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TituloItem item = lista.get(position);

        holder.tvRank.setText("#" + (position + 1));
        holder.tvTitle.setText(item.titulo);
        holder.tvRating.setText(String.format("%.1f", item.notaTmdb));
        holder.tvTag.setText("Tendências");

        // gênero e ano
        holder.tvGenreYear.setText(item.tipo.equals("filme") ? "Filme" : "Série");

        if (item.poster != null && !item.poster.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.poster)
                    .into(holder.ivPoster);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView  tvRank, tvTitle, tvGenreYear, tvRating, tvTag;
        ImageView ivPoster;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            ivPoster = itemView.findViewById(R.id.iv_poster);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvGenreYear = itemView.findViewById(R.id.tv_genre_year);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvTag = itemView.findViewById(R.id.tv_tag);
        }
    }
}