package aka_ecliptic.com.cinephile.Adapter;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Handler;

import aka_ecliptic.com.cinephile.DataRepository.Repository;
import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.R;

public class RecyclerViewAdapterSearch extends RecyclerView.Adapter<RecyclerViewAdapterSearch.ViewHolder> implements Filterable {

    private List<Media> data;
    private List<Media> displayedData;
    private LayoutInflater mInflater;
    private SelectedItemListener selectedItemListener;
    private Context context;
    private List<Media> onlineResults;
    private boolean isOnline;

    public RecyclerViewAdapterSearch(Context context, List<Media> list){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.data = list;
        displayedData = new ArrayList<>(list);
        onlineResults = new ArrayList<>();
        isOnline = false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.recycler_search, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position){

            Media tempMedia = displayedData.get(position);
            viewHolder.yearTextView.setText(MediaObjectHelper.dateYear(tempMedia.getReleaseDate()));
            viewHolder.titleTextView.setText(tempMedia.getTitle());
            viewHolder.ratingTextView.setText(String.valueOf(tempMedia.getRating()));

            if (data.contains(tempMedia)) {
                viewHolder.addToListBtn.hide();
            } else {
                viewHolder.addToListBtn.show();
                viewHolder.addToListBtn.setOnClickListener(view -> {
                    Repository.addToDB(context, tempMedia);
                    data.add(tempMedia);
                    viewHolder.addToListBtn.hide();

                    Toast.makeText(context, "Added " + tempMedia.getTitle() + " to your list",
                            Toast.LENGTH_SHORT).show();
                });

            }
    }

    @Override
    public int getItemCount() {
        return displayedData.size();
    }

    public void addOnlineData(List<Media> list) {
        onlineResults.clear();
        onlineResults.addAll(list);
    }

    public void setOnline(boolean b) {
        isOnline = b;
    }

    public Media getItem(int position) {
        return displayedData.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView yearTextView;
        TextView titleTextView;
        TextView ratingTextView;
        FloatingActionButton addToListBtn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
                yearTextView = itemView.findViewById(R.id.searchTVYear);
                titleTextView = itemView.findViewById(R.id.searchTVTitle);
                titleTextView.setSelected(true);
                ratingTextView = itemView.findViewById(R.id.searchTVRating);

                addToListBtn = itemView.findViewById(R.id.searchAddButton);
                addToListBtn.getDrawable().mutate().setTint(context.getColor(R.color.colorAccent));
        }

        @Override
        public void onClick(View v) {
            if (selectedItemListener != null)
                selectedItemListener.onSelect(v, getAdapterPosition());
        }
    }

    @Override
    public Filter getFilter(){
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Media> filteredMedia = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredMedia.addAll(data);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                data.stream().filter(media -> media.getTitle().toLowerCase().trim().contains(filterPattern))
                        .forEach(filteredMedia::add);

                if(isOnline){
                    onlineResults.removeAll(data);
                    filteredMedia.addAll(onlineResults);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredMedia;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            displayedData.clear();
            displayedData.addAll((List<Media>) results.values);

            notifyDataSetChanged();
        }
    };

    public void setSelectedClickListener(SelectedItemListener sICL){
        selectedItemListener = sICL;
    }

    public interface SelectedItemListener {
        void onSelect(View view, int position);
    }
}
