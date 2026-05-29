package com.example.cinesound.ui.busca;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinesound.R;
import com.example.cinesound.adapters.TituloAdapter;
import com.example.cinesound.models.TituloItem;
import com.example.cinesound.network.Callback;
import com.example.cinesound.repository.TmdbRepository;
import com.example.cinesound.ui.detalhes.DetalhesActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText etSearch;
    private MaterialButton btnBack, btnClear;
    private RecyclerView rvResults;
    private View layoutInitial, layoutLoading, layoutEmpty;
    private ChipGroup chipGroupSuggestions;
    private TmdbRepository tmdb;
    private TituloAdapter adapter;

    // debounce: só busca 300ms depois que o usuário parar de digitar
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable buscaPendente;

    private static final List<String> SUGESTOES = Arrays.asList(
            "Ação", "Comédia", "Terror", "Ficção Científica",
            "Anime", "Drama", "Suspense"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tmdb = new TmdbRepository();

        etSearch             = findViewById(R.id.et_search);
        btnBack              = findViewById(R.id.btn_back);
        btnClear             = findViewById(R.id.btn_clear);
        rvResults            = findViewById(R.id.rv_search_results);
        layoutInitial        = findViewById(R.id.layout_initial);
        layoutLoading        = findViewById(R.id.layout_loading);
        layoutEmpty          = findViewById(R.id.layout_empty);
        chipGroupSuggestions = findViewById(R.id.chip_group_suggestions);

        popularSugestoes();
        configurarBotoes();
        configurarBusca();

        // foca o campo automaticamente ao abrir
        etSearch.requestFocus();
    }

    // Cria os chips de sugestão dinamicamente
    private void popularSugestoes() {
        for (String sugestao : SUGESTOES) {
            Chip chip = new Chip(this);
            chip.setText(sugestao);
            chip.setClickable(true);
            chip.setOnClickListener(v -> {
                etSearch.setText(sugestao);
                etSearch.setSelection(sugestao.length()); // cursor no fim
            });
            chipGroupSuggestions.addView(chip);
        }
    }

    private void configurarBotoes() {
        btnBack.setOnClickListener(v -> finish());

        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            mostrarEstado("inicial");
        });
    }

    private void configurarBusca() {
        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // mostra ou esconde o botão X
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);

                // cancela busca anterior pendente
                if (buscaPendente != null) handler.removeCallbacks(buscaPendente);

                if (s.length() == 0) {
                    mostrarEstado("inicial");
                    return;
                }

                // aguarda 300ms de inatividade antes de buscar (debounce)
                mostrarEstado("carregando");
                buscaPendente = () -> buscar(s.toString());
                handler.postDelayed(buscaPendente, 300);
            }

            public void afterTextChanged(Editable s) {}
        });
    }

    private void buscar(String query) {
        tmdb.buscarPorNome(query, new Callback<List<TituloItem>>() {
            public void onSuccess(List<TituloItem> lista) {
                runOnUiThread(() -> {
                    if (lista.isEmpty()) {
                        mostrarEstado("vazio");
                    } else {
                        adapter = new TituloAdapter(lista, item -> abrirDetalhes(item));
                        rvResults.setAdapter(adapter);
                        mostrarEstado("resultados");
                    }
                });
            }

            public void onError(String msg) {
                runOnUiThread(() -> mostrarEstado("vazio"));
            }
        });
    }

    // Controla qual layout está visível
    private void mostrarEstado(String estado) {
        layoutInitial.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        rvResults.setVisibility(View.GONE);

        switch (estado) {
            case "inicial":
                layoutInitial.setVisibility(View.VISIBLE);
                break;
            case "carregando":
                layoutLoading.setVisibility(View.VISIBLE);
                break;
            case "vazio":
                layoutEmpty.setVisibility(View.VISIBLE);
                break;
            case "resultados":
                rvResults.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void abrirDetalhes(TituloItem item) {
        Intent intent = new Intent(this, DetalhesActivity.class);
        intent.putExtra("tmdbId", item.tmdbId);
        intent.putExtra("tipo",   item.tipo);
        intent.putExtra("titulo", item.titulo);
        intent.putExtra("poster", item.poster);
        intent.putExtra("nota",   item.notaTmdb);
        startActivity(intent);
    }
}