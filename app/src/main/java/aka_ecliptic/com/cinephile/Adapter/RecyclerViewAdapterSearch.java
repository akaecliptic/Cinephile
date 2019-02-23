package aka_ecliptic.com.cinephile.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import aka_ecliptic.com.cinephile.Handler.SQLiteHandler;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position){

        SQLiteHandler sql = SQLiteHandler.getInstance(context);

            Media tempMedia = displayedData.get(position);
            viewHolder.yearTextView.setText(Integer.toString(tempMedia.getYear()));
            viewHolder.titleTextView.setText(tempMedia.getTitle());
            viewHolder.ratingTextView.setText(Integer.toString(tempMedia.getRating()));

            if (sql.isInDB(tempMedia)) {
                viewHolder.addToListBtn.setClickable(false);
                viewHolder.addToListBtn.setAlpha(.0f);
            } else {

                viewHolder.addToListBtn.setClickable(true);
                viewHolder.addToListBtn.setAlpha(1f);

                if (sql.isInDB(tempMedia.getTitle(), tempMedia.getYear())) {
                    viewHolder.addToListBtn.setImageDrawable(context.getDrawable(R.drawable.ic_update));
                    viewHolder.addToListBtn.getDrawable().mutate().setTint(context.getColor(R.color.colorAccent));

//                    viewHolder.addToListBtn.setOnClickListener(view -> {
//                        List<Media> temp = sql.getList().getItems();
//                        Media old = null;
//
//                        for (Media m : temp) {
//                            if (old == null)
//                                old = sql.getInDB(m, tempMedia);
//
//                        }
//
//                        if (old != null) {
//                            sql.updateEntryFromOnline(old.getId(), tempMedia);
//                            Toast.makeText(context, "Updated " + tempMedia.getTitle() + " in your list",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
                } else {

                    viewHolder.addToListBtn.setOnClickListener(view -> {

                        sql.newEntry(tempMedia);

                        Toast.makeText(context, "Added " + tempMedia.getTitle() + " to your list",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
    }

    @Override
    public int getItemCount() {
        return displayedData.size();
    }

    public void addData(List<Media> list) {
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

                for(Media media : data ){
                    if(media.getTitle().toLowerCase().contains(filterPattern)){
                        filteredMedia.add(media);
                    }
                }

                if(isOnline){
                    SQLiteHandler sql = SQLiteHandler.getInstance(context);
                    List<Media> removedDupedOnline = new LinkedList<>(onlineResults);
                    Iterator it = removedDupedOnline.iterator();
                    while(it.hasNext()){
                        Media temp = (Media) it.next();
                        if(sql.isInDB(temp)){
                            it.remove();
                        }
                    }
                    filteredMedia.addAll(removedDupedOnline);
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

    public void setData(List<Media> list){
        this.data = list;
        displayedData = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public void setSelectedClickListener(SelectedItemListener sICL){
        selectedItemListener = sICL;
    }

    public interface SelectedItemListener {
        void onSelect(View view, int position);
    }
}
