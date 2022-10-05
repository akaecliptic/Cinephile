package akaecliptic.dev.cinephile.Interface;

import dev.akaecliptic.models.Movie;

public interface ItemClickListener {

    /**
     * Used to intercept clicks for recyclerview adapters.
     *
     * @param movie The movie represented by the item clicked.
     * @param position The position of the movie in the adapter's items.
     */
    void onClick(Movie movie, int position);
}
