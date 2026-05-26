package com.example.cinesound.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinesound.network.Callback;
import com.example.cinesound.repository.FirebaseRepository;
import com.example.cinesound.R;
import com.example.cinesound.adapters.TituloAdapter;
import com.example.cinesound.models.TituloItem;
import com.example.cinesound.repository.TmdbRepository;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private TituloAdapter adapter;
    private Chip chipFilmes, chipSeries, chipTodos;

    private FirebaseRepository firebase;

    private TmdbRepository tmdb;

    private List<TituloItem> listaCompleta = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebase = new FirebaseRepository();
        tmdb = new TmdbRepository();


        recyclerView = view.findViewById(R.id.rv_recommendations);
        chipFilmes = view.findViewById(R.id.chip_movies);
        chipSeries = view.findViewById(R.id.chip_series);
        chipTodos = view.findViewById(R.id.chip_all);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        configurarFiltros();
        carregarRecomendacoes();

        return view;
    }



    private void carregarRecomendacoes() {
        firebase.buscarGenerosFavoritos(snapshot -> {
            List<Integer> generos = firebase.extrairGenerosMaisFrequentes(snapshot);
                   if (generos.isEmpty()) {
                       // Usuário ainda não classificou nada
                       // Mostra trending como fallback
                        tmdb.buscarTrending(new Callback<List<TituloItem>>() {
                            public void onSuccess(List<TituloItem> lista) {
                                listaCompleta = lista;
                                atualizarLista("todos");
                            }
                            public void onError(String msg) {}
                        });
                        return;
                   }

                    tmdb.buscarPorGeneros(generos, "filme", new Callback<List<TituloItem>>() {
                        public void onSuccess(List<TituloItem> lista) {
                               listaCompleta = lista;
                               atualizarLista("todos");
                        }
                        public void onError(String msg) {}
                    });
                   }, err -> {}
        );

    }



    private void configurarFiltros() {
        chipTodos.setOnClickListener(v  -> atualizarLista("todos"));
        chipFilmes.setOnClickListener(v -> atualizarLista("filme"));
        chipSeries.setOnClickListener(v -> atualizarLista("serie"));
    }



    private void atualizarLista(String filtro) {
        List<TituloItem> filtrada = new ArrayList<>();

        for (TituloItem item : listaCompleta) {
            if (filtro.equals("todos") || item.tipo.equals(filtro)){
                filtrada.add(item);
            }
        }

        adapter = new TituloAdapter(filtrada, item -> abrirDetalhes(item));
        recyclerView.setAdapter(adapter);
    }



    private void abrirDetalhes(TituloItem item) {
        Intent intent = new Intent(getContext(), DetalhesActivity.class);

        intent.putExtra("tmdbId", item.tmdbId);
        intent.putExtra("tipo",   item.tipo);

        startActivity(intent);
    }

}