package akaecliptic.dev.cinephile.interaction.listener;

@FunctionalInterface
public interface OnCollectionSelected {
    void select(int movie, String collection, boolean add);
}