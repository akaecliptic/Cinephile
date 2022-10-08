package akaecliptic.dev.cinephile.Adapter.Explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import akaecliptic.dev.cinephile.Interface.ItemClickListener;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Wrapper.MovieCard;
import dev.akaecliptic.models.Configuration;
import dev.akaecliptic.models.Movie;

public class CardAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final Configuration configuration;

    private ItemClickListener itemClickListener;

    private final String size;
    private List<Movie> items;

    public CardAdapter(Context context, List<Movie> items, Configuration configuration) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
        this.configuration = configuration;
        this.size = configuration.posters()[3];
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null) view = this.inflater.inflate(R.layout.list_item_movie_card, parent, false);

        MovieCard card = new MovieCard(view);
        Movie movie = this.items.get(position);

        card.setTitle(movie.getTitle());
        card.setYear(movie.getRelease().getYear());
        card.setImage(configuration.image(size, movie.getInfo().getPoster()));

        card.setRating(movie.getNativeRating());
        card.setSeen(movie.isSeen());
        card.setHeart(false);

        card.setOnClickListener(v -> itemClickListener.onClick(movie, position));

        return card.getView();
    }

    public void setItems(List<Movie> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /*          OVERRIDES          */

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.items.get(position).getId();
    }
}
