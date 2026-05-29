package com.example.cinesound.ui.detalhes;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinesound.R;
import com.example.cinesound.adapters.ElencoAdapter;
import com.example.cinesound.adapters.ProvedorAdapter;
import com.example.cinesound.adapters.VideoAdapter;
import com.example.cinesound.models.DetalheTmdb;
import com.example.cinesound.models.ElencoTmdb;
import com.example.cinesound.models.ProvedoresTmdb;
import com.example.cinesound.models.TituloItem;
import com.example.cinesound.models.VideosTmdb;
import com.example.cinesound.network.Callback;
import com.example.cinesound.repository.FirebaseRepository;
import com.example.cinesound.repository.TmdbRepository;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class DetalhesActivity extends AppCompatActivity {

    private static final String IMG_BACKDROP = "https://image.tmdb.org/t/p/w780";

    // Views
    private ImageView ivPoster, ivBanner;
    private ImageView star1, star2, star3, star4, star5;
    private TextView tvTitle, tvMeta, tvSinopse, tvRating, tvVotes;
    private RecyclerView rvCast, rvSoundtrack, rvPlatforms;
    private MaterialButton btnAmei, btnGostei, btnNaoGostei, btnOdiei;

    // Repositórios
    private FirebaseRepository firebase;
    private TmdbRepository tmdb;

    // Estado
    private TituloItem tituloAtual;
    private String classificacaoAtual = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        firebase = new FirebaseRepository();
        tmdb     = new TmdbRepository();

        vincularComponentes();
        montarTituloDoIntent();

        preencherDadosBasicos();
        configurarBotoesClassificacao();

        verificarClassificacaoExistente();

        // Chamadas TMDB (Raian) — dependem da chave da API no local.properties
        carregarDetalhes();
        carregarElenco();
        carregarOndeAssistir();
        carregarTrilha();
    }

    // ============================================================
    // VIEWS
    // ============================================================

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
        btnAmei      = findViewById(R.id.btn_amei);
        btnGostei    = findViewById(R.id.btn_gostei);
        btnNaoGostei = findViewById(R.id.btn_nao_gostei);
        btnOdiei     = findViewById(R.id.btn_odiei);
    }

    // ============================================================
    // DADOS BÁSICOS (vindos do Intent)
    // ============================================================

    private void montarTituloDoIntent() {
        tituloAtual          = new TituloItem();
        tituloAtual.tmdbId   = getIntent().getIntExtra("tmdbId", 0);
        tituloAtual.tipo     = getIntent().getStringExtra("tipo");
        tituloAtual.titulo   = getIntent().getStringExtra("titulo");
        tituloAtual.poster   = getIntent().getStringExtra("poster");
        tituloAtual.notaTmdb = getIntent().getDoubleExtra("nota", 0.0);
        if (tituloAtual.tipo == null) tituloAtual.tipo = "filme";
    }

    private void preencherDadosBasicos() {
        tvTitle.setText(tituloAtual.titulo != null ? tituloAtual.titulo : "");
        tvMeta.setText("filme".equals(tituloAtual.tipo) ? "Filme" : "Série");
        tvRating.setText(String.format("%.1f", tituloAtual.notaTmdb));
        tvVotes.setText("TMDB");
        exibirEstrelas(tituloAtual.notaTmdb);

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

    // ============================================================
    // CHAMADAS À TMDB (Raian)
    // ============================================================

    private void carregarDetalhes() {
        if (tituloAtual.tmdbId == 0) return;

        tmdb.buscarDetalhes(tituloAtual.tmdbId, tituloAtual.tipo,
                new Callback<DetalheTmdb>() {
                    @Override
                    public void onSuccess(DetalheTmdb detalhe) {
                        // Sinopse
                        if (detalhe.sinopse != null && !detalhe.sinopse.isEmpty()) {
                            tvSinopse.setText(detalhe.sinopse);
                        }

                        // Meta: tipo + ano + duração + gêneros
                        StringBuilder meta = new StringBuilder();
                        meta.append("filme".equals(tituloAtual.tipo) ? "Filme" : "Série");

                        String ano = detalhe.getAno();
                        if (!ano.isEmpty()) meta.append(" • ").append(ano);

                        int min = detalhe.getDuracaoMinutos();
                        if (min > 0) {
                            meta.append(" • ").append(min / 60).append("h ").append(min % 60).append("min");
                        }

                        String generos = detalhe.getGenerosComoTexto();
                        if (!generos.isEmpty()) meta.append(" • ").append(generos);

                        tvMeta.setText(meta.toString());

                        // Banner mais bonito vindo do backdrop
                        if (detalhe.backdropPath != null && !detalhe.backdropPath.isEmpty()) {
                            Glide.with(DetalhesActivity.this)
                                    .load(IMG_BACKDROP + detalhe.backdropPath)
                                    .into(ivBanner);
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        // Fica com o básico, não polui o usuário com Toast
                    }
                });
    }

    private void carregarElenco() {
        if (tituloAtual.tmdbId == 0) return;

        tmdb.buscarElenco(tituloAtual.tmdbId, tituloAtual.tipo,
                new Callback<List<ElencoTmdb.MembroElenco>>() {
                    @Override
                    public void onSuccess(List<ElencoTmdb.MembroElenco> lista) {
                        if (lista == null || lista.isEmpty()) return;
                        rvCast.setLayoutManager(new LinearLayoutManager(
                                DetalhesActivity.this,
                                LinearLayoutManager.HORIZONTAL, false));
                        rvCast.setAdapter(new ElencoAdapter(lista));
                    }
                    @Override public void onError(String msg) {}
                });
    }

    private void carregarOndeAssistir() {
        if (tituloAtual.tmdbId == 0) return;

        tmdb.buscarOndeAssistir(tituloAtual.tmdbId, tituloAtual.tipo,
                new Callback<List<ProvedoresTmdb.Provedor>>() {
                    @Override
                    public void onSuccess(List<ProvedoresTmdb.Provedor> lista) {
                        if (lista == null || lista.isEmpty()) return;
                        rvPlatforms.setLayoutManager(new LinearLayoutManager(
                                DetalhesActivity.this,
                                LinearLayoutManager.HORIZONTAL, false));
                        rvPlatforms.setAdapter(new ProvedorAdapter(lista));
                    }
                    @Override public void onError(String msg) {}
                });
    }

    private void carregarTrilha() {
        if (tituloAtual.tmdbId == 0) return;

        tmdb.buscarVideos(tituloAtual.tmdbId, tituloAtual.tipo,
                new Callback<List<VideosTmdb.Video>>() {
                    @Override
                    public void onSuccess(List<VideosTmdb.Video> lista) {
                        if (lista == null || lista.isEmpty()) return;
                        rvSoundtrack.setLayoutManager(new LinearLayoutManager(
                                DetalhesActivity.this,
                                LinearLayoutManager.HORIZONTAL, false));
                        rvSoundtrack.setAdapter(new VideoAdapter(lista, video -> {
                            // Abrir trailer no YouTube ao tocar
                            if (video.key == null || video.key.isEmpty()) return;
                            String url = "https://www.youtube.com/watch?v=" + video.key;
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(i);
                        }));
                    }
                    @Override public void onError(String msg) {}
                });
    }

    // ============================================================
    // CLASSIFICAÇÃO (Firebase — Ayran)
    // ============================================================

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
            if (btn.getTag() != null && btn.getTag().equals(selecionado)) {
                btn.setBackgroundColor(getResources().getColor(R.color.accent, null));
                btn.setTextColor(getResources().getColor(R.color.text_primary, null));
            } else {
                btn.setBackgroundColor(Color.TRANSPARENT);
                btn.setTextColor(getResources().getColor(R.color.text_secondary, null));
            }
        }
    }

    private void salvarClassificacao(String classificacao) {
        if (firebase.getUsuarioAtual() == null) {
            Toast.makeText(this, "Faça login para classificar", Toast.LENGTH_SHORT).show();
            return;
        }
        firebase.salvarClassificacao(tituloAtual, classificacao,
                unused -> Toast.makeText(this, "Classificação salva!",
                        Toast.LENGTH_SHORT).show(),
                e -> Toast.makeText(this, "Erro ao salvar classificação",
                        Toast.LENGTH_SHORT).show()
        );
    }

    private void verificarClassificacaoExistente() {
        if (firebase.getUsuarioAtual() == null) return;

        firebase.buscarClassificacoes(
                snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Long tmdbId = doc.getLong("tmdbId");
                        if (tmdbId != null && tmdbId.intValue() == tituloAtual.tmdbId) {
                            String cl = doc.getString("classificacao");
                            if (cl != null) {
                                classificacaoAtual = cl;
                                destacarBotao(cl);
                            }
                            break;
                        }
                    }
                },
                err -> { /* silencioso */ }
        );
    }
}