package aka_ecliptic.com.cinephile.Fragments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import aka_ecliptic.com.cinephile.Activity.MovieProfileActivity;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;
import aka_ecliptic.com.cinephile.Adapter.MyListRecyclerViewAdapter;
import aka_ecliptic.com.cinephile.DataRepository.Repository;

public class MyListFragment extends Fragment implements MyListRecyclerViewAdapter.ItemClickListener {

    public static final String TAG = "MyListFragment";

    private RecyclerView recyclerView;
    private MyListRecyclerViewAdapter adapter;
    private static Repository<Media> repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_my_list, container, false);

        initialiseViews(view);
        bindData();

        return view;
    }

    private void initialiseViews(View view) {

        repository = new Repository<>(view.getContext());

        recyclerView = view.findViewById(R.id.myList_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new MyListRecyclerViewAdapter(this.getContext(), repository.getItems());


    }

    private void bindData() {
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
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
                    adapter.setMediaList(repository.getItems());
                    adapter.notifyDataSetChanged();
                }else {
                    makeToast("There was an error when editing the movie");
                }

            }
        }
    }
}


