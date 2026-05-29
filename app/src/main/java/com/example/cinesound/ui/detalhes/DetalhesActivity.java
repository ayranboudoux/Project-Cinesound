package com.example.cinesound.ui.detalhes;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinesound.R;
import com.example.cinesound.models.TituloItem;
import com.example.cinesound.repository.FirebaseRepository;
import com.example.cinesound.repository.TmdbRepository;
import com.google.android.material.button.MaterialButton;

public class DetalhesActivity extends AppCompatActivity {

    // Views
    private ImageView ivPoster, ivBanner;
    private ImageView star1, star2, star3, star4, star5;
    private TextView tvTitle, tvMeta, tvSinopse, tvRating, tvVotes;
    private RecyclerView rvCast, rvSoundtrack, rvPlatforms, rvInfoGrid;
    private MaterialButton btnAmei, btnGostei, btnNaoGostei, btnOdiei;

    // Repositórios
    private FirebaseRepository firebase;
    private TmdbRepository tmdb;

    // Dados
    private TituloItem tituloAtual;
    private String classificacaoAtual = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        firebase = new FirebaseRepository();
        tmdb     = new TmdbRepository();

        vincularComponentes();

        // Monta o TituloItem com os dados que vieram do Intent
        tituloAtual          = new TituloItem();
        tituloAtual.tmdbId   = getIntent().getIntExtra("tmdbId", 0);
        tituloAtual.tipo     = getIntent().getStringExtra("tipo");
        tituloAtual.titulo   = getIntent().getStringExtra("titulo");
        tituloAtual.poster   = getIntent().getStringExtra("poster");
        tituloAtual.notaTmdb = getIntent().getDoubleExtra("nota", 0.0);

        preencherDadosBasicos();
        configurarBotoesClassificacao();
        verificarClassificacaoExistente();

