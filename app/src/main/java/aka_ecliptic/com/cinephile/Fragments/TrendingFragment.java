package aka_ecliptic.com.cinephile.Fragments;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import aka_ecliptic.com.cinephile.Activity.MovieProfileActivity;
import aka_ecliptic.com.cinephile.Adapter.RecyclerViewAdapterTrending;
import aka_ecliptic.com.cinephile.DataRepository.Repository;
import aka_ecliptic.com.cinephile.Handler.GsonMovieConverter;
import aka_ecliptic.com.cinephile.Handler.TMDBHandler;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

public class TrendingFragment extends Fragment implements RecyclerViewAdapterTrending.SelectedItemListener{

    private static final String TAG = "TrendingFragment";
    private RecyclerViewAdapterTrending adapter;
    private final Repository<Media> repository = new Repository<>();
    private TMDBHandler tmdbHandler;
    private int pageCount = 1;
    private int insertAmount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.trending_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        //tmdbHandler = TMDBHandler.getInstance(this.getContext());
        /*tmdbHandler.getTrending(pageCount, trendingType, result -> {
            loadTrending(result);
            adapter.notifyDataSetChanged();
        });*/

        adapter = new RecyclerViewAdapterTrending(this.getContext(), repository.getItems(),
                tmdbHandler.getImageConfig("w92"));

        /*adapter.setAddMoreClickListener((v)->
                tmdbHandler.getTrending(getNewPageCount(), trendingType, toAdd -> {
                    int before = adapter.getItemCount();
                    loadTrending(toAdd);
                    adapter.notifyItemRangeInserted(before, insertAmount);
                })
        );*/

        adapter.setSelectedItemListener(this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private int getNewPageCount() {
        return ++pageCount;
    }

    private void loadTrending(JSONObject result){
        try {
            JSONArray jsonArray = result.getJSONArray("results");
            for(insertAmount = 0; insertAmount < jsonArray.length(); insertAmount++){
                JSONObject jso = (JSONObject) jsonArray.get(insertAmount);
                Gson gson = GsonMovieConverter.getCustomGson();
                Movie m = gson.fromJson(jso.toString(),Movie.class);

                repository.getItems().add(m);
            }
        }catch (Exception e){
            Log.d(TAG,"Error "+ e + "found making an API request");
            Toast.makeText(this.getContext(), "Was not able to find any movies, check internet connection",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSelect(View view, int position) {
        Bundle b = new Bundle();
        b.putSerializable(Movie.class.getName(), adapter.getItem(position));
        Intent newIntent = new Intent(view.getContext(), MovieProfileActivity.class);
        newIntent.putExtras(b);
        startActivityForResult(newIntent, 0);

        Toast.makeText(view.getContext(), "Opening " + adapter.getItem(position).getTitle(), Toast.LENGTH_SHORT).show();
    }
}
