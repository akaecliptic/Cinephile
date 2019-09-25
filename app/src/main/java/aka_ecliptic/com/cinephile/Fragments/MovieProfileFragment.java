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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import aka_ecliptic.com.cinephile.Handler.TMDBHandler;
import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Model.Genre;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

public class MovieProfileFragment extends Fragment {
    public static final String TAG = "MovieProfileFragment";

    private static Media mediaObject;
    private String posterConfig;
    private String backdropConfig;

    private ArrayAdapter<CharSequence> arrayAdapter;

    private TextView textTitle;
    private TextView textReleaseDate;
    private TextView textDescription;

    private ImageView imagePoster;
    private ImageView imageBackDrop;

    private Spinner spinnerGenre1;
    private Spinner spinnerGenre2;
    private Spinner spinnerGenre3;

    private FloatingActionButton btnSeen;
    private FloatingActionButton btnMore;

    private FloatingActionButton btnRating;
    private TextView textRating;
    private ProgressBar progressRating;



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
        textReleaseDate = view.findViewById(R.id.profile_text_release_date);

        textTitle = view.findViewById(R.id.profile_text_title);
        textTitle.setSelected(true);
        textTitle.setOnLongClickListener(l -> createDialog());

        textRating = view.findViewById(R.id.profile_text_rating);
        textDescription = view.findViewById(R.id.profile_text_description);

        imagePoster = view.findViewById(R.id.profile_image_poster);
        imageBackDrop = view.findViewById(R.id.profile_image_backdrop);

        //Buttons
        btnSeen = view.findViewById(R.id.profile_button_seen);
        btnRating = view.findViewById(R.id.profile_button_rating);

        btnMore = view.findViewById(R.id.profile_button_more);

        spinnerGenre1 = loadSpinner(view.findViewById(R.id.profile_spinner_genre_1));
        spinnerGenre2 = loadSpinner(view.findViewById(R.id.profile_spinner_genre_2));
        spinnerGenre3 = loadSpinner(view.findViewById(R.id.profile_spinner_genre_3));

        btnSeen.setOnClickListener((View vw) -> {
            mediaObject.setSeen(!mediaObject.isSeen());
            btnSeen.getDrawable().mutate().setTint(seenColor());
        });
    }

    private void bindData(){
        textReleaseDate.setText(MediaObjectHelper.dateToString(mediaObject.getReleaseDate()));
        textTitle.setText(mediaObject.getTitle());

        textRating.setText(String.valueOf(mediaObject.getRating()));
        textRating.setOnEditorActionListener((v,a,k) -> {
            String userText = textRating.getText().toString();
            int userInput = MediaObjectHelper.checkInt(userText);
            if(userInput >= 0 && userInput <= 100){
                mediaObject.setRating(userInput);
                closeKeyboard(v);
            }else {
                textRating.setText(String.valueOf(mediaObject.getRating()));
                Toast.makeText(this.getContext(), "Invalid input, try again", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        btnSeen.getDrawable().mutate().setTint(seenColor());
        spinnerGenre1.setSelection(getDefaultSelected(mediaObject.getGenre().name()));
        spinnerGenre1.setOnItemSelectedListener(spinnerSelect);
        if(mediaObject instanceof Movie){
            spinnerGenre2.setSelection(getDefaultSelected(((Movie) mediaObject).getSubGenre().name()));
            spinnerGenre2.setOnItemSelectedListener(spinnerSelect);
            spinnerGenre3.setSelection(getDefaultSelected(((Movie) mediaObject).getMinGenre().name()));
            spinnerGenre3.setOnItemSelectedListener(spinnerSelect);
        }else {
            spinnerGenre2.setVisibility(View.INVISIBLE);
            spinnerGenre3.setVisibility(View.INVISIBLE);
        }

        if(mediaObject.getStatistic() != null){
            textDescription.setText(mediaObject.getStatistic().getDescription());
        }

        if(mediaObject.getImageData() != null) {
            if (mediaObject.getImageData().getPosterImagePath() != null) {
                Picasso.get().load(posterConfig + mediaObject.getImageData().getPosterImagePath())
                        .fit().centerCrop().into(imagePoster);
            }
            if (mediaObject.getImageData().getBackdropImagePath() != null) {
                Picasso.get().load(backdropConfig + mediaObject.getImageData().getBackdropImagePath())
                        .fit().centerCrop().into(imageBackDrop);
            }
        }
    }

    AdapterView.OnItemSelectedListener spinnerSelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(parent == spinnerGenre1) {
                mediaObject.setGenre(Genre.valueOf((String) parent.getSelectedItem()));
            }else if (parent == spinnerGenre2){
                ((Movie) mediaObject).setSubGenre(Genre.valueOf((String) parent.getSelectedItem()));
            }else if (parent == spinnerGenre3){
                ((Movie) mediaObject).setMinGenre(Genre.valueOf((String) parent.getSelectedItem()));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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

    public static void setMovie(Media media) {
        if(media != null){
            mediaObject = media;
        } else{
            mediaObject = new Movie();
        }
    }

    private boolean createDialog() {

        Dialog dialogEdit = new Dialog(Objects.requireNonNull(this.getContext()));

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
                if(!toChange.isEmpty()) {
                    mediaObject.setTitle(toChange);
                    textTitle.setText(toChange);
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
        arrayAdapter = ArrayAdapter.createFromResource(spinner.getContext(),
                R.array.movie_genres, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        return spinner;
    }

    private void closeKeyboard(View v){
        if(v != null){
            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    private int seenColor() {
        if (mediaObject.isSeen()){
            return Color.YELLOW;
        }else{
            return Color.GRAY;
        }
    }

    public static boolean checkRating(EditText view){
        String userText = view.getText().toString();
        int userInput = MediaObjectHelper.checkInt(userText);
        if(mediaObject.getRating() != userInput) {
            if (userInput >= 0 && userInput <= 100) {
                mediaObject.setRating(userInput);
                return true;
            } else {
                view.setText(String.valueOf(mediaObject.getRating()));
                Toast.makeText(view.getContext(), "Invalid input for rating, try again", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
