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
import com.example.cinesound.models.ProvedoresTmdb;

import java.util.List;

public class ProvedorAdapter extends RecyclerView.Adapter<ProvedorAdapter.VH> {

    private static final String IMG = "https://image.tmdb.org/t/p/w92";
    private final List<ProvedoresTmdb.Provedor> lista;

    public ProvedorAdapter(List<ProvedoresTmdb.Provedor> lista) { this.lista = lista; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_platform, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ProvedoresTmdb.Provedor p = lista.get(position);
        h.tvName.setText(p.nome != null ? p.nome : "");
        if (p.logoPath != null && !p.logoPath.isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(IMG + p.logoPath)
                    .placeholder(R.color.bg_tertiary)
                    .into(h.ivLogo);
        } else {
            h.ivLogo.setImageResource(R.color.bg_tertiary);
        }
    }

    @Override public int getItemCount() { return lista == null ? 0 : lista.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivLogo; TextView tvName;
        VH(View v) {
            super(v);
            ivLogo = v.findViewById(R.id.iv_platform_logo);
            tvName = v.findViewById(R.id.tv_platform_name);
        }
    }
}