package akaecliptic.dev.cinephile.auxil.database;

import androidx.annotation.NonNull;

public enum Tables {
    MOVIES("movies"),
    MOVIE_DATA("movie_data"),
    COLLECTIONS("collections"),
    INFORMATION("movie_information"),
    COLLECTION_DATA("collection_data"),
    COLLECTION_MOVIE_DATA("collection_movie_data"),
    LINK_MOVIE_COLLECTION("link_movie_collection");

    private final String name;

    Tables(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
