package aka_ecliptic.com.cinephile.Adapter;

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
import java.util.stream.Collectors;

import aka_ecliptic.com.cinephile.DataRepository.Repository;
import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

public class RecyclerViewAdapterTrending extends RecyclerView.Adapter<RecyclerViewAdapterTrending.ViewHolder>{

    private static final int VIEW_TYPE_FOOTER = 725;
    private static final int VIEW_TYPE_CELL = 527;
    private List<Media> data;
    private LayoutInflater mInflater;
    private ItemClickListener addClickListener;
    private Context context;
    private String imageConfig;
    private SelectedItemListener selectedListener;
    private Repository<Media> ref;

    public RecyclerViewAdapterTrending(Context context, List<Media> list, String imageConfig){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.data = list;
        this.imageConfig = imageConfig;
        ref = new Repository<>(context);
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {


        if(viewHolder.viewType == VIEW_TYPE_CELL) {
            Media tempMedia = data.get(position);

            viewHolder.yearTextView.setText(MediaObjectHelper.dateToString(tempMedia.getReleaseDate()));
            viewHolder.titleTextView.setText(tempMedia.getTitle());
            viewHolder.ratingTextView.setText(String.valueOf(tempMedia.getRating()));
            Picasso.get().load(imageConfig + tempMedia.getImageData().getPosterImagePath()).
                    fit().centerCrop().into(viewHolder.imageView);

            if(ref.getItems().contains(tempMedia)){
                viewHolder.addToListBtn.hide();
            }else if(ref.getItems().stream().anyMatch(m -> m.getId() == tempMedia.getId())){
                viewHolder.addToListBtn.show();

                viewHolder.addToListBtn.setImageDrawable(context.getDrawable(R.drawable.ic_update));
                viewHolder.addToListBtn.getDrawable().mutate().setTint(context.getColor(R.color.colorAccent));

                viewHolder.addToListBtn.setOnClickListener(view -> {

                    Movie m = (Movie) ref.getItems().parallelStream()
                            .filter(mov -> mov.getId() == tempMedia.getId())
                            .collect(Collectors.toList()).get(0);

                    tempMedia.setSeen(m.isSeen());
                    tempMedia.setRating(m.getRating());

                    ref.replaceItem(tempMedia, context);

                    viewHolder.addToListBtn.hide();
                    Toast.makeText(context, tempMedia.getTitle() + " has been updated",
                            Toast.LENGTH_SHORT).show();
                });
            }else {
                viewHolder.addToListBtn.show();

                viewHolder.addToListBtn.setImageDrawable(context.getDrawable(android.R.drawable.ic_input_add));
                viewHolder.addToListBtn.getDrawable().mutate().setTint(context.getColor(R.color.colorAccent));

                viewHolder.addToListBtn.setOnClickListener(view -> {
                    ref.addItem(tempMedia, context);
                    viewHolder.addToListBtn.hide();
                    Toast.makeText(context, "Added " + tempMedia.getTitle() + " to your list",
                            Toast.LENGTH_SHORT).show();
                });
            }
        }else{

            viewHolder.footerButton.setText(R.string.load_more);
            viewHolder.footerButton.setOnClickListener(addClickListener);
            viewHolder.footerButton.setVisibility(View.VISIBLE);
            viewHolder.footerButton.setEnabled(true);

            if(data.size() == 0) {
                viewHolder.footerButton.setVisibility(View.INVISIBLE);
                viewHolder.footerButton.setEnabled(false);
            }
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
