package com.example.cinesound.ui.catalogo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinesound.R;
import com.example.cinesound.adapters.TituloAdapter;
import com.example.cinesound.models.TituloItem;
import com.example.cinesound.network.Callback;
import com.example.cinesound.repository.TmdbRepository;
import com.example.cinesound.ui.detalhes.DetalhesActivity;

import java.util.ArrayList;
import java.util.List;

public class CatalogoFragment extends Fragment {

    private EditText editTextBusca;
    private RecyclerView recyclerView;
    private TmdbRepository tmdb;
    private TituloAdapter adapter;
    private List<TituloItem> listaCompleta = new ArrayList<>();
    private com.google.android.material.chip.ChipGroup chipGroupType, chipGroupGenre, chipGroupSort;
    private static final int[] GENRE_IDS = { 28, 18, 878, 27, 35, 16, 80 }; // action, drama, sci-fi, horror, comedy, animation, crime


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        tmdb = new TmdbRepository();

        editTextBusca = view.findViewById(R.id.et_search);
        recyclerView  = view.findViewById(R.id.rv_catalog);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        configurarBusca();

        chipGroupType  = view.findViewById(R.id.chip_group_type);
        chipGroupGenre = view.findViewById(R.id.chip_group_genre);
        chipGroupSort  = view.findViewById(R.id.chip_group_sort);
        configurarFiltros();
        aplicarFiltros();

        return view;
    }

    private void configurarBusca() {
        editTextBusca.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarPorNome(s.toString());
            }
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filtrarPorNome(String query) {
        if (query.isEmpty()) {
            atualizarRecycler(listaCompleta);
            return;
        }
        List<TituloItem> filtrada = new ArrayList<>();
        String q = query.toLowerCase();
        for (TituloItem item : listaCompleta) {
            if (item.titulo != null && item.titulo.toLowerCase().contains(q)) {
                filtrada.add(item);
            }
        }
        atualizarRecycler(filtrada);
    }

    private void atualizarRecycler(List<TituloItem> lista) {
        adapter = new TituloAdapter(lista, this::abrirDetalhes);
        recyclerView.setAdapter(adapter);
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

    private void configurarFiltros() {

        chipGroupType.setOnCheckedChangeListener(
                (group, checkedId) -> aplicarFiltros()
        );

        chipGroupSort.setOnCheckedChangeListener(
                (group, checkedId) -> aplicarFiltros()
        );

        int[] genreChipIds = {
                R.id.chip_genre_action,
                R.id.chip_genre_drama,
                R.id.chip_genre_scifi,
                R.id.chip_genre_horror,
                R.id.chip_genre_comedy,
                R.id.chip_genre_animation,
                R.id.chip_genre_crime
        };

        for (int chipId : genreChipIds) {

            com.google.android.material.chip.Chip chip =
                    chipGroupGenre.findViewById(chipId);

            if (chip != null) {

                chip.setOnCheckedChangeListener(
                        (buttonView, isChecked) -> aplicarFiltros()
                );
            }
        }
    }

    private void aplicarFiltros() {
        // Tipo
        int typeId = chipGroupType.getCheckedChipId();
        String tipo;
        if (typeId == R.id.chip_type_series) tipo = "serie";
        else if (typeId == R.id.chip_type_all || typeId == R.id.chip_type_anime) tipo = "todos";
        else tipo = "filme"; // chip_type_movies (default)

        // Gênero — pega todos os chips marcados
        List<Integer> generoIds = new ArrayList<>();
        int[] chipIds = {R.id.chip_genre_action, R.id.chip_genre_drama, R.id.chip_genre_scifi,
                R.id.chip_genre_horror, R.id.chip_genre_comedy,
                R.id.chip_genre_animation, R.id.chip_genre_crime};

        for (int i = 0; i < chipIds.length; i++) {
            com.google.android.material.chip.Chip chip =
                    chipGroupGenre.findViewById(chipIds[i]);
            if (chip != null && chip.isChecked()) {
                generoIds.add(GENRE_IDS[i]);
            }
        }
        String generosParam = generoIds.isEmpty() ? null :
                generoIds.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));

        // Ordenação
        int sortId = chipGroupSort.getCheckedChipId();
        String ordenacao;
        if (sortId == R.id.chip_sort_rating)   ordenacao = "vote_average.desc";
        else if (sortId == R.id.chip_sort_newest) ordenacao = "primary_release_date.desc";
        else if (sortId == R.id.chip_sort_popular) ordenacao = "popularity.desc";
        else ordenacao = "original_title.asc"; // A-Z

        Callback<List<TituloItem>> cb = new Callback<List<TituloItem>>() {
            @Override public void onSuccess(List<TituloItem> lista) {
                listaCompleta = lista;
                atualizarRecycler(lista);
            }
            @Override public void onError(String msg) {}
        };

        if ("serie".equals(tipo)) {

            tmdb.buscarSeries(
                    ordenacao,
                    generosParam,
                    null,
                    null,
                    cb
            );

        } else if ("filme".equals(tipo)) {

            tmdb.buscarFilmes(
                    ordenacao,
                    generosParam,
                    null,
                    null,
                    cb
            );

        } else {

            tmdb.buscarTrending(cb);

        }
    }
}