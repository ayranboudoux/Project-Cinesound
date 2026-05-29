package com.example.cinesound.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
public class ProvedoresTmdb {
    @SerializedName("results")
    public Map<String, ResultadoPais> resultados;

    public static class ResultadoPais {
        @SerializedName("flatrate") public List<Provedor> streaming;
        @SerializedName("rent")    public List<Provedor> aluguel;
        @SerializedName("buy")     public List<Provedor> compra;
    }
    public static class Provedor {
        @SerializedName("provider_id") public int id;
        @SerializedName("provider_name") public String nome;
        @SerializedName("logo_path") public String logoPath;
    }
}
