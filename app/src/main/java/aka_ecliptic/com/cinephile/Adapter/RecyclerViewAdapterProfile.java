package aka_ecliptic.com.cinephile.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import aka_ecliptic.com.cinephile.Handler.TMDBHandler;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

public class RecyclerViewAdapterProfile extends RecyclerView.Adapter<RecyclerViewAdapterProfile.ViewHolder>{

    private Movie movie;
    private LayoutInflater mInflater;
    private Context context;
    private ViewHolder vw;
    private String posterConfig;
    private String backdropConfig;

    public RecyclerViewAdapterProfile(Context context, Movie movie){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;

        posterConfig = TMDBHandler.getInstance(context).getImageConfig("w185");
        backdropConfig = TMDBHandler.getInstance(context).getImageConfig("w1280");

        if(movie != null){
            this.movie = movie;
        } else{
            this.movie = new Movie(true, 2019, "Error Getting Movie", 99, Media.Genre.ACTION );
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_movie_profile_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        this.vw = viewHolder;
        viewHolder.yearTextView.setText(Integer.toString(movie.getYear()));
        viewHolder.titleTextView.setText(movie.getTitle());
        viewHolder.ratingTextView.setText(Integer.toString(movie.getRating()));
        viewHolder.seenBtn.getDrawable().mutate().setTint(seenColor());
        viewHolder.genre.setSelection(getDefaultSelected(movie.getGenre().name()));

        if(movie.getDescriptor() != null){
            viewHolder.descriptionTextView.setText(movie.getDescriptor().getDescription());
        }

        if(movie.getImageData() != null) {
            if (movie.getImageData().getPosterImagePath() != null) {
                Picasso.get().load(posterConfig + movie.getImageData().getPosterImagePath())
                        .fit().centerCrop().into(viewHolder.moviePoster);
            }
            if (movie.getImageData().getBackdropImagePath() != null) {
                Picasso.get().load(backdropConfig + movie.getImageData().getBackdropImagePath())
                        .fit().centerCrop().into(viewHolder.movieBackDrop);
            }
        }
    }

    private int getDefaultSelected(String mGenre){
        return vw.arrayAdapter.getPosition(mGenre);
    }

    private int seenColor() {
        if (movie.isSeen()){
            return Color.YELLOW;
        }else{
            return Color.GRAY;
        }
    }

    public Movie getEditedItem(){

        if(Integer.parseInt(vw.yearTextView.getText().toString()) != movie.getYear()){
            movie.setYear(Integer.parseInt(vw.yearTextView.getText().toString()));
        } else if(Integer.parseInt(vw.ratingTextView.getText().toString()) != movie.getRating()){
            movie.setRating(Integer.parseInt(vw.ratingTextView.getText().toString()));
        } else if(!vw.titleTextView.getText().toString().equals(movie.getTitle())){
            movie.setTitle(vw.titleTextView.getText().toString());
        } else if (!vw.genre.getSelectedItem().toString().equals(movie.getGenre().toString())){
            movie.setGenre(Media.Genre.valueOf(vw.genre.getSelectedItem().toString()));
        }
        return movie;
    }

    @Override
    public int getItemCount() {
        return 1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ArrayAdapter<CharSequence> arrayAdapter;

        EditText yearTextView;
        TextView titleTextView;
        EditText ratingTextView;
        TextView descriptionTextView;
        ImageView moviePoster;
        ImageView movieBackDrop;
        Spinner genre;
        FloatingActionButton seenBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            yearTextView = itemView.findViewById(R.id.movieProfileYear);
            titleTextView = itemView.findViewById(R.id.movieProfileTitle);
            titleTextView.setSelected(true);
            titleTextView.setOnLongClickListener(l -> createDialog());
            ratingTextView = itemView.findViewById(R.id.movieProfileRating);
            descriptionTextView = itemView.findViewById(R.id.movieProfileDesc);

            moviePoster = itemView.findViewById(R.id.movieProfileImage);
            movieBackDrop = itemView.findViewById(R.id.movieProfileBackdrop);

            seenBtn = itemView.findViewById(R.id.movieProfileSeen);
            genre = loadSpinner(itemView.findViewById(R.id.movieProfileGenre));

            seenBtn.setOnClickListener((View vw) -> {
                RecyclerViewAdapterProfile.this.movie.setSeen(!movie.isSeen());
                seenBtn.getDrawable().mutate().setTint(seenColor());
            });
        }

        private boolean createDialog() {

            Dialog dialogEdit = new Dialog(context);

            Button btnDone;
            Button btnCancel;
            TextView editPrompt;
            EditText movieTitle;
            String prompt = "Editing Title";

            dialogEdit.setContentView(R.layout.edit_movie_title_layout);

            btnCancel = dialogEdit.findViewById(R.id.btnEditCancel);
            btnDone = dialogEdit.findViewById(R.id.btnEditDone);
            editPrompt = dialogEdit.findViewById(R.id.textViewEditTitlePrompt);
            editPrompt.setText(prompt);
            movieTitle = dialogEdit.findViewById(R.id.textViewEditTitle);
            movieTitle.setText(movie.getTitle());

            btnDone.setOnClickListener((View vw) -> {
                String toChange = movieTitle.getText().toString();
                if(!movie.getTitle().equals(toChange)){
                    if(toChange.length() > 0) {
                        movie.setTitle(movieTitle.getText().toString());
                        notifyDataSetChanged();
                    }
                }
                closeKeyboard(vw);
                dialogEdit.dismiss();
            });

            btnCancel.setOnClickListener((View vw) -> {
                closeKeyboard(vw);
                dialogEdit.dismiss();
            });

            Objects.requireNonNull(dialogEdit.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            dialogEdit.show();
            return true;
        }

        private Spinner loadSpinner(Spinner spinner){
            arrayAdapter = ArrayAdapter.createFromResource(context,
                    R.array.media_genres, android.R.layout.simple_spinner_item);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);
            return spinner;
        }

        private void closeKeyboard(View v){
            if(v != null){
                InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
    //String getItemTitle(int id){
        //return data.get(id).getTitle();
}

