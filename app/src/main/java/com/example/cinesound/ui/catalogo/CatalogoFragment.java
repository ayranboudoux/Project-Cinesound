package com.example.cinesound.ui.catalogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinesound.R;
import com.example.cinesound.adapters.TituloAdapter;
import com.example.cinesound.models.TituloItem;
import com.example.cinesound.network.Callback;
import com.example.cinesound.repository.TmdbRepository;

import java.util.List;

public class CatalogoFragment extends Fragment {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private TmdbRepository tmdb;
    private TituloAdapter adapter;

    // filtros ativos
    private String tipoFiltro = "todos";
    private String generosFiltro = "";
    private String ordenacao = "popularity.desc";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);


        tmdb = new TmdbRepository();

        searchView = view.findViewById(R.id.searchView);//CONFIRMAR ISSO
        recyclerView = view.findViewById(R.id.rv_catalog);


        // Grid de 2 colunas conforme o design
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        configurarBusca();
        carregarCatalogo();
        return view;
    }



    private void configurarBusca() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        buscarPorNome(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String texto) {
                        if (texto.length() >= 3){
                            buscarPorNome(texto);
                        } else if (texto.isEmpty()) {
                            carregarCatalogo();
                        }
                        return true;
                    }
                });
    }


    private void buscarPorNome(String nome) {
        tmdb.buscarPorNome(nome, new Callback<List<TituloItem>>() {
            public void onSuccess(List<TituloItem> lista) {
                atualizarRecycler(lista);
            }
            public void onError(String msg) {}
        });

    }


    private void carregarCatalogo() {
        tmdb.buscarFilmes(null, null, ordenacao, generosFiltro,
                null, null, 1, new Callback<List<TituloItem>>() {
                    public void onSuccess(List<TituloItem> lista) {
                        atualizarRecycler(lista);
                    }
                    public void onError(String msg) {}
                });
    }


    // Chamado pelo FiltroBottomSheet quando o usuário aplica filtros
    public void aplicarFiltros(String tipo, String generos, String ordem) {
        this.tipoFiltro = tipo;
        this.generosFiltro = generos;
        this.ordenacao = ordem;
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