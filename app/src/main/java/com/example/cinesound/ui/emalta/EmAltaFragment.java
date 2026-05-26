package com.example.cinesound.ui.emalta;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import com.example.cinesound.R;
import com.example.cinesound.adapters.EmAltaAdapter;
import com.example.cinesound.models.TituloItem;
import com.example.cinesound.network.Callback;
import com.example.cinesound.repository.TmdbRepository;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class EmAltaFragment extends Fragment {
    private RecyclerView recyclerView;
    private Chip chipFilmes, chipSeries, chipTodos;
    private TmdbRepository tmdb;
    private List<TituloItem> listaCompleta = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);



        tmdb = new TmdbRepository();

        recyclerView = view.findViewById(R.id.rv_trending);
        chipFilmes   = view.findViewById(R.id.chip_movies);
        chipSeries   = view.findViewById(R.id.chip_series);
        chipTodos    = view.findViewById(R.id.chip_all);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        configurarFiltros();
        carregarTrending();

        return view;
    }



    private void carregarTrending() {
        tmdb.buscarTrending(new Callback<List<TituloItem>>() {
            public void onSuccess(List<TituloItem> lista) {
                listaCompleta = lista;
                atualizarLista("todos");
            }

            public void onError(String msg) {
                Toast.makeText(getContext(), "Erro ao carregar",
                        Toast.LENGTH_SHORT).show();
            }
        });
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

        // passa a posição para o adapter mostrar #1, #2...

        EmAltaAdapter adapter = new EmAltaAdapter(filtrada,

                item -> abrirDetalhes(item));

        recyclerView.setAdapter(adapter);

    }



    private void abrirDetalhes(TituloItem item) {

        Intent intent = new Intent(getContext(), DetalhesActivity.class);

        intent.putExtra("tmdbId", item.tmdbId);

        intent.putExtra("tipo",   item.tipo);

        startActivity(intent);

    }

}