package akaecliptic.dev.cinephile.wrapper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.base.BaseMovieAdapter.BaseMovieViewHolder;

public class MovieCardRow extends BaseMovieViewHolder {

    private final View parent;

    private TextView title;
    private TextView year;
    private ShapeableImageView image;

    private TextView rating;
    private ImageView seen;
    private ImageView heart;

    private ImageView add;

    public MovieCardRow(View parent) {
        super(parent);
        this.parent = parent;
        init();
    }

    private void init() {
        title = parent.findViewById(R.id.movie_card_row_text_title);
        year = parent.findViewById(R.id.movie_card_row_text_year);
        image = parent.findViewById(R.id.movie_card_row_image);

        rating = parent.findViewById(R.id.component_icons_rating);
        seen = parent.findViewById(R.id.component_icons_seen);
        heart = parent.findViewById(R.id.component_icons_heart);

        add = parent.findViewById(R.id.movie_card_row_button_add);
    }

    public View getView() {
        return this.parent;
    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.parent.setOnClickListener(clickListener);
    }

    public void setOnAddClickListener(OnClickListener clickListener) {
        this.add.setOnClickListener(clickListener);
    }

    public void toggleAdd(boolean show) {
        this.add.setVisibility((show) ? VISIBLE : GONE);
        this.add.setEnabled(show);
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
        this.seen.setVisibility((seen) ? VISIBLE : GONE);
    }

    public void setHeart(boolean heart) {
        this.heart.setVisibility((heart) ? VISIBLE : GONE);
    }
}
