package akaecliptic.dev.cinephile.Fragment;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;

import akaecliptic.dev.cinephile.Adapter.MovieListAdapter;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Super.BaseFragment;

public class WatchListFragment extends BaseFragment {

    static final String SELECTED_MOVIE = "SELECTED_MOVIE";
    static final String SELECTED_SAVED = "SELECTED_SAVED";
    static final String SELECTED_TYPE = "SELECTED_TYPE";


    @Override
    public void initAdapter(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.watch_list_recycler);
        MovieListAdapter adapter = new MovieListAdapter(requireContext(), new ArrayList<>(), "", new HashSet<>());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setResource() {
        resource = R.layout.fragment_watch_list;
    }
}
