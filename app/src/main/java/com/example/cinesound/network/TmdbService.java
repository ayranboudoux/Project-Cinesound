package com.example.cinesound.network;

import com.example.cinesound.models.DetalheTmdb;
import com.example.cinesound.models.ElencoTmdb;
import com.example.cinesound.models.GenerosTmdb;
import com.example.cinesound.models.ProvedoresTmdb;
import com.example.cinesound.models.ResultadoTmdb;
import com.example.cinesound.models.VideosTmdb;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbService {

    @GET("search/multi")
    Call<ResultadoTmdb> buscarPorNome(
            @Query("api_key") String apiKey,
            @Query("query") String nome,
            @Query("language") String idioma,
            @Query("page") int pagina
    );

    @GET("trending/all/week")
    Call<ResultadoTmdb> buscarTrending(
            @Query("api_key") String apiKey,
            @Query("language") String idioma
    );

    @GET("discover/movie")
    Call<ResultadoTmdb> buscarFilmes(
            @Query("api_key") String apiKey,
            @Query("language") String idioma,
            @Query("sort_by") String ordenacao,
            @Query("with_genres") String generos,
            @Query("primary_release_year") Integer ano,
            @Query("vote_average.gte") Double notaMinima,
            @Query("page") int pagina
    );

    @GET("discover/tv")
    Call<ResultadoTmdb> buscarSeries(
            @Query("api_key") String apiKey,
            @Query("language") String idioma,
            @Query("sort_by") String ordenacao,
            @Query("with_genres") String generos,
            @Query("first_air_date_year") Integer ano,
            @Query("vote_average.gte") Double notaMinima,
            @Query("page") int pagina
    );

    @GET("movie/{id}")
    Call<DetalheTmdb> buscarDetalhesFilme(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String idioma
    );

    @GET("tv/{id}")
    Call<DetalheTmdb> buscarDetalhesSerie(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String idioma
    );

    @GET("movie/{id}/credits")
    Call<ElencoTmdb> buscarElencoFilme(
            @Path("id") int id,
            @Query("api_key") String apiKey
    );

    @GET("tv/{id}/credits")
    Call<ElencoTmdb> buscarElencoSerie(
            @Path("id") int id,
            @Query("api_key") String apiKey
    );

    @GET("movie/{id}/watch/providers")
    Call<ProvedoresTmdb> buscarOndeAssistirFilme(
            @Path("id") int id,
            @Query("api_key") String apiKey
    );

    @GET("tv/{id}/watch/providers")
    Call<ProvedoresTmdb> buscarOndeAssistirSerie(
            @Path("id") int id,
            @Query("api_key") String apiKey
    );

    @GET("movie/{id}/videos")
    Call<VideosTmdb> buscarVideosFilme(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String idioma
    );

    @GET("tv/{id}/videos")
    Call<VideosTmdb> buscarVideosSerie(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String idioma
    );

    @GET("genre/movie/list")
    Call<GenerosTmdb> buscarGenerosFilme(
            @Query("api_key") String apiKey,
            @Query("language") String idioma
    );

    @GET("genre/tv/list")
    Call<GenerosTmdb> buscarGenerosSerie(
            @Query("api_key") String apiKey,
            @Query("language") String idioma
    );
}