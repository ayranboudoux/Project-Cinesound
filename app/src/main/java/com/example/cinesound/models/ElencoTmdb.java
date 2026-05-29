package com.example.cinesound.models;

import java.util.List;

public class ElencoTmdb {
    public List<Cast> cast;

    public static class Cast {
        public String name;
        public String character;
        public String profile_path;
    }
}
