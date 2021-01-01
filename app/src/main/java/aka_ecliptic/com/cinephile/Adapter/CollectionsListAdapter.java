package aka_ecliptic.com.cinephile.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

public class CollectionsListAdapter extends RecyclerView.Adapter<CollectionsListAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textYear;
        TextView textTitle;
        TextView textRating;

        CheckBox checkSeen;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            textYear = itemView.findViewById(R.id.rci_mylist_tv_year);
            textTitle = itemView.findViewById(R.id.rci_mylist_tv_title);
            textRating = itemView.findViewById(R.id.rci_mylist_tv_rating);

            checkSeen = itemView.findViewById(R.id.rci_mylist_check);

            checkSeen.setOnClickListener((view) -> checkedBox.onItemClick(view, getAdapterPosition()));
            itemView.setOnClickListener((view) -> itemClick.onItemClick(view, getAdapterPosition()));
            itemView.setOnLongClickListener((view -> longClick.onLongClick(view, getAdapterPosition())));
        }
    }

    private List<Movie> mediaList;
    private LayoutInflater inflater;

    private ItemClickListener itemClick;
    private ItemClickListener checkedBox;
    private ItemLongClickListener longClick;

    public CollectionsListAdapter(Context context, List<Movie> list){
        this.inflater = LayoutInflater.from(context);
        mediaList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_item_my_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Media current = mediaList.get(position);

        viewHolder.textYear.setText(MediaObjectHelper.dateYear(current.getReleaseDate()));
        viewHolder.textTitle.setText(current.getTitle());
        viewHolder.textRating.setText(String.valueOf(current.getRating() * 10));
        viewHolder.checkSeen.setChecked(current.isSeen());
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public void setMediaList(List<Movie> list){
        this.mediaList = list;
        notifyDataSetChanged();
    }

    public Movie getItem(int position){
        return mediaList.get(position);
    }

    public void setItem(int position, Movie movie) {
        mediaList.set(position, movie);
        notifyItemChanged(position);
    }

    public void setItems(List<Movie> items){
        mediaList = items;
        notifyDataSetChanged();
    }

    public void setOnLongClickListener(ItemLongClickListener longClickListener) {
        longClick = longClickListener;
    }

    public void setClickListener(ItemClickListener itemClickListener){
        this.itemClick = itemClickListener;
    }

    public void setCheckBoxListener(ItemClickListener checkBoxListener){
        this.checkedBox = checkBoxListener;
    }

    public interface ItemLongClickListener{
        boolean onLongClick(View view, int position);
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }
}
