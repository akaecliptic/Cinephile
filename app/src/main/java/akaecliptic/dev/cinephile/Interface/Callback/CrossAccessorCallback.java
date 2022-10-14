package akaecliptic.dev.cinephile.Interface.Callback;

@FunctionalInterface
public interface CrossAccessorCallback<A, B> {

    /**
     * Passed to the TMDB and SQLite Accessor for async queries.
     * Amalgamates two separate queries, each from a data source into one result.
     * Used to access data queried, and run procedures on main thread when received.
     *
     * @param one The data received from one of the query sources.
     * @param two The data received from the other query sources.
     */
    void onResponses(A one, B two);
}
