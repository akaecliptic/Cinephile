package akaecliptic.dev.cinephile.Adapter.Explore;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;

import akaecliptic.dev.cinephile.Interface.ItemClickListener;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Super.BaseMovieAdapter;
import akaecliptic.dev.cinephile.Wrapper.FooterMore;
import akaecliptic.dev.cinephile.Wrapper.MovieCard;
import dev.akaecliptic.models.Configuration;
import dev.akaecliptic.models.Movie;

public class CardAdapter extends BaseMovieAdapter {

    private static final int CARD = 0;
    private static final int FOOTER = 1;

    private final Configuration configuration;

    private ItemClickListener itemClickListener;
    private OnClickListener moreClickListener;

    private final String size;

    public CardAdapter(Context context, List<Movie> items, Configuration configuration) {
        super(context, items);
        this.configuration = configuration;
        this.size = configuration.posters()[3];
    }

    /*          METHODS          */

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setMoreClickListener(OnClickListener moreClickListener) {
        this.moreClickListener = moreClickListener;
    }

    /*          OVERRIDES          */

    @NonNull
    @Override
    public BaseMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == CARD) {
            View view = this.inflater.inflate(R.layout.list_item_movie_card, parent, false);
            return new MovieCard(view);
        }

        View view = this.inflater.inflate(R.layout.list_item_footer_more, parent, false);
        return new FooterMore(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseMovieViewHolder holder, int position) {
        if (position == this.items.size()) {
            FooterMore footer = (FooterMore) holder;
            footer.setOnClickListener(moreClickListener);

            return;
        }

        MovieCard card = (MovieCard) holder;
        Movie movie = this.items.get(position);

        card.setTitle(movie.getTitle());
        card.setYear(movie.getRelease().getYear());
        card.setImage(configuration.image(size, movie.getInfo().getPoster()));

        card.setRating(movie.getNativeRating());
        card.setSeen(movie.isSeen());
        card.setHeart(false);

        card.setOnClickListener(v -> itemClickListener.onClick(movie, position));
    }

    @Override
    public int getItemCount() {
        return this.items.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == this.items.size()) return FOOTER;
        return CARD;
    }
}
