package akaecliptic.com.cinephile.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import akaecliptic.com.cinephile.Handler.SQLiteHandler;
import akaecliptic.com.cinephile.Model.Media;
import akaecliptic.com.cinephile.R;

public class RecyclerViewAdapterTrending extends RecyclerView.Adapter<RecyclerViewAdapterTrending.ViewHolder>{

    private static final int VIEW_TYPE_FOOTER = 725;
    private static final int VIEW_TYPE_CELL = 527;
    private List<Media> data;
    private LayoutInflater mInflater;
    private ItemClickListener addClickListener;
    private Context context;
    private String imageConfig;
    private SelectedItemListener selectedListener;

    public RecyclerViewAdapterTrending(Context context, List<Media> list, String imageConfig){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.data = list;
        this.imageConfig = imageConfig;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == VIEW_TYPE_CELL){
            view = mInflater.inflate(R.layout.recycler_trending, parent, false);
        }else{
            view = mInflater.inflate(R.layout.recycler_footer, parent, false);
        }

        return new ViewHolder(view, viewType);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if(viewHolder.viewType == VIEW_TYPE_CELL) {
            Media tempMedia = data.get(position);
            viewHolder.yearTextView.setText(Integer.toString(tempMedia.getYear()));
            viewHolder.titleTextView.setText(tempMedia.getTitle());
            viewHolder.ratingTextView.setText(Integer.toString(tempMedia.getRating()));
            Picasso.get().load(imageConfig + tempMedia.getImageData().getPosterImagePath()).
                    fit().centerCrop().into(viewHolder.imageView);

            viewHolder.addToListBtn.setOnClickListener(view -> {

                SQLiteHandler.getInstance(context).newEntry(tempMedia);

                Toast.makeText(context, "Added " + tempMedia.getTitle() + " to your list",
                        Toast.LENGTH_SHORT).show();
            });
        }else{
            viewHolder.footerButton.setText("Load More");
            viewHolder.footerButton.setOnClickListener(addClickListener);
        }

    }

    @Override
    public int getItemCount() {
        return data.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == data.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    public Media getItem(int position) {
        return data.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView yearTextView;
        TextView titleTextView;
        TextView ratingTextView;
        ImageView imageView;
        FloatingActionButton addToListBtn;
        Button footerButton;
        int viewType;

        ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            itemView.setOnClickListener(this);
            if(viewType == VIEW_TYPE_CELL) {
                yearTextView = itemView.findViewById(R.id.trendingTVYear);
                titleTextView = itemView.findViewById(R.id.trendingTVTitle);
                titleTextView.setSelected(true);
                ratingTextView = itemView.findViewById(R.id.trendingTVRating);
                imageView = itemView.findViewById(R.id.trendingImageView);

                addToListBtn = itemView.findViewById(R.id.trendingAddButton);
                addToListBtn.getDrawable().mutate().setTint(context.getColor(R.color.colorAccent));
            }else{
                footerButton = itemView.findViewById(R.id.footerButton);
            }
        }

        @Override
        public void onClick(View v) {
            if(selectedListener != null)
                selectedListener.onSelect(v, getAdapterPosition());
        }
    }

    public void setData(List<Media> list){
        this.data = list;
    }

    public void setAddMoreClickListener(ItemClickListener itemClickListener){
        addClickListener = itemClickListener;
    }

    public void setSelectedItemListener(SelectedItemListener sIL){
        this.selectedListener = sIL;
    }

    public interface SelectedItemListener {
        void onSelect(View view, int position);
    }

    public interface ItemClickListener extends View.OnClickListener {
        void onClick(View view);
    }
}
