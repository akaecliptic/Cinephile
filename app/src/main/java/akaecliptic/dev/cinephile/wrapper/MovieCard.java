package akaecliptic.dev.cinephile.wrapper;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.base.BaseMovieAdapter.BaseMovieViewHolder;

public class MovieCard extends BaseMovieViewHolder {

    private final View parent;

    private TextView title;
    private TextView year;
    private ShapeableImageView image;

    private TextView rating;
    private ImageView seen;
    private ImageView heart;

    public MovieCard(View parent) {
        super(parent);
        this.parent = parent;
        init();
    }

    private void init() {
        title = parent.findViewById(R.id.movie_card_text_title);
        year = parent.findViewById(R.id.movie_card_text_year);
        image = parent.findViewById(R.id.movie_card_image);

        rating = parent.findViewById(R.id.component_icons_rating);
        seen = parent.findViewById(R.id.component_icons_seen);
        heart = parent.findViewById(R.id.component_icons_heart);
    }

    public View getView() {
        return this.parent;
    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.parent.setOnClickListener(clickListener);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setYear(int year) {
        this.year.setText(String.valueOf(year));
    }

    public void setImage(String url) {
        Picasso.get()
                .load(url)
                .fit()
                .centerCrop()
                .into(image);
    }

    public void setRating(int rating) {
        this.rating.setText(String.valueOf(rating));
    }

    public void setSeen(boolean seen) {
        this.seen.setVisibility((seen) ? View.VISIBLE : View.GONE);
    }

    public void setHeart(boolean heart) {
        this.heart.setVisibility((heart) ? View.VISIBLE : View.GONE);
    }
}
