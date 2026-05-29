package com.example.cinesound.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinesound.R;
import com.example.cinesound.models.VideosTmdb;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VH> {

    public interface OnClick {
        void abrir(VideosTmdb.Video video);
    }

    private final List<VideosTmdb.Video> lista;
    private final OnClick aoClicar;

    public VideoAdapter(List<VideosTmdb.Video> lista, OnClick aoClicar) {
        this.lista = lista;
        this.aoClicar = aoClicar;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_soundtrack, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        VideosTmdb.Video video = lista.get(position);
        h.tvTitle.setText(video.nome != null ? video.nome : "");
        h.itemView.setOnClickListener(v -> {
            if (aoClicar != null) aoClicar.abrir(video);
        });
    }

    @Override public int getItemCount() { return lista == null ? 0 : lista.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle;
        VH(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tv_video_title);
        }
    }
}