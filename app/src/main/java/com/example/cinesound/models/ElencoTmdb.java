package com.example.cinesound.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ElencoTmdb {
    @SerializedName("cast")
    public List<MembroElenco> elenco;

    public static class MembroElenco {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String nome;
        @SerializedName("character")
        public String personagem;
        @SerializedName("profile_path")
        public String fotoPath;
    }
}
