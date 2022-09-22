package akaecliptic.dev.cinephile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import akaecliptic.dev.cinephile.Helper.MediaObjectHelper;
import akaecliptic.dev.cinephile.Model.Media;
import akaecliptic.dev.cinephile.Model.Movie;
import akaecliptic.dev.cinephile.R;

import static android.view.ViewGroup.LayoutParams;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imagePoster;
        TextView textYear;
        TextView textTitle;
        TextView textRating;

        ImageButton btnAdd;
        Button btnFooter;

        int viewType;

        ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            if(viewType == VIEW_TYPE_CELL) {
                imagePoster = itemView.findViewById(R.id.rci_movie_list_img_poster);
                textYear = itemView.findViewById(R.id.rci_movie_list_text_year);
                textTitle = itemView.findViewById(R.id.rci_movie_list_text_title);
                textRating = itemView.findViewById(R.id.rci_movie_list_text_rating);

                btnAdd = itemView.findViewById(R.id.rci_movie_list_button_add);

                btnAdd.setOnClickListener(view -> addClick.onItemClick(view, getItem(getAdapterPosition())));
                itemView.setOnClickListener(view -> itemClick.onItemClick(view, getItem(getAdapterPosition())));
            }else{
                btnFooter = itemView.findViewById(R.id.rci_footer_btn);
                btnFooter.setOnClickListener((view) -> paginateContent.onRequest());
            }

            this.viewType = viewType;
        }
    }

    private static final int VIEW_TYPE_FOOTER = 725;
    private static final int VIEW_TYPE_CELL = 527;

    private ArrayList<Movie> mediaList;
    private Set<Integer> savedSet;
    private LayoutInflater mInflater;
    private String imageConfig;
    private ExploreAdapter.ItemClickListener itemClick;
    private ExploreAdapter.ItemClickListener addClick;
    private PaginateContent paginateContent;
    private boolean lockPagination = false;

    public MovieListAdapter(Context context, ArrayList<Movie> list, String imageConfig, Set<Integer> savedSet) {
        this.mInflater = LayoutInflater.from(context);
        this.mediaList = list;
        this.imageConfig = imageConfig;
        this.savedSet = savedSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == VIEW_TYPE_CELL){
            view = mInflater.inflate(R.layout.list_item_movie_list, parent, false);
        }else{
            view = mInflater.inflate(R.layout.list_item_footer, parent, false);
            view.getLayoutParams().width = LayoutParams.MATCH_PARENT;
        }

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if(viewHolder.viewType == VIEW_TYPE_CELL && position != mediaList.size()) {
            Media current = mediaList.get(position);

            Picasso.get().load(imageConfig + current.getImageData().getPosterImagePath()).
                    fit().centerCrop().into(viewHolder.imagePoster);
            viewHolder.textYear.setText(MediaObjectHelper.dateYear(current.getReleaseDate()));
            viewHolder.textTitle.setText(current.getTitle());

            String rating = current.getRating() + "/10";
            viewHolder.textRating.setText(rating);

            viewHolder.btnAdd.setVisibility((savedSet.contains(current.getId())) ? View.INVISIBLE : View.VISIBLE);
        }else if(lockPagination){
            viewHolder.itemView.setVisibility(View.GONE);
            viewHolder.itemView.getLayoutParams().height = 0;
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

    private Movie getItem(int position) {
        return mediaList.get(position);
    }

    public void updateItem(Movie movie) {
        int index = mediaList.indexOf(movie);

        savedSet.add(movie.getId());

        notifyItemChanged(index);
    }

    public void appendContent(List<Movie> movies, Set<Integer> saved, boolean lock) {
        int startIndex = mediaList.size() + 1;

        mediaList.addAll(movies);
        savedSet.addAll(saved);
        lockPagination = lock;

        int endIndex = mediaList.size();

        notifyItemRangeInserted(startIndex, endIndex);
    }

    public void setLockPagination(boolean lock){
        lockPagination = lock;
    }

    public void setItemClickListener(ExploreAdapter.ItemClickListener itemClickListener){
        itemClick = itemClickListener;
    }

    public void setAddClickListener(ExploreAdapter.ItemClickListener itemClickListener){
        addClick = itemClickListener;
    }

    public void setPaginateContent(PaginateContent paginate){
        paginateContent = paginate;
    }

    public interface PaginateContent{
        void onRequest();
    }
}
