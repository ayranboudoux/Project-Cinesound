package com.example.cinesound.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItemTmdb {

        @SerializedName("id")
        public int id;

        @SerializedName("title")
        public String titulo;          // vem em filmes

        @SerializedName("name")
        public String nome;            // vem em séries

        @SerializedName("poster_path")
        public String posterPath;

        @SerializedName("backdrop_path")
        public String backdropPath;

        @SerializedName("media_type")
        public String mediaType;       // "movie" ou "tv"

        @SerializedName("genre_ids")
        public List<Integer> generoIds;

        @SerializedName("vote_average")
        public double nota;

        @SerializedName("vote_count")
        public int totalVotos;

        @SerializedName("overview")
        public String sinopse;

        @SerializedName("release_date")
        public String dataLancamento;  // filmes

        @SerializedName("first_air_date")
        public String dataEstreia;     // séries

        @SerializedName("original_language")
        public String idiomaOriginal;

        @SerializedName("popularity")
        public double popularidade;
}