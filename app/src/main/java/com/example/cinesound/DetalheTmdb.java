package com.example.cinesound;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetalheTmdb {
        @SerializedName("id")
        public int id;
        @SerializedName("title")
        public String titulo;
        @SerializedName("name")
        public String nome;
        @SerializedName("overview")
        public String sinopse;
        @SerializedName("poster_path")
        public String posterPath;
        @SerializedName("vote_average")
        public double nota;
        @SerializedName("runtime")
        public int duracao;
        @SerializedName("release_date")
        public String dataLancamento;
        @SerializedName("genres")
        public List<GeneroPar> generos;
        public static class GeneroPar {
            @SerializedName("id")
            public int id;
            @SerializedName("name")
            public String nome;
        }
}