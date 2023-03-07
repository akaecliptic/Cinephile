package akaecliptic.dev.cinephile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import akaecliptic.dev.cinephile.R;

@Deprecated
public class ViewCollectionsAdapter extends BaseAdapter {

    private List<String> collections;
    private Context context;
    private OnCollectionClick collectionClick;
    private OnCollectionLongClick collectionLongClick;

    public ViewCollectionsAdapter(Context context, List<String> collections) {
        this.collections = collections;
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View gridItem = view;

        if(gridItem == null)
            gridItem = LayoutInflater.from(context).inflate(R.layout.list_item_collection_card, parent, false);

        TextView title = gridItem.findViewById(R.id.rci_collection_title);
        title.setText(collections.get(position));

        gridItem.setOnClickListener(v -> collectionClick.click(position));
        gridItem.setOnLongClickListener(v -> collectionLongClick.click(position));

        return gridItem;
    }

    @Override
    public int getCount() {
        return collections.size();
    }

    @Override
    public String getItem(int pos) {
        return collections.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    public void setCollectionClick(OnCollectionClick collectionClick) {
        this.collectionClick = collectionClick;
    }

    public void setCollectionLongClick(OnCollectionLongClick collectionLongClick) {
        this.collectionLongClick = collectionLongClick;
    }

    public interface OnCollectionClick {
        void click(int position);
    }

    public interface OnCollectionLongClick {
        boolean click(int position);
    }
}
