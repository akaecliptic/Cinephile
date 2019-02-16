package akaecliptic.com.cinephile.Activity;

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

import akaecliptic.com.cinephile.Adapter.RecyclerViewAdapterTrending;
import akaecliptic.com.cinephile.DataRepository.Repository;
import akaecliptic.com.cinephile.Handler.GsonMovieConverter;
import akaecliptic.com.cinephile.Handler.TMDBHandler;
import akaecliptic.com.cinephile.Model.Media;
import akaecliptic.com.cinephile.Model.Movie;
import akaecliptic.com.cinephile.R;

public class TrendingFragment extends Fragment{

    private static final String TAG = "TrendingFragment";
    private RecyclerViewAdapterTrending adapter;
    private final Repository<Media> repository = new Repository<>();
    private TMDBHandler tmdbHandler;
    private int pageCount = 1;
    private int insertAmount;
    private TMDBHandler.TrendingType trendingType;
    private FloatingActionButton trendingBtn;
    private TextView trendingTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.activity_trending, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.trendingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        trendingTitle = view.findViewById(R.id.trendingRecyclerTitle);
        trendingTitle.setText(getResources().getStringArray(R.array.trending_type_titles)[0]);

        trendingBtn = view.findViewById(R.id.trendingActionButton);
        trendingBtn.getDrawable().mutate().setTint(getResources().getColor(R.color.colorAccent, null));
        trendingBtn.setOnClickListener((View v) -> switchTrendingType());

        trendingType = TMDBHandler.TrendingType.POPULAR;

        tmdbHandler = TMDBHandler.getInstance(this.getContext());
        tmdbHandler.getTrending(pageCount, trendingType, result -> {

            loadTrending(result);

            adapter = new RecyclerViewAdapterTrending(this.getContext(), repository.getItems(),
                                                        tmdbHandler.getImageConfig("w92"));
            adapter.setAddMoreClickListener((v)->
                tmdbHandler.getTrending(getNewPageCount(), trendingType, toAdd -> {
                    int before = adapter.getItemCount();
                    loadTrending(toAdd);
                    adapter.notifyItemRangeInserted(before, insertAmount);
                })
            );

            adapter.setSelectedItemListener((view1, position) -> {
                Bundle b = new Bundle();
                b.putSerializable(Movie.class.getName(), adapter.getItem(position));
                Intent newIntent = new Intent(view1.getContext(), MovieProfileActivity.class);
                newIntent.putExtras(b);
                startActivityForResult(newIntent, 0);

                Toast.makeText(view1.getContext(), "Opening " + adapter.getItem(position).getTitle(), Toast.LENGTH_SHORT).show();
            });
            recyclerView.setAdapter(adapter);
        });

        return view;
    }

    private void switchTrendingType() {

        TypedArray icons = getResources().obtainTypedArray(R.array.trending_type_icons);
        TypedArray titles = getResources().obtainTypedArray(R.array.trending_type_titles);

        int index = trendingType.ordinal();
        index += 1;
        if(index >= icons.length())
            index = 0;

        trendingType = TMDBHandler.TrendingType.values()[index];
        trendingTitle.setText(titles.getString(index));

        pageCount = 1;
        tmdbHandler.getTrending(pageCount, trendingType, result -> {
            repository.getItems().clear();
            loadTrending(result);
            adapter.setData(repository.getItems());
            adapter.notifyDataSetChanged();
        });

        trendingBtn.setImageDrawable(icons.getDrawable(index));
        trendingBtn.getDrawable().mutate().setTint(getResources().getColor(R.color.colorAccent, null));

        titles.recycle();
        icons.recycle();
    }

    private int getNewPageCount() {
        pageCount += 1;
        return pageCount;
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
            Toast.makeText(this.getContext(), "There was an error making request",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
