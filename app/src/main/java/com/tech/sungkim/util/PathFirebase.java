package com.tech.sungkim.util;


import com.google.firebase.auth.FirebaseAuth;

public class PathFirebase {

    public static final String PATH_FIREBASE = "https://shining-inferno-121.firebaseio.com/";

    public static final String PATH_USER = "users";

    private static String episodeKey;

    public static String getEpisodeKey() {
        return episodeKey;
    }

    public static void setEpisodeKey(String episodeKey) {
        PathFirebase.episodeKey = episodeKey;
    }
}
