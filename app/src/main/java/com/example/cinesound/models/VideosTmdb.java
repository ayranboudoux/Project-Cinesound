package com.example.cinesound.models;

import java.util.List;

public class VideosTmdb {
    public List<Video> results;

    public static class Video {
        public String key;
        public String name;
        public String site;
        public String type;
    }
}
