package com.example.cinesound;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItemTmdb {
        @SerializedName("id")
        public int id;
        @SerializedName("title")
        public String titulo;      // filmes
        @SerializedName("name")
        public String nome;        // séries
        @SerializedName("poster_path")
        public String posterPath;
        @SerializedName("media_type")
        public String mediaType;   // "movie" ou "tv"
        @SerializedName("genre_ids")
        public List<Integer> generoIds;
        @SerializedName("vote_average")
        public double nota;
        @SerializedName("overview")
        public String sinopse;
        @SerializedName("release_date")
        public String dataLancamento;

}
