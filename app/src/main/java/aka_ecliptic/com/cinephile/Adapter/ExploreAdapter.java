package aka_ecliptic.com.cinephile.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import aka_ecliptic.com.cinephile.Architecture.MovieApiDAO;
import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

import static android.view.ViewGroup.LayoutParams;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder>{

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imagePoster;
        TextView textYear;
        TextView textTitle;

        Button btnFooter;

        int viewType;

        ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            if(viewType == VIEW_TYPE_CELL) {
                imagePoster = itemView.findViewById(R.id.rci_movie_card_img_poster);
                textYear = itemView.findViewById(R.id.rci_movie_card_tv_year);
                textTitle = itemView.findViewById(R.id.rci_movie_card_tv_title);

                itemView.setOnClickListener((view) -> itemClick.onItemClick(view, getItem(getAdapterPosition())));
            } else {
                btnFooter = itemView.findViewById(R.id.rci_footer_btn);
                btnFooter.setOnClickListener((view) -> buttonClick.onItemClick(view, movieType));
            }

            this.viewType = viewType;
        }
    }

    private static final int VIEW_TYPE_FOOTER = 725;
    private static final int VIEW_TYPE_CELL = 527;

    private List<Movie> mediaList;
    private LayoutInflater mInflater;
    private String imageConfig;
    private ItemClickListener itemClick;
    private MoreClickListener buttonClick;
    private MovieApiDAO.MovieType movieType;

    public ExploreAdapter(Context context, @Nullable List<Movie> list, String imageConfig, MovieApiDAO.MovieType movieType){
        this.mInflater = LayoutInflater.from(context);
        this.mediaList = list;
        this.imageConfig = imageConfig;
        this.movieType = movieType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == VIEW_TYPE_CELL){
            view = mInflater.inflate(R.layout.recycler_item_movie_card, parent, false);
        }else{
            view = mInflater.inflate(R.layout.recycler_item_footer, parent, false);
            view.getLayoutParams().height = LayoutParams.MATCH_PARENT ;
        }

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if(viewHolder.viewType == VIEW_TYPE_CELL) {
            Movie current = mediaList.get(position);

            if(current != null) {
                Picasso.get().load(imageConfig + current.getImageData().getPosterImagePath()).
                        fit().centerCrop().into(viewHolder.imagePoster);
                viewHolder.textYear.setText(MediaObjectHelper.dateYearVert(current.getReleaseDate()));
                viewHolder.textTitle.setText(current.getTitle());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mediaList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mediaList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    public void setItemList(List<Movie> list) {
        this.mediaList = list;
        notifyDataSetChanged();
    }

    private Movie getItem(int position) {
        return mediaList.get(position);
    }

    public void setClickListener(ItemClickListener itemClickListener){
        this.itemClick = itemClickListener;
    }

    public void setButtonClickListener(MoreClickListener buttonClickListener){
        this.buttonClick = buttonClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View view, Movie movie);
    }

    public interface MoreClickListener{
        void onItemClick(View view, MovieApiDAO.MovieType movieType);
    }
}
