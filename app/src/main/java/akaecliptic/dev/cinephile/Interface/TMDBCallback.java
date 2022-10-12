package akaecliptic.dev.cinephile.Interface;

@FunctionalInterface
public interface TMDBCallback<T> {

    /**
     * Passed to the TMDB Accessor for async queries.
     * Used to access data queried, and run procedures on main thread when received.
     *
     * @param response The data received from the query result.
     */
    void onResponse(T response);
}
