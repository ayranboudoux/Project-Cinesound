package com.example.cinesound.network;

import com.example.cinesound.models.DetalheTmdb;
import com.example.cinesound.models.ResultadoTmdb;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbService {
    // Busca por nome (filmes e séries)
    @GET("search/multi")
    Call<ResultadoTmdb> buscarPorNome(
            @Query("api_key")
            String apiKey,
            @Query("query")
            String nome,
            @Query("language")
            String idioma,
            @Query("page")
            int pagina
    );
    // Filmes e séries em alta na semana
    @GET("trending/all/week")
    Call<ResultadoTmdb> buscarTrending(
            @Query("api_key")
            String apiKey,
            @Query("language")
            String idioma
    );
    // Catálogo de filmes com filtros
    @GET("discover/movie")
    Call<ResultadoTmdb> buscarFilmes(
            @Query("api_key")
            String apiKey,
            @Query("language")
            String idioma,
            @Query("sort_by")
            String ordenacao,
            @Query("with_genres")
            String generos,
            @Query("primary_release_year")
            Integer ano,
            @Query("vote_average.gte")
            Double notaMinima,
            @Query("page")
            int pagina
    );



    // Catálogo de séries com filtros

    @GET("discover/tv")
    Call<ResultadoTmdb> buscarSeries(
            @Query("api_key")
            String apiKey,
            @Query("language")
            String idioma,
            @Query("sort_by")
            String ordenacao,
            @Query("with_genres")
            String generos,
            @Query("first_air_date_year")
            Integer ano,
            @Query("vote_average.gte")
            Double notaMinima,
            @Query("page")
            int pagina
    );
    // Detalhes de um filme
    @GET("movie/{id}")
    Call<DetalheTmdb> buscarDetalhesFilme(
            @Path("id")
            int id,
            @Query("api_key")
            String apiKey,
            @Query("language")
            String idioma
    );
    // Detalhes de uma série
    @GET("tv/{id}")
    Call<DetalheTmdb> buscarDetalhesSerie(
            @Path("id")
            int id,
            @Query("api_key")
            String apiKey,
            @Query("language")
            String idioma
    );
    // Elenco de um filme
    @GET("movie/{id}/credits")
    Call<ElencoTmdb> buscarElencoFilme(
            @Path("id")
            int id,
            @Query("api_key")
            String apiKey
    );
    // Onde assistir (Netflix, Prime, etc.)
    @GET("movie/{id}/watch/providers")
    Call<ProvedoresTmdb> buscarOndeAssistir(
            @Path("id")
            int id,
            @Query("api_key")
            String apiKey
    );
    // Vídeos/trilha sonora
    @GET("movie/{id}/videos")
    Call<VideosTmdb> buscarVideos(
            @Path("id")        int id,
            @Query("api_key")  String apiKey,
            @Query("language") String idioma
    );
    // Lista de gêneros
    @GET("genre/movie/list")
    Call<GenerosTmdb> buscarGeneros(
            @Query("api_key")
            String apiKey,
            @Query("language")
            String idioma
    );
}
