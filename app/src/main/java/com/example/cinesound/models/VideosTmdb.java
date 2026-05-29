package com.example.cinesound.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VideosTmdb {
    @SerializedName("results")
    public List<Video> videos;

    public static class Video {
        @SerializedName("id")    public String id;
        @SerializedName("key")   public String key;     // ID do YouTube
        @SerializedName("name")  public String nome;
        @SerializedName("type")  public String tipo;    // Trailer, Clip, etc
        @SerializedName("site")  public String site;    // YouTube, Vimeo
    }
}