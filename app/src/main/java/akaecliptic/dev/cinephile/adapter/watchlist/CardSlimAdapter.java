package akaecliptic.dev.cinephile.adapter.watchlist;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.base.BaseMovieAdapter;
import akaecliptic.dev.cinephile.interaction.listener.ItemClickListener;
import dev.akaecliptic.models.Movie;

public class CardSlimAdapter extends BaseMovieAdapter {

    private ItemClickListener onClickCheckbox;
    private ItemClickListener onLongClickItem;

    public CardSlimAdapter(Context context, List<Movie> items) {
        super(context, items);
    }

    /*          METHODS          */

    public void setOnClickCheckbox(ItemClickListener onClickCheckbox) {
        this.onClickCheckbox = onClickCheckbox;
    }

    public void setOnLongClickItem(ItemClickListener onLongClickItem) {
        this.onLongClickItem = onLongClickItem;
    }

    /*          OVERRIDES          */

    @NonNull
    @Override
    public CardSlimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_movie_card_slim, parent, false);
        return new CardSlimViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseMovieViewHolder baseViewHolder, int position) {
        CardSlimViewHolder holder = (CardSlimViewHolder) baseViewHolder;
        Movie movie = items.get(position);

        holder.setCheck(movie.isSeen());
        holder.setYear(movie.getRelease().getYear());
        holder.setTitle(movie.getTitle());
        holder.setRating(movie.getUserRating());

        holder.setOnClickListener(view -> onClickItem.onClick(movie, position));
        holder.setOnLongClickListener(view -> {
            onLongClickItem.onClick(movie, position);
            return true;
        });
        holder.setOnCheckListener((view, value) -> {
            if(!view.isPressed()) return;

            movie.setSeen(value);
            onClickCheckbox.onClick(movie, position);
        });
    }

    /*          VIEW HOLDER          */

    static class CardSlimViewHolder extends BaseMovieViewHolder {

        private final View view;
        private final CheckBox checkbox;
        private final TextView year;
        private final TextView title;
        private final TextView rating;

        public CardSlimViewHolder(@NonNull View view) {
            super(view);

            this.view = view;
            this.checkbox = view.findViewById(R.id.movie_card_slim_checkbox);
            this.year = view.findViewById(R.id.movie_card_slim_text_year);
            this.title = view.findViewById(R.id.movie_card_slim_text_title);
            this.rating = view.findViewById(R.id.movie_card_slim_text_rating);
        }

        // LISTENERS
        public void setOnClickListener(OnClickListener onClickListener) {
            view.setOnClickListener(onClickListener);
        }

        public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
            view.setOnLongClickListener(onLongClickListener);
        }

        public void setOnCheckListener(OnCheckedChangeListener onCheckedChangeListener) {
            checkbox.setOnCheckedChangeListener(onCheckedChangeListener);
        }

        // SETTERS
        public void setCheck(boolean value) {
            checkbox.setChecked(value);
        }

        public void setYear(int year) {
            this.year.setText(String.valueOf(year));
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setRating(int rating) {
            this.rating.setText(String.valueOf(rating));
        }
    }
}
