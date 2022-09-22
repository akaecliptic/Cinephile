package akaecliptic.dev.cinephile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import akaecliptic.dev.cinephile.R;

public class CollectionsArrayAdapter<T extends String> extends ArrayAdapter<T> {

    private Context context;
    private List<T> collections;
    private List<Boolean> isPresent;
    private OnCollectionSelected collectionSelected;

    public CollectionsArrayAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<T> names, List<String> presentIn) {
        super(context, resource, textViewResourceId, names);
        this.context = context;

        collections = names;
        isPresent = new ArrayList<>();
        checkMovieInCollections(collections, presentIn);
    }

    private void checkMovieInCollections(List<T> collections, List<String> presentIn) {
        for (int i = 0; i < collections.size(); i++) {
            isPresent.add(i, presentIn.contains(collections.get(i)));
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item_collection, parent, false);

        TextView collection = listItem.findViewById(R.id.list_collection_text_title);
        ImageView check = listItem.findViewById(R.id.list_collection_image_check);

        collection.setText(collections.get(position));
        check.setVisibility(isPresent.get(position) ? View.VISIBLE : View.INVISIBLE);

        listItem.setOnClickListener(view -> {
            isPresent.set(position, !isPresent.get(position));
            collectionSelected.select(position, isPresent.get(position));
            check.setVisibility(isPresent.get(position) ? View.VISIBLE : View.INVISIBLE);
        });

        return listItem;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        isPresent.add(isPresent.size(), false);
    }

    public void setOnCollectionSelected(OnCollectionSelected collectionSelected){
        this.collectionSelected = collectionSelected;
    }

    public interface OnCollectionSelected {
        void select(int position, boolean set);
    }
}
