package akaecliptic.dev.cinephile.interaction.callback;

public interface UpdatedQueryCallback<T> {
    T query(T prev);
}
