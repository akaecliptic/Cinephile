package akaecliptic.dev.cinephile.Auxiliary.Database;

import androidx.annotation.NonNull;

public enum Tables {
    MOVIES("movies"),
    INFORMATION("movie_information"),
    MOVIE_DATA("movie_data");

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
