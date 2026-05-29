package com.example.cinesound.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetalheTmdb {

        // ===== Campos comuns (filmes e séries) =====

        @SerializedName("id")
        public int id;

        @SerializedName("title")
        public String titulo;            // vem em filmes

        @SerializedName("name")
        public String nome;              // vem em séries

        @SerializedName("original_title")
        public String tituloOriginal;    // filmes

        @SerializedName("original_name")
        public String nomeOriginal;      // séries

        @SerializedName("overview")
        public String sinopse;

        @SerializedName("poster_path")
        public String posterPath;

        @SerializedName("backdrop_path")
        public String backdropPath;      // imagem grande do topo (banner)

        @SerializedName("vote_average")
        public double nota;

        @SerializedName("vote_count")
        public int totalVotos;

        @SerializedName("popularity")
        public double popularidade;

        @SerializedName("original_language")
        public String idiomaOriginal;

        @SerializedName("status")
        public String status;            // "Released", "Returning Series", etc

        @SerializedName("tagline")
        public String slogan;

        @SerializedName("homepage")
        public String homepage;

        @SerializedName("genres")
        public List<GeneroPar> generos;

        @SerializedName("production_companies")
        public List<Produtora> produtoras;

        @SerializedName("production_countries")
        public List<Pais> paises;

        @SerializedName("spoken_languages")
        public List<Idioma> idiomas;

        // ===== Específicos de FILME =====

        @SerializedName("runtime")
        public int duracao;              // em minutos (filmes)

        @SerializedName("release_date")
        public String dataLancamento;    // filmes

        @SerializedName("budget")
        public long orcamento;

        @SerializedName("revenue")
        public long bilheteria;

        // ===== Específicos de SÉRIE =====

        @SerializedName("first_air_date")
        public String dataEstreia;       // séries

        @SerializedName("last_air_date")
        public String dataUltimoEpisodio;

        @SerializedName("number_of_seasons")
        public int totalTemporadas;

        @SerializedName("number_of_episodes")
        public int totalEpisodios;

        @SerializedName("episode_run_time")
        public List<Integer> duracaoEpisodios;   // séries (lista de durações)

        @SerializedName("in_production")
        public boolean emProducao;

        @SerializedName("created_by")
        public List<Criador> criadores;

        @SerializedName("networks")
        public List<Rede> redes;         // canais/streamings que produziram a série

        // ===== Classes internas =====

        public static class GeneroPar {
                @SerializedName("id")   public int id;
                @SerializedName("name") public String nome;
        }

        public static class Produtora {
                @SerializedName("id")            public int id;
                @SerializedName("name")          public String nome;
                @SerializedName("logo_path")     public String logoPath;
                @SerializedName("origin_country") public String pais;
        }

        public static class Pais {
                @SerializedName("iso_3166_1") public String codigo;
                @SerializedName("name")       public String nome;
        }

        public static class Idioma {
                @SerializedName("iso_639_1")    public String codigo;
                @SerializedName("name")         public String nome;
                @SerializedName("english_name") public String nomeIngles;
        }

        public static class Criador {
                @SerializedName("id")           public int id;
                @SerializedName("name")         public String nome;
                @SerializedName("profile_path") public String fotoPath;
        }

        public static class Rede {
                @SerializedName("id")             public int id;
                @SerializedName("name")           public String nome;
                @SerializedName("logo_path")      public String logoPath;
                @SerializedName("origin_country") public String pais;
        }

        // ===== Helpers (atalhos para usar na DetalhesActivity) =====

        /** Retorna o título (de filme) ou o nome (de série), o que vier preenchido. */
        public String getTituloOuNome() {
                return (titulo != null && !titulo.isEmpty()) ? titulo : nome;
        }

        /** Retorna a data certa (filme = release_date, série = first_air_date). */
        public String getDataPrincipal() {
                return (dataLancamento != null && !dataLancamento.isEmpty())
                        ? dataLancamento : dataEstreia;
        }

        /** Retorna só o ano de lançamento (4 primeiros chars de yyyy-MM-dd). */
        public String getAno() {
                String data = getDataPrincipal();
                if (data != null && data.length() >= 4) return data.substring(0, 4);
                return "";
        }

        /** Retorna a duração em minutos: filme = runtime, série = média do array. */
        public int getDuracaoMinutos() {
                if (duracao > 0) return duracao;
                if (duracaoEpisodios != null && !duracaoEpisodios.isEmpty()) {
                        return duracaoEpisodios.get(0);
                }
                return 0;
        }

        /** Junta todos os gêneros em uma string única, ex: "Ação, Drama, Suspense". */
        public String getGenerosComoTexto() {
                if (generos == null || generos.isEmpty()) return "";
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < generos.size(); i++) {
                        if (i > 0) sb.append(", ");
                        sb.append(generos.get(i).nome);
                }
                return sb.toString();
        }
}