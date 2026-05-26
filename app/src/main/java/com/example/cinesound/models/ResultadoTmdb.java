package com.example.cinesound.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultadoTmdb {
    @SerializedName("results")
    public List<ItemTmdb> resultados;
    @SerializedName("total_pages")
    public int totalPaginas;
}
