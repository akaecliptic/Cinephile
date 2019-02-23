package aka_ecliptic.com.cinephile.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import java.util.Objects;

import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;
import aka_ecliptic.com.cinephile.Adapter.RecyclerViewAdapterProfile;
import aka_ecliptic.com.cinephile.Handler.SQLiteHandler;

public class MovieProfileActivity extends AppCompatActivity {

    FloatingActionButton backBtn;
    RecyclerViewAdapterProfile adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        backBtn = findViewById(R.id.btnBackActivityMovie);
        backBtn.getDrawable().mutate().setTint(getResources().getColor(R.color.colorAccent, null));

        RecyclerView recyclerView = findViewById(R.id.activityMovieRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapterProfile(this, getMovie());
        recyclerView.setAdapter(adapter);

        backBtn.setOnClickListener((View vw) -> {
            endProcedure();
        });
    }

    @Override
    public void onBackPressed(){
        endProcedure();
    }

    private void endProcedure(){
        Intent intent = new Intent();
        if(!Objects.equals(getMovie(), adapter.getEditedItem())){

            Bundle b = new Bundle();
            b.putSerializable(Movie.class.getName(), adapter.getEditedItem());

            if(this.getIntent().getExtras() != null)
                this.getIntent().getExtras().clear();

            this.getIntent().putExtras(b);

            setResult(99, intent);
            finish();
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    private Movie getMovie(){
        Bundle b = this.getIntent().getExtras();
        if (b != null){
            return (Movie) b.getSerializable(Movie.class.getName());
        }else{
            return null;
        }
    }
}
