package akaecliptic.dev.cinephile.Fragment;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import akaecliptic.dev.cinephile.Activity.MainActivity;
import akaecliptic.dev.cinephile.Adapter.List.CardSlimAdapter;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Super.BaseFragment;

public class WatchlistFragment extends BaseFragment {

    static final String SELECTED_MOVIE = "SELECTED_MOVIE";
    static final String SELECTED_SAVED = "SELECTED_SAVED";
    static final String SELECTED_TYPE = "SELECTED_TYPE";

    @Override
    public void setResource() {
        this.resource = R.layout.fragment_watchlist;
    }

    @Override
    protected void initViews(View view) {
        this.viewModel.subscribe(0, () -> {
            RecyclerView recyclerView = view.findViewById(R.id.watchlist_recycler);
            CardSlimAdapter adapter = new CardSlimAdapter(requireContext(), this.viewModel.watchlist());

            adapter.setOnClickCheckbox((movie, position) -> viewModel.updateSeen(movie));
            adapter.setOnClickItem((movie, position) -> {
                MainActivity activity = (MainActivity) requireActivity();
                Bundle bundle = new Bundle();
                bundle.putSerializable(SELECTED_MOVIE, movie);
                activity.getNavigationController().navigate(R.id.movie_profile_fragment, bundle);
            });

            recyclerView.setAdapter(adapter);
        });
    }
}
