package com.example.cinesound.ui.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinesound.R;
import com.example.cinesound.adapters.TituloAdapter;
import com.example.cinesound.models.TituloItem;
import com.example.cinesound.repository.FirebaseRepository;
import com.example.cinesound.ui.detalhes.DetalhesActivity;
import com.example.cinesound.ui.login.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PerfilFragment extends Fragment {

    private ImageView    ivAvatar;
    private TextView     tvNome, tvEmail, tvHistoryCount;
    private MaterialButton btnEditar, btnLogout;
    private RecyclerView rvHistorico, rvFilmes, rvSeries, rvTrilhas;

    private FirebaseRepository firebase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebase = new FirebaseRepository();

        ivAvatar       = view.findViewById(R.id.iv_avatar);
        tvNome         = view.findViewById(R.id.tv_user_name);
        tvEmail        = view.findViewById(R.id.tv_user_email);
        tvHistoryCount = view.findViewById(R.id.tv_history_count);
        btnEditar      = view.findViewById(R.id.btn_edit_profile);
        btnLogout      = view.findViewById(R.id.btn_logout);
        rvHistorico    = view.findViewById(R.id.rv_history);
        rvFilmes       = view.findViewById(R.id.rv_fav_movies);
        rvSeries       = view.findViewById(R.id.rv_fav_series);
        rvTrilhas      = view.findViewById(R.id.rv_fav_soundtracks);

        configurarRecyclers();
        carregarPerfil();
        carregarClassificacoes();

        btnLogout.setOnClickListener(v -> fazerLogout());
        // btnEditar: pode abrir AlertDialog  depois

        return view;
    }

    private void configurarRecyclers() {
        rvHistorico.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        rvFilmes.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        rvSeries.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        rvTrilhas.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
    }

    private void carregarPerfil() {
        if (firebase.getUsuarioAtual() == null) return;
        String uid = firebase.getUsuarioAtual().getUid();

        firebase.buscarPerfil(uid, doc -> {
            if (doc == null || !doc.exists()) return;
            String nome  = doc.getString("nome");
            String email = doc.getString("email");
            tvNome.setText(nome != null ? nome : "");
            tvEmail.setText(email != null ? email : firebase.getUsuarioAtual().getEmail());

            String avatarUrl = doc.getString("avatarUrl");
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(this).load(avatarUrl).into(ivAvatar);
            }
        }, err -> tvEmail.setText(firebase.getUsuarioAtual().getEmail()));
    }

    private void carregarClassificacoes() {
        firebase.buscarClassificacoes(snapshot -> {
            List<TituloItem> historico = new ArrayList<>();
            List<TituloItem> filmes    = new ArrayList<>();
            List<TituloItem> series    = new ArrayList<>();

            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                TituloItem item = new TituloItem();
                Long tmdbId = doc.getLong("tmdbId");
                if (tmdbId != null) item.tmdbId = tmdbId.intValue();
                item.titulo        = doc.getString("titulo");
                item.poster        = doc.getString("poster");
                item.tipo          = doc.getString("tipo");
                item.classificacao = doc.getString("classificacao");
                Double nota = doc.getDouble("notaTmdb");
                if (nota != null) item.notaTmdb = nota;

                historico.add(item);

                if ("amei".equals(item.classificacao) || "gostei".equals(item.classificacao)) {
                    if ("filme".equals(item.tipo))      filmes.add(item);
                    else if ("serie".equals(item.tipo)) series.add(item);
                }
            }

            tvHistoryCount.setText(historico.size() + " títulos");
            rvHistorico.setAdapter(new TituloAdapter(historico, this::abrirDetalhes));
            rvFilmes.setAdapter(new TituloAdapter(filmes, this::abrirDetalhes));
            rvSeries.setAdapter(new TituloAdapter(series, this::abrirDetalhes));
            // Trilhas favoritas: usa filmes favoritos como base (já que a TMDB liga trilha ao título)
            rvTrilhas.setAdapter(new TituloAdapter(filmes, this::abrirDetalhes));
        }, err -> {});
    }

    private void abrirDetalhes(TituloItem item) {
        Intent intent = new Intent(getContext(), DetalhesActivity.class);
        intent.putExtra("tmdbId", item.tmdbId);
        intent.putExtra("tipo",   item.tipo);
        intent.putExtra("titulo", item.titulo);
        intent.putExtra("poster", item.poster);
        intent.putExtra("nota",   item.notaTmdb);
        startActivity(intent);
    }

    private void fazerLogout() {
        firebase.logout();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}