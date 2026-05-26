package com.example.cinesound.repository;


import com.example.cinesound.network.Callback;
import com.example.cinesound.network.RetrofitClient;
import com.example.cinesound.network.TmdbService;
import com.example.cinesound.models.ItemTmdb;
import com.example.cinesound.models.ResultadoTmdb;
import com.example.cinesound.models.TituloItem;
import com.google.firebase.appcheck.interop.BuildConfig;

apiKey = BuildConfig.TMDB_API_KEY;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;
public class TmdbRepository {
    private TmdbService service;
    private String apiKey;
    private static final String BASE_POSTER =
            "https://image.tmdb.org/t/p/w500";
    private static final String IDIOMA = "pt-BR";
    public TmdbRepository() {
        service = RetrofitClient.getClient()
                .create(TmdbService.class);
        apiKey  = BuildConfig.TMDB_API_KEY;
    }
    // Converte ItemTmdb para TituloItem (classe compartilhada com Ayran)
    private TituloItem converter(ItemTmdb item) {
        TituloItem t = new TituloItem();
        t.tmdbId   = item.id;
        t.titulo   = item.titulo != null ? item.titulo : item.nome;
        t.poster   = item.posterPath != null
                ? BASE_POSTER + item.posterPath : "";
        t.tipo     = "tv".equals(item.mediaType) ? "serie" : "filme";
        t.generos  = item.generoIds != null
                ? item.generoIds : new ArrayList<>();
        t.notaTmdb = item.nota;
        return t;
    }
    // Busca por nome
    public void buscarPorNome(String nome,
                              Callback<List<TituloItem>> callback) {
        service.buscarPorNome(apiKey, nome, IDIOMA, 1)
                .enqueue(new retrofit2.Callback<ResultadoTmdb>() {
                    public void onResponse(Call<ResultadoTmdb> c, Response<ResultadoTmdb> r) {
                        if (r.isSuccessful() && r.body() != null) {
                            List<TituloItem> lista = new ArrayList<>();
                            for (ItemTmdb i : r.body().resultados)
                                lista.add(converter(i));
                            callback.onSuccess(lista);
                        } else callback.onError("Erro na busca");
                    }
                    public void onFailure(Call<ResultadoTmdb> c, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }
    // Trending semanal
    public void buscarTrending(Callback<List<TituloItem>> callback) {
        service.buscarTrending(apiKey, IDIOMA)
                .enqueue(new retrofit2.Callback<ResultadoTmdb>() {
                    public void onResponse(Call<ResultadoTmdb> c, Response<ResultadoTmdb> r) {
                        if (r.isSuccessful() && r.body() != null) {
                            List<TituloItem> lista = new ArrayList<>();
                            for (ItemTmdb i : r.body().resultados)
                                lista.add(converter(i));
                            callback.onSuccess(lista);
                        } else callback.onError("Erro trending");
                    }
                    public void onFailure(Call<ResultadoTmdb> c, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }
    // Recomendações por gêneros (integração com Ayran)
    public void buscarPorGeneros(List<Integer> generoIds, String tipo,
                                 Callback<List<TituloItem>> callback) {
        String ids = generoIds.stream()
                .map(String::valueOf).collect(Collectors.joining(","));
        if ("serie".equals(tipo)) {
            service.buscarSeries(apiKey, IDIOMA, "popularity.desc", ids, null, null, 1)
                    .enqueue(/* mesmo padrão acima */);
        } else {
            service.buscarFilmes(apiKey, IDIOMA, "popularity.desc", ids, null, null, 1)
                    .enqueue(/* mesmo padrão acima */);
        }
    }

}