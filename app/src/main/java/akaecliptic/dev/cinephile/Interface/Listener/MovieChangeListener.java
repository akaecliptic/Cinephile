package akaecliptic.dev.cinephile.Interface.Listener;

import dev.akaecliptic.models.Movie;

@FunctionalInterface
public interface MovieChangeListener {

    /**
     * Used to listen and respond to changes in a {@link Movie}.
     *
     * @param movie The movie being updated.
     */
    void onChange(Movie movie);
}
