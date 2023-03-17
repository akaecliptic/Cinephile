package akaecliptic.dev.cinephile.interaction.callback;

@FunctionalInterface
public interface UpdatedQueryCallback<T> {
    T query(T prev);
}
