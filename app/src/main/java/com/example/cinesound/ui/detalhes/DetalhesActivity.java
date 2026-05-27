package com.example.cinesound.ui.detalhes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinesound.R;
import com.example.cinesound.models.TituloItem;
import com.example.cinesound.repository.FirebaseRepository;
import com.example.cinesound.repository.TmdbRepository;

public class DetalhesActivity extends AppCompatActivity {
    private ImageView imgPoster;
    private TextView txtTitulo, txtSinopse, txtGenero;
    private TextView txtDuracao, txtAno, txtClassificacao;
    private RatingBar ratingBar;
    private TextView    txtNota, txtVotos;
    private RecyclerView recyclerElenco, recyclerTrilha, recyclerOnde;
    private Button btnAmei, btnGostei, btnNaoGostei, btnOdiei;
    private FirebaseRepository firebase;
    private TmdbRepository tmdb;
    private TituloItem tituloAtual;
    private int tmdbId;
    private String tipo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalhes);

        firebase = new FirebaseRepository();
        tmdb = new TmdbRepository();

        tmdbId = getIntent().getIntExtra("tmdbId", 0);
        tipo   = getIntent().getStringExtra("tipo");


        vincularComponentes();
        configurarBotoesClassificacao();
        carregarDetalhes();
        verificarClassificacaoExistente();
    }


    private void vincularComponentes() {

        imgPoster = findViewById(R.id.iv_poster);
        txtTitulo = findViewById(R.id.tv_title);
        txtSinopse = findViewById(R.id.tv_sinopse);
        txtGenero = findViewById(R.id.tv_genero);
        txtDuracao = findViewById(R.id.tv_duracao);
        txtAno = findViewById(R.id.tv_ano);
        ratingBar = findViewById(R.id.ratingBar);
        txtNota = findViewById(R.id.txtNota);
        txtVotos = findViewById(R.id.txtVotos);
        recyclerElenco = findViewById(R.id.recyclerElenco);
        recyclerTrilha = findViewById(R.id.recyclerTrilha);
        recyclerOnde = findViewById(R.id.recyclerOnde);
        btnAmei = findViewById(R.id.btn_amei);
        btnGostei = findViewById(R.id.btn_gostei);
        btnNaoGostei = findViewById(R.id.btn_nao_gostei);
        btnOdiei = findViewById(R.id.btn_odiei);
    }



    private void carregarDetalhes() {

        tmdb.buscarDetalhes(tmdbId, tipo, new Callback<DetalheTmdb>() {

            public void onSuccess(DetalheTmdb d) {

                // Monta o TituloItem pra poder classificar depois

                tituloAtual = new TituloItem();

                tituloAtual.tmdbId  = d.id;

                tituloAtual.titulo  = d.titulo != null ? d.titulo : d.nome;

                tituloAtual.tipo    = tipo;

                tituloAtual.notaTmdb= d.nota;



                // Preenche a tela

                txtTitulo.setText(tituloAtual.titulo);

                txtSinopse.setText(d.sinopse);

                txtAno.setText(d.dataLancamento != null

                        ? d.dataLancamento.substring(0,4) : "");

                txtDuracao.setText(d.duracao + " min");

                ratingBar.setRating((float)(d.nota / 2));

                txtNota.setText(String.format("%.1f", d.nota));



                // Gêneros

                if (d.generos != null && !d.generos.isEmpty()) {

                    StringBuilder sb = new StringBuilder();

                    for (DetalheTmdb.GeneroPar g : d.generos)

                        sb.append(g.nome).append(", ");

                    txtGenero.setText(sb.toString()

                            .replaceAll(", $", ""));

                }



                // Pôster

                Glide.with(DetalhesActivity.this)

                        .load("https://image.tmdb.org/t/p/w500"

                                + d.posterPath)

                        .into(imgPoster);



                carregarElenco();

                carregarOndeAssistir();

                carregarTrilha();

            }

            public void onError(String msg) {}

        });

    }



    private void carregarElenco() {

        tmdb.buscarElenco(tmdbId, new Callback<List<MembroElenco>>() {

            public void onSuccess(List<MembroElenco> lista) {

                recyclerElenco.setLayoutManager(

                        new LinearLayoutManager(DetalhesActivity.this,

                                LinearLayoutManager.HORIZONTAL, false));

                recyclerElenco.setAdapter(new ElencoAdapter(lista));

            }

            public void onError(String msg) {}

        });

    }



    private void carregarOndeAssistir() {

        tmdb.buscarOndeAssistir(tmdbId, new Callback<List<Provedor>>() {

            public void onSuccess(List<Provedor> lista) {

                recyclerOnde.setLayoutManager(

                        new LinearLayoutManager(DetalhesActivity.this,

                                LinearLayoutManager.HORIZONTAL, false));

                recyclerOnde.setAdapter(new ProvedorAdapter(lista));

            }

            public void onError(String msg) {}

        });

    }



    private void carregarTrilha() {

        tmdb.buscarVideos(tmdbId, new Callback<List<Video>>() {

            public void onSuccess(List<Video> lista) {

                recyclerTrilha.setLayoutManager(

                        new LinearLayoutManager(DetalhesActivity.this,

                                LinearLayoutManager.HORIZONTAL, false));

                recyclerTrilha.setAdapter(new VideoAdapter(lista));

            }

            public void onError(String msg) {}

        });

    }



    // Verifica se o usuário já classificou esse título

    private void verificarClassificacaoExistente() {

        firebase.buscarClassificacaoDoTitulo(tmdbId,

                doc -> {

                    if (doc.exists()) {

                        String cl = doc.getString("classificacao");

                        destacarBotao(cl);

                    }

                }, err -> {}),

        );

    }



    private void configurarBotoesClassificacao() {

        btnAmei.setOnClickListener(v      -> classificar("amei"));

        btnGostei.setOnClickListener(v    -> classificar("gostei"));

        btnNaoGostei.setOnClickListener(v -> classificar("nao_gostei"));

        btnOdiei.setOnClickListener(v     -> classificar("odiei"));

    }



    private void classificar(String valor) {

        if (tituloAtual == null) return;

        firebase.salvarClassificacao(tituloAtual, valor,

                ok  -> destacarBotao(valor),

                err -> Toast.makeText(this, "Erro ao salvar",

                        Toast.LENGTH_SHORT).show()

        );

    }



    // Destaca o botão selecionado e apaga os outros

    private void destacarBotao(String classificacao) {

        resetarBotoes();

        switch (classificacao) {

            case "amei":      btnAmei.setAlpha(1f);     break;

            case "gostei":    btnGostei.setAlpha(1f);   break;

            case "nao_gostei":btnNaoGostei.setAlpha(1f);break;

            case "odiei":     btnOdiei.setAlpha(1f);    break;

        }

    }



    private void resetarBotoes() {

        btnAmei.setAlpha(0.4f);

        btnGostei.setAlpha(0.4f);

        btnNaoGostei.setAlpha(0.4f);

        btnOdiei.setAlpha(0.4f);

    }

}