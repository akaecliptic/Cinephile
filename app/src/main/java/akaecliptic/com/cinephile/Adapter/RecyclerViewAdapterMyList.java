package akaecliptic.com.cinephile.Adapter;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import akaecliptic.com.cinephile.Model.Media;
import akaecliptic.com.cinephile.Model.Movie;
import akaecliptic.com.cinephile.R;
import akaecliptic.com.cinephile.Handler.SQLiteHandler;

public class RecyclerViewAdapterMyList extends RecyclerView.Adapter<RecyclerViewAdapterMyList.ViewHolder> implements Filterable {

    private List<Media> data;
    private LayoutInflater mInflater;
    private ItemClickListener rcClickListener;
    private Context context;
    private List<Media> displayedData;

    public RecyclerViewAdapterMyList(Context context, List<Media> list){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.data = list;
        displayedData = new ArrayList<>(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_my_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Media tempMedia = displayedData.get(position);
        viewHolder.yearTextView.setText(Integer.toString(tempMedia.getYear()));
        viewHolder.titleTextView.setText(tempMedia.getTitle());
        viewHolder.ratingTextView.setText(Integer.toString(tempMedia.getRating()));
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

                for(Media media : data ){
                    if(media.getTitle().toLowerCase().contains(filterPattern)){
                        filteredMedia.add(media);
                    }
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView yearTextView;
        TextView titleTextView;
        TextView ratingTextView;
        CheckBox seenCheckbox;

        public ViewHolder(@NonNull View itemView) {
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
                SQLiteHandler.getInstance(context).updateEntry(data.get(getAdapterPosition()));
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
            SQLiteHandler.getInstance(context).deleteEntry(tempMedia.getId());
            int index = data.indexOf(tempMedia);
            data.remove(index);
            displayedData = new ArrayList<>(data);
            notifyItemRemoved(index);
        }

        @Override
        public boolean onLongClick(View v) {

            Dialog dialogDelete = new Dialog(context);

            Button btnAdd;
            Button btnCancel;
            TextView textViewDel;
            String prompt = "Do You want to delete '" + this.titleTextView.getText().toString() + "'" +
                    " or update from online";

            dialogDelete.setContentView(R.layout.options_movie_layout);

            btnCancel = dialogDelete.findViewById(R.id.btnDelCancel);
            btnAdd = dialogDelete.findViewById(R.id.btnDelete);
            textViewDel = dialogDelete.findViewById(R.id.textViewDelete);
            textViewDel.setText(prompt);

            btnAdd.setOnClickListener((View vw) -> {
                removeItem();
                dialogDelete.dismiss();
            });

            btnCancel.setOnClickListener((View vw) -> {
                //TODO Have switch to a popup
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

    public String getItemTitle(int id){
        return data.get(id).getTitle();
    }

    public Movie getItem(int id){
        return (Movie) data.get(id);
    }

    public void setClickListener(ItemClickListener iCL){
        this.rcClickListener = iCL;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }
}
