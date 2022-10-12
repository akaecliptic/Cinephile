package akaecliptic.dev.cinephile.Interface;

public interface SQLiteCallback<T> {

    /**
     * Passed to the SQLite Accessor for async queries.
     * Used to access data queried, and run procedures on main thread when received.
     *
     * @param response The data received from the query result.
     */
    void onResponse(T response);
}
