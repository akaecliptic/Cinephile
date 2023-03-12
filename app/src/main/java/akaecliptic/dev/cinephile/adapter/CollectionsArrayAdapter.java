package akaecliptic.dev.cinephile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.interaction.listener.OnCollectionSelected;
import akaecliptic.dev.cinephile.model.Collection;

public class CollectionsArrayAdapter extends ArrayAdapter<Collection> {

    private final int movie;
    private final LayoutInflater inflater;
    private OnCollectionSelected collectionSelected;

    public CollectionsArrayAdapter(@NonNull Context context, int movie) {
        super(context, R.layout.list_item_collection, R.id.list_collection_text_title);
        this.inflater = LayoutInflater.from(context);
        this.movie = movie;
    }

    public void setItems(List<Collection> collections) {
        this.clear();
        this.addAll(collections);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        Collection working = this.getItem(position);

        if (listItem == null)
            listItem = this.inflater.inflate(R.layout.list_item_collection, parent, false);

        TextView collection = listItem.findViewById(R.id.list_collection_text_title);
        ImageView check = listItem.findViewById(R.id.list_collection_image_check);

        collection.setText(working.getName());
        check.setVisibility(working.hasMember(movie) ? View.VISIBLE : View.INVISIBLE);

        listItem.setOnClickListener(view -> {
            boolean isMember = working.hasMember(movie);

            if (isMember) {
                working.getMembers().remove(movie);
            } else {
                working.getMembers().add(movie);
            }

            this.collectionSelected.select(movie, working.getName(), working.hasMember(movie));
            check.setVisibility(working.hasMember(movie) ? View.VISIBLE : View.INVISIBLE);
        });

        return listItem;
    }

    public void setOnCollectionSelected(OnCollectionSelected collectionSelected) {
        this.collectionSelected = collectionSelected;
    }
}
