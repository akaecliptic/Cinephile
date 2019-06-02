package aka_ecliptic.com.cinephile.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Objects;

import aka_ecliptic.com.cinephile.Activity.MovieProfileActivity;
import aka_ecliptic.com.cinephile.Handler.GsonMovieConverter;
import aka_ecliptic.com.cinephile.Handler.SQLiteHandler;
import aka_ecliptic.com.cinephile.Handler.TMDBHandler;
import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Model.Genre;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;
import aka_ecliptic.com.cinephile.Adapter.RecyclerViewAdapterMyList;
import aka_ecliptic.com.cinephile.DataRepository.Repository;

public class MyListFragment extends Fragment implements RecyclerViewAdapterMyList.ItemClickListener {

    public static final String TAG = "MyListFragment";

    private FloatingActionButton sortBtn;

    private Dialog dialogAddMovie;
    private RecyclerViewAdapterMyList adapter;
    private SearchView searchView;

    private Repository<Media> repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_my_list, container, false);

        repository = new Repository<>(view.getContext());

        RecyclerView recyclerView = view.findViewById(R.id.myListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new RecyclerViewAdapterMyList(this.getContext(), repository.getItems());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        dialogAddMovie = new Dialog(inflater.getContext());

        searchView = view.findViewById(R.id.myListSearchView);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnClickListener(v -> searchView.setIconified(false));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                if(searchView.getQuery() == null || searchView.getQuery().length() <= 0){
                    searchView.setIconified(true);
                }
            }
        });

        //TODO: Rework implementation of adding offline movies.
        FloatingActionButton addBtn = view.findViewById(R.id.addActionButton);
        addBtn.getDrawable().mutate().setTint(getResources().getColor(R.color.colorAccent, null));
        addBtn.hide();
        //addBtn.setOnClickListener((View v) ->  this.addMovie());

        sortBtn = view.findViewById(R.id.sortActionButton);
        sortBtn.getDrawable().mutate().setTint(getResources().getColor(R.color.colorAccent, null));
        sortBtn.setOnClickListener((View v) -> sortMovies());

        return view;
    }

    private void sortMovies() {

        TypedArray icons = getResources().obtainTypedArray(R.array.sort_icons);

        int index = this.repository.getSortType().getSortIndex();
        index += 1;
        if(index >= icons.length())
            index = 0;

        repository.sortBySortType(Repository.Sort.valueOf(index).get());
        adapter.setData(repository.getItems());
        adapter.notifyDataSetChanged();

        sortBtn.setImageDrawable(icons.getDrawable(index));
        sortBtn.getDrawable().mutate().setTint(getResources().getColor(R.color.colorAccent, null));

        icons.recycle();
    }

    @Override
    public void onItemClick(View view, int position) {

        Bundle b = new Bundle();
        b.putSerializable(Movie.class.getName(), adapter.getItem(position));
        Intent newIntent = new Intent(view.getContext(), MovieProfileActivity.class);
        newIntent.putExtras(b);
        startActivityForResult(newIntent, 77);

        makeToast("Opening '" + adapter.getItem(position).getTitle() +"'");
    }

    private void makeToast(String message){
        Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void addMovie(){

        Button btnAdd;
        Button btnCancel;
        CheckBox seenAdd;
        EditText titleEdit;
        EditText yearEdit;
        EditText ratingEdit;
        Spinner spinnerEdit;

        dialogAddMovie.setContentView(R.layout.popup_add_movie);

        btnCancel = dialogAddMovie.findViewById(R.id.btnAddMovieCancel);
        btnAdd = dialogAddMovie.findViewById(R.id.btnAddMovieAccept);

        seenAdd = dialogAddMovie.findViewById(R.id.checkBoxAddSeen);

        titleEdit = dialogAddMovie.findViewById(R.id.editTextTitle);
        yearEdit = dialogAddMovie.findViewById(R.id.editTextYear);
        ratingEdit = dialogAddMovie.findViewById(R.id.editTextRating);
        spinnerEdit = loadSpinner(dialogAddMovie.findViewById(R.id.spinnerAdd));

        btnAdd.setOnClickListener((View v) -> {
            try{

                boolean seen = seenAdd.isChecked();
                String title = titleEdit.getText().toString();
                String year = yearEdit.getText().toString();
                int rating = Integer.parseInt(ratingEdit.getText().toString());
                String genre = spinnerEdit.getSelectedItem().toString();

                int index = repository.getItems().size();
                repository.addItem(new Movie(seen, MediaObjectHelper.parseDate(year), title, rating, Genre.valueOf(genre)), this.getContext());
                adapter.notifyItemInserted(index);

                makeToast("The Movie " + title + " has been added");
                dialogAddMovie.dismiss();
            }catch (Exception e){
                if(e instanceof NullPointerException || e instanceof NumberFormatException){
                    makeToast("please fill out all fields");
                }else{
                    dialogAddMovie.dismiss();
                    Log.d(TAG, "Exception "+ e +" found");
                }
            }
        });

        btnCancel.setOnClickListener((View v) -> dialogAddMovie.dismiss());
        Objects.requireNonNull(dialogAddMovie.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialogAddMovie.show();
    }

    private Spinner loadSpinner(Spinner spinner){
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(spinner.getContext(),
                R.array.media_genres, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        return spinner;
    }

   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==99)
        {
            Bundle b = data.getExtras();
            if (b != null){
                Movie temp = (Movie) b.getSerializable(Movie.class.getName());
                int index = repository.replaceItem(temp, this.getContext());
                if (index != -1){
                    adapter.getFilter().filter(searchView.getQuery().toString());
                    adapter.setData(repository.getItems());
                    adapter.notifyDataSetChanged();
                }else {
                    makeToast("There was an error when editing the movie");
                }

            }
        }
    }
}


