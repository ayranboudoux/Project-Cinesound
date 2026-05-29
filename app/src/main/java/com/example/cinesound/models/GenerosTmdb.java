package com.example.cinesound.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GenerosTmdb {
    @SerializedName("genres")
    public List<Genero> generos;

    public static class Genero {
        @SerializedName("id")   public int id;
        @SerializedName("name") public String nome;
    }
}