package aka_ecliptic.com.cinephile.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import aka_ecliptic.com.cinephile.DataRepository.Repository;
import aka_ecliptic.com.cinephile.Handler.GsonMovieConverter;
import aka_ecliptic.com.cinephile.Handler.SQLiteHandler;
import aka_ecliptic.com.cinephile.Handler.TMDBHandler;
import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

public class RecyclerViewAdapterMyList extends RecyclerView.Adapter<RecyclerViewAdapterMyList.ViewHolder> implements Filterable {

    private List<Media> data;
    private List<Media> displayedData;
    private LayoutInflater mInflater;
    private ItemClickListener rcClickListener;
    private Context context;

    public RecyclerViewAdapterMyList(Context context, List<Media> list){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.data = list;
        displayedData = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_myList, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Media tempMedia = displayedData.get(position);
        viewHolder.id = tempMedia.getId();
        viewHolder.yearTextView.setText(MediaObjectHelper.dateYear(tempMedia.getReleaseDate()));
        viewHolder.titleTextView.setText(tempMedia.getTitle());
        viewHolder.ratingTextView.setText(String.valueOf(tempMedia.getRating()));
        viewHolder.seenCheckbox.setChecked(tempMedia.isSeen());
    }

    @Override
    public int getItemCount() {
        return displayedData.size();
    }

    @Override
    public Filter getFilter() {
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        int id;
        TextView yearTextView;
        TextView titleTextView;
        TextView ratingTextView;
        CheckBox seenCheckbox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            yearTextView = itemView.findViewById(R.id.myListTVYear);
            titleTextView = itemView.findViewById(R.id.myListTVTitle);
            titleTextView.setSelected(true);
            ratingTextView = itemView.findViewById(R.id.myListTVRating);
            seenCheckbox = itemView.findViewById(R.id.checkBoxSeen);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            seenCheckbox.setOnClickListener((View sv) ->{
                data.get(getAdapterPosition()).setSeen(seenCheckbox.isChecked());
                SQLiteHandler.getInstance(context).updateMovie(data.get(getAdapterPosition()));
                int index = data.indexOf(data.get(getAdapterPosition()));
                RecyclerViewAdapterMyList.this.notifyItemChanged(index);
            });
        }

        @Override
        public void onClick(View view) {
            if (rcClickListener != null)
                rcClickListener.onItemClick(view, getAdapterPosition());
        }

        private void removeItem(){
            Media tempMedia = data.get(getAdapterPosition());
            SQLiteHandler.getInstance(context).deleteMovie(tempMedia.getId());
            int index = data.indexOf(tempMedia);
            data.remove(index);
            displayedData = new ArrayList<>(data);
            notifyItemRemoved(index);
        }

        @Override
        public boolean onLongClick(View v) {

            Dialog dialogDelete = new Dialog(context);

            Button btnConfirm;
            Button btnCancel;
            TextView textViewDel;
            String prompt = "Do You want to delete '" + titleTextView.getText().toString() + "'?";

            dialogDelete.setContentView(R.layout.popup_movie_options);

            btnCancel = dialogDelete.findViewById(R.id.btnDelCancel);
            btnConfirm = dialogDelete.findViewById(R.id.btnDelete);
            textViewDel = dialogDelete.findViewById(R.id.textViewDelete);
            textViewDel.setText(prompt);

            btnConfirm.setOnClickListener((View vw) -> {
                removeItem();
                dialogDelete.dismiss();
            });

            btnCancel.setOnClickListener((View vw) -> {
                dialogDelete.dismiss();
            });
            Objects.requireNonNull(dialogDelete.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            dialogDelete.show();
            return true;
        }
    }

    public void setData(List<Media> list){
        this.data = list;
        this.displayedData = new ArrayList<>(list);
    }

    public Movie getItem(int id){
        return (Movie) displayedData.get(id);
    }

    public void setClickListener(ItemClickListener iCL){
        this.rcClickListener = iCL;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }
}
