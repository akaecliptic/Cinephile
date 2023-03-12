package akaecliptic.dev.cinephile.interaction.listener;

import akaecliptic.dev.cinephile.interaction.callback.CallableCallback;

public interface OnCollectionCreated {
    void add(int movie, String collection, CallableCallback dismiss);
}
