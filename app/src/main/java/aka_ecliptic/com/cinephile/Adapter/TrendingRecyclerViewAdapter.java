package aka_ecliptic.com.cinephile.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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

public class TrendingRecyclerViewAdapter extends RecyclerView.Adapter<TrendingRecyclerViewAdapter.ViewHolder>{

    private static final int VIEW_TYPE_FOOTER = 725;
    private static final int VIEW_TYPE_CELL = 527;

    private List<Media> mediaList;
    private LayoutInflater mInflater;
    private String imageConfig;
    private SelectedItemListener selectedListener;

    public TrendingRecyclerViewAdapter(Context context, List<Media> list, String imageConfig){
        this.mInflater = LayoutInflater.from(context);
        this.mediaList = list;
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        if(viewHolder.viewType == VIEW_TYPE_CELL) {
            Media tempMedia = mediaList.get(position);

            viewHolder.textTitle.setText(tempMedia.getTitle());
            Picasso.get().load(imageConfig + tempMedia.getImageData().getPosterImagePath()).
                    fit().centerCrop().into(viewHolder.imagePoster);

        }else{

            viewHolder.btnFooter.setText(R.string.option_more);
            viewHolder.btnFooter.setVisibility(View.VISIBLE);
            viewHolder.btnFooter.setEnabled(true);

            if(mediaList.size() == 0) {
                viewHolder.btnFooter.setVisibility(View.INVISIBLE);
                viewHolder.btnFooter.setEnabled(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mediaList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mediaList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    public Media getItem(int position) {
        return mediaList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        int viewType;

        TextView textTitle;
        ImageView imagePoster;
        Button btnFooter;


        ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            this.viewType = viewType;
            itemView.setOnClickListener(this);

            if(viewType == VIEW_TYPE_CELL) {
                textTitle = itemView.findViewById(R.id.trending_card_text_title);
                textTitle.setSelected(true);

                imagePoster = itemView.findViewById(R.id.trending_card_image_poster);
            }else{
                btnFooter = itemView.findViewById(R.id.footer_button);
            }
        }

        @Override
        public void onClick(View v) {
            if(selectedListener != null)
                selectedListener.onSelect(v, getAdapterPosition());
        }
    }

    public void setSelectedItemListener(SelectedItemListener sIL){
        this.selectedListener = sIL;
    }

    public interface SelectedItemListener {
        void onSelect(View view, int position);
    }
}
