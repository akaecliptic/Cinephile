package akaecliptic.dev.cinephile.interaction.listener;

@FunctionalInterface
public interface OnDeleteListener<T> {
    void delete(T item);
}