        // DEPENDE DO RAYAN — descomentar quando os métodos existirem
        // carregarElenco();
        // carregarOndeAssistir();
        // carregarTrilha();
    }

    private void vincularComponentes() {
        ivPoster     = findViewById(R.id.iv_poster);
        ivBanner     = findViewById(R.id.iv_banner);
        tvTitle      = findViewById(R.id.tv_title);
        tvMeta       = findViewById(R.id.tv_meta);
        tvSinopse    = findViewById(R.id.tv_sinopse);
        tvRating     = findViewById(R.id.tv_rating);
        tvVotes      = findViewById(R.id.tv_votes);
        star1        = findViewById(R.id.star1);
        star2        = findViewById(R.id.star2);
        star3        = findViewById(R.id.star3);
        star4        = findViewById(R.id.star4);
        star5        = findViewById(R.id.star5);
        rvCast       = findViewById(R.id.rv_cast);
        rvSoundtrack = findViewById(R.id.rv_soundtrack);
        rvPlatforms  = findViewById(R.id.rv_platforms);
        rvInfoGrid   = findViewById(R.id.rv_info_grid);
        btnAmei      = findViewById(R.id.btn_amei);
        btnGostei    = findViewById(R.id.btn_gostei);
        btnNaoGostei = findViewById(R.id.btn_nao_gostei);
        btnOdiei     = findViewById(R.id.btn_odiei);
    }

    private void preencherDadosBasicos() {
        tvTitle.setText(tituloAtual.titulo);
        tvMeta.setText(tituloAtual.tipo.equals("filme") ? "Filme" : "Série");
        tvRating.setText(String.format("%.1f", tituloAtual.notaTmdb));
        tvVotes.setText("IMDb");

        exibirEstrelas(tituloAtual.notaTmdb);

        // Pôster
        if (tituloAtual.poster != null && !tituloAtual.poster.isEmpty()) {
            Glide.with(this).load(tituloAtual.poster).into(ivPoster);
            Glide.with(this).load(tituloAtual.poster).into(ivBanner);
        }
    }

    private void exibirEstrelas(double notaTmdb) {
        int estrelas = (int) Math.round(notaTmdb / 2);

        ImageView[] stars = { star1, star2, star3, star4, star5 };

        for (int i = 0; i < 5; i++) {
            if (i < estrelas) {
                stars[i].setImageResource(R.drawable.ic_star_1_details);
                stars[i].setColorFilter(getResources().getColor(R.color.yellow_star, null));
            } else {
                stars[i].setImageResource(R.drawable.ic_star_2_details);
                stars[i].setColorFilter(getResources().getColor(R.color.bg_tertiary, null));
            }
        }
    }

    private void configurarBotoesClassificacao() {
        btnAmei.setTag("amei");
        btnGostei.setTag("gostei");
        btnNaoGostei.setTag("nao_gostei");
        btnOdiei.setTag("odiei");

        View.OnClickListener listener = v -> {
            String classificacao = (String) v.getTag();
            classificacaoAtual = classificacao;
            destacarBotao(classificacao);
            salvarClassificacao(classificacao);
        };

        btnAmei.setOnClickListener(listener);
        btnGostei.setOnClickListener(listener);
        btnNaoGostei.setOnClickListener(listener);
        btnOdiei.setOnClickListener(listener);
    }

    private void destacarBotao(String selecionado) {
        MaterialButton[] botoes = { btnAmei, btnGostei, btnNaoGostei, btnOdiei };

        for (MaterialButton btn : botoes) {
            if (btn.getTag().equals(selecionado)) {
                btn.setBackgroundColor(getResources().getColor(R.color.accent, null));
                btn.setTextColor(getResources().getColor(R.color.text_primary, null));
            } else {
                btn.setBackgroundColor(Color.TRANSPARENT);
                btn.setTextColor(getResources().getColor(R.color.text_secondary, null));
            }
        }
    }

    private void salvarClassificacao(String classificacao) {
        firebase.salvarClassificacao(tituloAtual, classificacao,
                unused -> { /* sucesso, botão já destacado */ },
                e -> Toast.makeText(this, "Erro ao salvar classificação",
                        Toast.LENGTH_SHORT).show()
        );
    }

    private void verificarClassificacaoExistente() {
        firebase.buscarClassificacoes(
                snapshot -> {
                    snapshot.getDocuments().forEach(doc -> {
                        if (doc.getLong("tmdbId") != null &&
                                doc.getLong("tmdbId").intValue() == tituloAtual.tmdbId) {
                            String cl = doc.getString("classificacao");
                            if (cl != null) destacarBotao(cl);
                        }
                    });
                },
                err -> { /* silencioso */ }
        );
    }

    // =============================================
    // DEPENDE DO RAYAN — descomentar quando pronto
    // =============================================

    // private void carregarElenco() {
    //     tmdb.buscarElenco(tituloAtual.tmdbId, new Callback<List<MembroElenco>>() {
    //         public void onSuccess(List<MembroElenco> lista) {
    //             rvCast.setLayoutManager(new LinearLayoutManager(
    //                     DetalhesActivity.this, LinearLayoutManager.HORIZONTAL, false));
    //             rvCast.setAdapter(new ElencoAdapter(lista));
    //         }
    //         public void onError(String msg) {}
    //     });
    // }

    // private void carregarOndeAssistir() {
    //     tmdb.buscarOndeAssistir(tituloAtual.tmdbId, new Callback<List<Provedor>>() {
    //         public void onSuccess(List<Provedor> lista) {
    //             rvPlatforms.setLayoutManager(new LinearLayoutManager(
    //                     DetalhesActivity.this, LinearLayoutManager.HORIZONTAL, false));
    //             rvPlatforms.setAdapter(new ProvedorAdapter(lista));
    //         }
    //         public void onError(String msg) {}
    //     });
    // }

    // private void carregarTrilha() {
    //     tmdb.buscarVideos(tituloAtual.tmdbId, new Callback<List<Video>>() {
    //         public void onSuccess(List<Video> lista) {
    //             rvSoundtrack.setLayoutManager(new LinearLayoutManager(
    //                     DetalhesActivity.this, LinearLayoutManager.HORIZONTAL, false));
    //             rvSoundtrack.setAdapter(new VideoAdapter(lista));
    //         }
    //         public void onError(String msg) {}
    //     });
    // }
}