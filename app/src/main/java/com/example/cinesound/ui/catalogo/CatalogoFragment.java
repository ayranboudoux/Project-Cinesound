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

import java.util.ArrayList;
import java.util.List;

public class CatalogoFragment extends Fragment {

    private EditText editTextBusca;
    private RecyclerView recyclerView;
    private TmdbRepository tmdb;
    private TituloAdapter adapter;
    private List<TituloItem> listaCompleta = new ArrayList<>();

    // filtros ativos
    private String tipoFiltro = "todos";
    private List<Integer> generosFiltro = new ArrayList<>();
    private String ordenacao = "popularity.desc";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        tmdb = new TmdbRepository();

        editTextBusca = view.findViewById(R.id.et_search);
        recyclerView  = view.findViewById(R.id.rv_catalog);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        configurarBusca();
        carregarCatalogo();

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

    // Filtra a lista local sem chamar a API de novo
    private void filtrarPorNome(String query) {
        if (query.isEmpty()) {
            atualizarRecycler(listaCompleta);
            return;
        }

        List<TituloItem> filtrada = new ArrayList<>();
        for (TituloItem item : listaCompleta) {
            if (item.titulo.toLowerCase().contains(query.toLowerCase())) {
                filtrada.add(item);
            }
        }
        atualizarRecycler(filtrada);
    }

    private void carregarCatalogo() {
        tmdb.buscarFilmes(null, tipoFiltro, ordenacao, generosFiltro,
                null, null, 1, new Callback<List<TituloItem>>() {
                    public void onSuccess(List<TituloItem> lista) {
                        listaCompleta = lista;
                        atualizarRecycler(lista);
                    }
                    public void onError(String msg) {}
                });
    }

    // Chamado pelo FiltroBottomSheet quando o usuário aplica filtros
    public void aplicarFiltros(String tipo, List<Integer> generos, String ordem) {
        this.tipoFiltro    = tipo;
        this.generosFiltro = generos;
        this.ordenacao     = ordem;
        carregarCatalogo();
    }

    private void atualizarRecycler(List<TituloItem> lista) {
        adapter = new TituloAdapter(lista, item -> abrirDetalhes(item));
        recyclerView.setAdapter(adapter);
    }

    private void abrirDetalhes(TituloItem item) {
        Intent intent = new Intent(getContext(), DetalhesActivity.class);
        intent.putExtra("tmdbId", item.tmdbId);
        intent.putExtra("tipo",   item.tipo);
        startActivity(intent);
    }
}