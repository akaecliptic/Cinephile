package aka_ecliptic.com.cinephile.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import java.util.Objects;

import aka_ecliptic.com.cinephile.Fragments.MediaProfileFragment;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

public class MovieProfileActivity extends AppCompatActivity {

    /**
     * Used to create activity, load movie object into profile fragment, and then add profile fragment
     * to fragment container.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        FloatingActionButton backBtn = findViewById(R.id.btnBackActivityMovie);
        backBtn.getDrawable().mutate().setTint(getResources().getColor(R.color.colorAccent, null));

        MediaProfileFragment.setMediaObject(getMovie());
        getSupportFragmentManager().beginTransaction().replace(R.id.profileContainer,
                new MediaProfileFragment()).commit();

        backBtn.setOnClickListener((View vw) -> endProcedure());
    }

    /**
     * Used to ensure endProcedure method is called when leaving activity through android back button.
     *
     * @see MovieProfileActivity#endProcedure()
     */
    @Override
    public void onBackPressed(){
        endProcedure();
    }

    /**
     * Called when closing this activity, used to run a check whether the viewed movie object was
     * edited or not. If so it attaches the edited movie object to a bundle.
     */
    private void endProcedure(){
        Intent intent = new Intent();
        if(!Objects.requireNonNull(getMovie()).equals2(MediaProfileFragment.getMediaObject())){

            Bundle b = new Bundle();
            b.putSerializable(Movie.class.getName(), MediaProfileFragment.getMediaObject());

            intent.putExtras(b);
            setResult(99, intent);
            finish();
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Used to pass the movie object of focus between activities, by passing through bundles.
     *
     * @return The Movie object to be viewed
     */
    private Movie getMovie(){
        Bundle b = this.getIntent().getExtras();
        if (b != null){
            return (Movie) b.getSerializable(Movie.class.getName());
        }else{
            return null;
        }
    }
}