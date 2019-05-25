package aka_ecliptic.com.cinephile.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import java.util.Date;
import java.util.Objects;

import aka_ecliptic.com.cinephile.Handler.TMDBHandler;
import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Model.Genre;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

public class MediaProfileFragment extends Fragment {
    public static final String TAG = "MediaProfileFragment";

    public static Media mediaObject;
    private String posterConfig;
    private String backdropConfig;

    ArrayAdapter<CharSequence> arrayAdapter;

    EditText yearTextView;
    TextView titleTextView;
    EditText ratingTextView;
    TextView descriptionTextView;
    ImageView moviePoster;
    ImageView movieBackDrop;
    Spinner genre;
    FloatingActionButton seenBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_movie_profile, container, false);

        setImageDataConfig(view);
        initialiseViews(view);
        bindData();

        return view;
    }

    private void initialiseViews(View view){
        yearTextView = view.findViewById(R.id.movieProfileYear);
        titleTextView = view.findViewById(R.id.movieProfileTitle);
        titleTextView.setSelected(true);
        titleTextView.setOnLongClickListener(l -> createDialog());
        ratingTextView = view.findViewById(R.id.movieProfileRating);
        descriptionTextView = view.findViewById(R.id.movieProfileDesc);

        moviePoster = view.findViewById(R.id.movieProfileImage);
        movieBackDrop = view.findViewById(R.id.movieProfileBackdrop);

        seenBtn = view.findViewById(R.id.movieProfileSeen);
        genre = loadSpinner(view.findViewById(R.id.movieProfileGenre));

        seenBtn.setOnClickListener((View vw) -> {
            mediaObject.setSeen(!mediaObject.isSeen());
            seenBtn.getDrawable().mutate().setTint(seenColor());
        });
    }

    private void bindData(){
        //TODO incorporate other sub-genres.
        yearTextView.setText(MediaObjectHelper.stringDate(mediaObject.getReleaseDate()));
        titleTextView.setText(mediaObject.getTitle());
        ratingTextView.setText(String.valueOf(mediaObject.getRating()));
        seenBtn.getDrawable().mutate().setTint(seenColor());
        genre.setSelection(getDefaultSelected(mediaObject.getGenre().name()));

        if(mediaObject.getDescriptor() != null){
            descriptionTextView.setText(mediaObject.getDescriptor().getDescription());
        }

        if(mediaObject.getImageData() != null) {
            if (mediaObject.getImageData().getPosterImagePath() != null) {
                Picasso.get().load(posterConfig + mediaObject.getImageData().getPosterImagePath())
                        .fit().centerCrop().into(moviePoster);
            }
            if (mediaObject.getImageData().getBackdropImagePath() != null) {
                Picasso.get().load(backdropConfig + mediaObject.getImageData().getBackdropImagePath())
                        .fit().centerCrop().into(movieBackDrop);
            }
        }
    }

    private int getDefaultSelected(String mGenre){
        return arrayAdapter.getPosition(mGenre);
    }

    private void setImageDataConfig(View view){
        posterConfig = TMDBHandler.getInstance(view.getContext()).getImageConfig("w185");
        backdropConfig = TMDBHandler.getInstance(view.getContext()).getImageConfig("w1280");
    }

    public static Media getMediaObject() {
        return mediaObject;
    }

    public static void setMediaObject(Media media) {
        if(media != null){
            mediaObject = media;
        } else{
            mediaObject = new Movie(true, new Date(), "Error Getting Movie", 99, Genre.ACTION );
        }
    }

    private boolean createDialog() {

        Dialog dialogEdit = new Dialog(this.getContext());

        Button btnDone;
        Button btnCancel;
        TextView editPrompt;
        EditText movieTitle;
        String prompt = "Editing Title";

        dialogEdit.setContentView(R.layout.popup_edit_movie_title);

        btnCancel = dialogEdit.findViewById(R.id.btnEditCancel);
        btnDone = dialogEdit.findViewById(R.id.btnEditDone);
        editPrompt = dialogEdit.findViewById(R.id.textViewEditTitlePrompt);
        editPrompt.setText(prompt);
        movieTitle = dialogEdit.findViewById(R.id.textViewEditTitle);
        movieTitle.setText(mediaObject.getTitle());

        btnDone.setOnClickListener((View vw) -> {
            String toChange = movieTitle.getText().toString();
            if(!mediaObject.getTitle().equals(toChange)){
                if(toChange.length() > 0) {
                    mediaObject.setTitle(movieTitle.getText().toString());
                    //TODO Update movie title
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
        arrayAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.media_genres, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        return spinner;
    }

    private void closeKeyboard(View v){
        if(v != null){
            InputMethodManager imm = (InputMethodManager)this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private int seenColor() {
        if (mediaObject.isSeen()){
            return Color.YELLOW;
        }else{
            return Color.GRAY;
        }
    }
}
