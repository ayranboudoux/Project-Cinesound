package com.example.cinesound.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class TituloItem {
    public int tmdbId;
    public String titulo;
    public String poster;
    public String tipo;   //filme ou serie
    public List<Integer> generos;
    public double notaTmdb;
    public String classificacao;   //preenchido so quando vem do firebase
    public Timestamp dataClassificacao;
}
