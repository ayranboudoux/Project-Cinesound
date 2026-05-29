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
import com.example.cinesound.models.ElencoTmdb;

import java.util.List;

public class ElencoAdapter extends RecyclerView.Adapter<ElencoAdapter.VH> {

    private static final String IMG = "https://image.tmdb.org/t/p/w185";
    private final List<ElencoTmdb.MembroElenco> lista;

    public ElencoAdapter(List<ElencoTmdb.MembroElenco> lista) { this.lista = lista; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cast_member, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ElencoTmdb.MembroElenco m = lista.get(position);
        h.tvName.setText(m.nome != null ? m.nome : "");
        h.tvChar.setText(m.personagem != null ? m.personagem : "");
        if (m.fotoPath != null && !m.fotoPath.isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(IMG + m.fotoPath)
                    .placeholder(R.color.bg_tertiary)
                    .into(h.ivPhoto);
        } else {
            h.ivPhoto.setImageResource(R.color.bg_tertiary);
        }
    }

    @Override public int getItemCount() { return lista == null ? 0 : lista.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivPhoto; TextView tvName, tvChar;
        VH(View v) {
            super(v);
            ivPhoto = v.findViewById(R.id.iv_cast_photo);
            tvName  = v.findViewById(R.id.tv_cast_name);
            tvChar  = v.findViewById(R.id.tv_cast_character);
        }
    }
}