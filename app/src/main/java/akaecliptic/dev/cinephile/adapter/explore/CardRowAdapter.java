package akaecliptic.dev.cinephile.adapter.explore;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Set;

import akaecliptic.dev.cinephile.interaction.listener.ItemClickListener;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.base.BaseMovieAdapter;
import akaecliptic.dev.cinephile.wrapper.FooterMore;
import akaecliptic.dev.cinephile.wrapper.MovieCardRow;
import dev.akaecliptic.models.Configuration;
import dev.akaecliptic.models.Movie;

public class CardRowAdapter extends BaseMovieAdapter {

    private static final int CARD = 0;
    private static final int FOOTER = 1;

    private final Configuration configuration;
    private final Set<Integer> watchlist;
    private final String size;

    private ItemClickListener itemAddClickListener;
    private ItemClickListener itemClickListener;
    private OnClickListener moreClickListener;

    private boolean paginate;

    public CardRowAdapter(Context context, List<Movie> items, Configuration configuration, Set<Integer> watchlist) {
        super(context, items);
        this.configuration = configuration;
        this.size = configuration.posters()[1];
        this.watchlist = watchlist;
    }

    /*          METHODS          */

    public void setItemAddClickListener(ItemClickListener itemAddClickListener) {
        this.itemAddClickListener = itemAddClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setMoreClickListener(OnClickListener moreClickListener) {
        this.moreClickListener = moreClickListener;
    }

    public void setPaginate(boolean paginate) {
        this.paginate = paginate;
    }

    public void addItems(List<Movie> items) {
        int start = this.items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(start, items.size());
    }

    /*          OVERRIDES          */

    @NonNull
    @Override
    public BaseMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == CARD) {
            View view = this.inflater.inflate(R.layout.list_item_movie_card_row, parent, false);
            return new MovieCardRow(view);
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

        MovieCardRow card = (MovieCardRow) holder;
        Movie movie = this.items.get(position);

        card.setTitle(movie.getTitle());
        card.setYear(movie.getRelease().getYear());
        card.setImage(configuration.image(size, movie.getInfo().getPoster()));

        card.setRating(movie.getNativeRating());
        card.setSeen(movie.isSeen());
        card.setHeart(false);

        card.setOnClickListener(v -> itemClickListener.onClick(movie, position));
        card.setOnAddClickListener(v -> itemAddClickListener.onClick(movie, position));

        card.toggleAdd(!watchlist.contains(movie.getId()));
    }

    @Override
    public int getItemCount() {
        return (paginate) ? this.items.size() + 1 : this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == this.items.size()) return FOOTER;
        return CARD;
    }
}
