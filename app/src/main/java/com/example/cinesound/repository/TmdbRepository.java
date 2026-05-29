package com.example.cinesound.repository;


import com.example.cinesound.BuildConfig;
import com.example.cinesound.models.DetalheTmdb;
import com.example.cinesound.models.ElencoTmdb;
import com.example.cinesound.models.GenerosTmdb;
import com.example.cinesound.models.ItemTmdb;
import com.example.cinesound.models.ProvedoresTmdb;
import com.example.cinesound.models.ResultadoTmdb;
import com.example.cinesound.models.TituloItem;
import com.example.cinesound.models.VideosTmdb;
import com.example.cinesound.network.Callback;
import com.example.cinesound.network.RetrofitClient;
import com.example.cinesound.network.TmdbService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class TmdbRepository {

    private final TmdbService service;
    private final String apiKey;
    private static final String BASE_POSTER = "https://image.tmdb.org/t/p/w500";
    private static final String IDIOMA = "pt-BR";

    public TmdbRepository() {
        service = RetrofitClient.getClient().create(TmdbService.class);
        apiKey  = "chave da api aqui";
    }

    // Converte um item da TMDB para o TituloItem compartilhado
    private TituloItem converter(ItemTmdb item) {
        TituloItem t = new TituloItem();
        t.tmdbId = item.id;
        t.titulo = (item.titulo != null && !item.titulo.isEmpty())
                ? item.titulo : item.nome;
        t.poster = (item.posterPath != null && !item.posterPath.isEmpty())
                ? BASE_POSTER + item.posterPath : "";
        if (item.mediaType == null) {
            t.tipo = item.titulo != null ? "filme" : "serie";
        } else {
            t.tipo = "tv".equals(item.mediaType) ? "serie" : "filme";
        }
        t.generos  = item.generoIds != null ? item.generoIds : new ArrayList<>();
        t.notaTmdb = item.nota;
        return t;
    }

    // 1) Busca por nome (filmes + séries)
    public void buscarPorNome(String nome, Callback<List<TituloItem>> callback) {
        service.buscarPorNome(apiKey, nome, IDIOMA, 1)
                .enqueue(new retrofit2.Callback<ResultadoTmdb>() {
                    @Override
                    public void onResponse(Call<ResultadoTmdb> c, Response<ResultadoTmdb> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().resultados != null) {
                            List<TituloItem> lista = new ArrayList<>();
                            for (ItemTmdb i : r.body().resultados) {
                                if (i.mediaType == null
                                        || "movie".equals(i.mediaType)
                                        || "tv".equals(i.mediaType)) {
                                    lista.add(converter(i));
                                }
                            }
                            callback.onSuccess(lista);
                        } else {
                            callback.onError("Erro na busca");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultadoTmdb> c, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    // 2) Trending semanal (Em Alta)
    public void buscarTrending(Callback<List<TituloItem>> callback) {
        service.buscarTrending(apiKey, IDIOMA)
                .enqueue(new retrofit2.Callback<ResultadoTmdb>() {
                    @Override
                    public void onResponse(Call<ResultadoTmdb> c, Response<ResultadoTmdb> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().resultados != null) {
                            List<TituloItem> lista = new ArrayList<>();
                            for (ItemTmdb i : r.body().resultados) {
                                lista.add(converter(i));
                            }
                            callback.onSuccess(lista);
                        } else {
                            callback.onError("Erro no trending");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultadoTmdb> c, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    // 3) Catálogo de filmes (com filtros opcionais — null = sem filtro)
    public void buscarFilmes(String ordenacao,
                             String generos,
                             Integer ano,
                             Double notaMinima,
                             Callback<List<TituloItem>> callback) {
        String ord = (ordenacao == null || ordenacao.isEmpty())
                ? "popularity.desc" : ordenacao;
        service.buscarFilmes(apiKey, IDIOMA, ord, generos, ano, notaMinima, 1)
                .enqueue(new retrofit2.Callback<ResultadoTmdb>() {
                    @Override
                    public void onResponse(Call<ResultadoTmdb> c, Response<ResultadoTmdb> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().resultados != null) {
                            List<TituloItem> lista = new ArrayList<>();
                            for (ItemTmdb i : r.body().resultados) {
                                i.mediaType = "movie";
                                lista.add(converter(i));
                            }
                            callback.onSuccess(lista);
                        } else {
                            callback.onError("Erro ao buscar filmes");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultadoTmdb> c, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    // 4) Catálogo de séries
    public void buscarSeries(String ordenacao,
                             String generos,
                             Integer ano,
                             Double notaMinima,
                             Callback<List<TituloItem>> callback) {
        String ord = (ordenacao == null || ordenacao.isEmpty())
                ? "popularity.desc" : ordenacao;
        service.buscarSeries(apiKey, IDIOMA, ord, generos, ano, notaMinima, 1)
                .enqueue(new retrofit2.Callback<ResultadoTmdb>() {
                    @Override
                    public void onResponse(Call<ResultadoTmdb> c, Response<ResultadoTmdb> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().resultados != null) {
                            List<TituloItem> lista = new ArrayList<>();
                            for (ItemTmdb i : r.body().resultados) {
                                i.mediaType = "tv";
                                lista.add(converter(i));
                            }
                            callback.onSuccess(lista);
                        } else {
                            callback.onError("Erro ao buscar séries");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultadoTmdb> c, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    // 5) Recomendações por gêneros (integração com o Ayran)
    public void buscarPorGeneros(List<Integer> generoIds, String tipo,
                                 Callback<List<TituloItem>> callback) {
        if (generoIds == null || generoIds.isEmpty()) {
            callback.onError("Nenhum gênero informado");
            return;
        }
        String ids = generoIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        if ("serie".equals(tipo)) {
            buscarSeries("popularity.desc", ids, null, null, callback);
        } else {
            buscarFilmes("popularity.desc", ids, null, null, callback);
        }
    }

    // 6) Detalhes de um título (filme ou série)
    public void buscarDetalhes(int id, String tipo, Callback<DetalheTmdb> callback) {
        Call<DetalheTmdb> chamada = "serie".equals(tipo)
                ? service.buscarDetalhesSerie(id, apiKey, IDIOMA)
                : service.buscarDetalhesFilme(id, apiKey, IDIOMA);

        chamada.enqueue(new retrofit2.Callback<DetalheTmdb>() {
            @Override
            public void onResponse(Call<DetalheTmdb> c, Response<DetalheTmdb> r) {
                if (r.isSuccessful() && r.body() != null) {
                    callback.onSuccess(r.body());
                } else {
                    callback.onError("Erro nos detalhes");
                }
            }

            @Override
            public void onFailure(Call<DetalheTmdb> c, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // 7) Elenco
    public void buscarElenco(int id, String tipo,
                             Callback<List<ElencoTmdb.MembroElenco>> callback) {
        Call<ElencoTmdb> chamada = "serie".equals(tipo)
                ? service.buscarElencoSerie(id, apiKey)
                : service.buscarElencoFilme(id, apiKey);

        chamada.enqueue(new retrofit2.Callback<ElencoTmdb>() {
            @Override
            public void onResponse(Call<ElencoTmdb> c, Response<ElencoTmdb> r) {
                if (r.isSuccessful() && r.body() != null && r.body().elenco != null) {
                    List<ElencoTmdb.MembroElenco> top = r.body().elenco.size() > 10
                            ? r.body().elenco.subList(0, 10)
                            : r.body().elenco;
                    callback.onSuccess(new ArrayList<>(top));
                } else {
                    callback.onError("Erro no elenco");
                }
            }

            @Override
            public void onFailure(Call<ElencoTmdb> c, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // 8) Onde assistir (BR)
    public void buscarOndeAssistir(int id, String tipo,
                                   Callback<List<ProvedoresTmdb.Provedor>> callback) {
        Call<ProvedoresTmdb> chamada = "serie".equals(tipo)
                ? service.buscarOndeAssistirSerie(id, apiKey)
                : service.buscarOndeAssistirFilme(id, apiKey);

        chamada.enqueue(new retrofit2.Callback<ProvedoresTmdb>() {
            @Override
            public void onResponse(Call<ProvedoresTmdb> c, Response<ProvedoresTmdb> r) {
                if (r.isSuccessful() && r.body() != null && r.body().resultados != null) {
                    ProvedoresTmdb.ResultadoPais br = r.body().resultados.get("BR");
                    List<ProvedoresTmdb.Provedor> lista = new ArrayList<>();
                    if (br != null && br.streaming != null) {
                        lista.addAll(br.streaming);
                    }
                    callback.onSuccess(lista);
                } else {
                    callback.onError("Erro em onde assistir");
                }
            }

            @Override
            public void onFailure(Call<ProvedoresTmdb> c, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // 9) Vídeos (trailers + trilha sonora)
    public void buscarVideos(int id, String tipo,
                             Callback<List<VideosTmdb.Video>> callback) {
        Call<VideosTmdb> chamada = "serie".equals(tipo)
                ? service.buscarVideosSerie(id, apiKey, IDIOMA)
                : service.buscarVideosFilme(id, apiKey, IDIOMA);

        chamada.enqueue(new retrofit2.Callback<VideosTmdb>() {
            @Override
            public void onResponse(Call<VideosTmdb> c, Response<VideosTmdb> r) {
                if (r.isSuccessful() && r.body() != null && r.body().videos != null) {
                    callback.onSuccess(r.body().videos);
                } else {
                    callback.onError("Erro nos vídeos");
                }
            }

            @Override
            public void onFailure(Call<VideosTmdb> c, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}