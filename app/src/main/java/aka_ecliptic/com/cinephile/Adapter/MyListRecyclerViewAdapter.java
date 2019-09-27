package aka_ecliptic.com.cinephile.Adapter;

import android.app.Dialog;
import android.content.Context;
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

import aka_ecliptic.com.cinephile.Handler.SQLiteHandler;
import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

public class MyListRecyclerViewAdapter extends RecyclerView.Adapter<MyListRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<Media> mediaList;
    private List<Media> displayedData;
    private LayoutInflater mInflater;
    private ItemClickListener rcClickListener;
    private Context context;

    public MyListRecyclerViewAdapter(Context context, List<Media> list){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mediaList = list;
        displayedData = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_my_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Media tempMedia = displayedData.get(position);
        viewHolder.id = tempMedia.getId();
        viewHolder.textYear.setText(MediaObjectHelper.dateYear(tempMedia.getReleaseDate()));
        viewHolder.textTitle.setText(tempMedia.getTitle());
        viewHolder.textRating.setText(String.valueOf(tempMedia.getRating()));
        viewHolder.checkSeen.setChecked(tempMedia.isSeen());
    }

    @Override
    public int getItemCount() {
        return displayedData.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    /*private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Media> filteredMedia = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredMedia.addAll(mediaList);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                mediaList.stream().filter(media -> media.getTitle().toLowerCase().trim().contains(filterPattern))
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
    };*/

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        int id;

        TextView textYear;
        TextView textTitle;
        TextView textRating;

        CheckBox checkSeen;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            textYear = itemView.findViewById(R.id.myList_text_year);
            textTitle = itemView.findViewById(R.id.myList_text_title);
            textTitle.setSelected(true);
            textRating = itemView.findViewById(R.id.myList_text_rating);

            checkSeen = itemView.findViewById(R.id.myList_check);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            checkSeen.setOnClickListener((View sv) ->{
                mediaList.get(getAdapterPosition()).setSeen(checkSeen.isChecked());
                //TODO will new to change when future media objects are implemented
                SQLiteHandler.getInstance(context).updateMovie((Movie)mediaList.get(getAdapterPosition()));
                int index = mediaList.indexOf(mediaList.get(getAdapterPosition()));
                MyListRecyclerViewAdapter.this.notifyItemChanged(index);
            });
        }

        @Override
        public void onClick(View view) {
            if (rcClickListener != null)
                rcClickListener.onItemClick(view, getAdapterPosition());
        }

        private void removeItem(){
            Media tempMedia = mediaList.get(getAdapterPosition());
            SQLiteHandler.getInstance(context).deleteMovie(tempMedia.getId());
            int index = mediaList.indexOf(tempMedia);
            mediaList.remove(index);
            displayedData = new ArrayList<>(mediaList);
            notifyItemRemoved(index);
        }

        @Override
        public boolean onLongClick(View v) {

            Dialog dialogDelete = new Dialog(context);

            Button btnConfirm;
            Button btnCancel;
            TextView textPrompt;
            String prompt = "Do You want to delete '" + textTitle.getText().toString() + "'?";

            dialogDelete.setContentView(R.layout.popup_generic);

            btnCancel = dialogDelete.findViewById(R.id.popup_button_cancel);
            btnConfirm = dialogDelete.findViewById(R.id.popup_button_confirm);
            textPrompt = dialogDelete.findViewById(R.id.popup_text_message);
            textPrompt.setText(prompt);

            btnConfirm.setOnClickListener((View vw) -> {
                removeItem();
                dialogDelete.dismiss();
            });

            btnCancel.setOnClickListener((View vw) -> dialogDelete.dismiss());

            dialogDelete.show();
            return true;
        }
    }

    public void setMediaList(List<Media> list){
        this.mediaList = list;
        this.displayedData = new ArrayList<>(list);
    }

    public Media getItem(int id){
        return displayedData.get(id);
    }

    public void setClickListener(ItemClickListener iCL){
        this.rcClickListener = iCL;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }
}
