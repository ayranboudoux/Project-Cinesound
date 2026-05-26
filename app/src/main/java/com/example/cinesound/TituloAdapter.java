package com.example.cinesound;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class TituloAdapter extends RecyclerView.Adapter<TituloAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(TituloItem item);
    }

    private List<TituloItem> lista;
    private OnItemClickListener listener;

    public TituloAdapter(List<TituloItem> lista, OnItemClickListener listener) {
        this.lista    = lista;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TituloItem item = lista.get(position);

        holder.txtTitulo.setText(item.titulo);

        String sub = String.format("%.1f ★  •  %s", item.notaTmdb,
                "filme".equals(item.tipo) ? "Filme" : "Série");
        holder.txtGeneroAno.setText(sub);
        holder.txtMotivo.setText("");

        Glide.with(holder.itemView.getContext())
                .load(item.poster)
                .placeholder(R.drawable.ic_movie)
                .into(holder.imgPoster);

        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView  txtTitulo, txtGeneroAno, txtMotivo;

        public ViewHolder(View itemView) {
            super(itemView);
            imgPoster    = itemView.findViewById(R.id.iv_poster);
            txtTitulo    = itemView.findViewById(R.id.tv_title);
            txtGeneroAno = itemView.findViewById(R.id.tv_genre_year);
            txtMotivo    = itemView.findViewById(R.id.tv_description);
        }
    }
}