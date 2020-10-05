package aka_ecliptic.com.cinephile.Adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Set;

import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imagePoster;
        TextView textYear;
        TextView textTitle;
        TextView textRating;

        ImageButton btnAdd;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagePoster = itemView.findViewById(R.id.rci_movie_list_img_poster);
            textYear = itemView.findViewById(R.id.rci_movie_list_text_year);
            textTitle = itemView.findViewById(R.id.rci_movie_list_text_title);
            textRating = itemView.findViewById(R.id.rci_movie_list_text_rating);

            btnAdd = itemView.findViewById(R.id.rci_movie_list_button_add);

            btnAdd.setOnClickListener(view -> addClick.onItemClick(view, getItem(getAdapterPosition())));
            itemView.setOnClickListener(view -> itemClick.onItemClick(view, getItem(getAdapterPosition())));
        }
    }

    private List<Movie> mediaList;
    private Set<Integer> savedSet;
    private LayoutInflater mInflater;
    private String imageConfig;
    private ExploreAdapter.ItemClickListener itemClick;
    private ExploreAdapter.ItemClickListener addClick;

    public MovieListAdapter(Context context, @Nullable List<Movie> list, String imageConfig, Set<Integer> savedSet) {
        this.mInflater = LayoutInflater.from(context);
        this.mediaList = list;
        this.imageConfig = imageConfig;
        this.savedSet = savedSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_item_movie_list, parent, false);
        return new MovieListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Media current = mediaList.get(position);

        Picasso.get().load(imageConfig + current.getImageData().getPosterImagePath()).
                fit().centerCrop().into(viewHolder.imagePoster);
        viewHolder.textYear.setText(MediaObjectHelper.dateYear(current.getReleaseDate()));
        viewHolder.textTitle.setText(current.getTitle());

        String rating = current.getRating() + "/10";
        viewHolder.textRating.setText(rating);

        viewHolder.btnAdd.setVisibility((savedSet.contains(current.getId())) ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return (mediaList != null) ? mediaList.size() : 0;
    }

    private Movie getItem(int position) {
        return mediaList.get(position);
    }

    public void updateItem(Movie movie) {
        int index = mediaList.indexOf(movie);

        savedSet.add(movie.getId());

        notifyItemChanged(index);
    }

    public void setItemClickListener(ExploreAdapter.ItemClickListener itemClickListener){
        itemClick = itemClickListener;
    }

    public void setAddClickListener(ExploreAdapter.ItemClickListener itemClickListener){
        addClick = itemClickListener;
    }
}
