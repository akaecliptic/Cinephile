package akaecliptic.dev.cinephile.Super;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import akaecliptic.dev.cinephile.Interface.ItemClickListener;
import dev.akaecliptic.models.Movie;

/**
 * A personal implementation of Base class extending {@link RecyclerView.Adapter} class.
 * This is an attempt to abstract some of the common methods being reused. This will also serve as
 * a anatomy reference for future adapters.
 */
public abstract class BaseMovieAdapter extends RecyclerView.Adapter<BaseMovieAdapter.BaseMovieViewHolder> {

    private final String TAG = getClass().getSimpleName();

    protected ItemClickListener onClickItem;
    protected final LayoutInflater inflater;
    protected List<Movie> items;

    public BaseMovieAdapter(Context context, List<Movie> items) {
        this.items = items; // CONSIDER: May make this a reference of the items rather than the list itself.
        this.inflater = LayoutInflater.from(context);
    }

    /*          METHODS          */

    // GETTERS & SETTERS
    public void setOnClickItem(ItemClickListener onClickItem) {
        this.onClickItem = onClickItem;
    }

    public List<Movie> getItems() {
        return items;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Movie> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    /*          OVERRIDES          */

    @Override
    public int getItemCount() {
        return items.size();
    }

    /*          VIEW HOLDER          */

    public abstract static class BaseMovieViewHolder extends RecyclerView.ViewHolder {
        public BaseMovieViewHolder(@NonNull View view) {
            super(view);
        }
    }
}
