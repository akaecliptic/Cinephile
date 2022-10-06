package akaecliptic.dev.cinephile.Fragment;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import akaecliptic.dev.cinephile.Activity.MainActivity;
import akaecliptic.dev.cinephile.Adapter.List.CardSlimAdapter;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Super.BaseFragment;

public class WatchListFragment extends BaseFragment {

    static final String SELECTED_MOVIE = "SELECTED_MOVIE";
    static final String SELECTED_SAVED = "SELECTED_SAVED";
    static final String SELECTED_TYPE = "SELECTED_TYPE";

    @Override
    public void setResource() {
        this.resource = R.layout.fragment_watch_list;
    }

    @Override
    protected void initViews(View view) {
        this.viewModel.popular(1, movies -> {
            RecyclerView recyclerView = view.findViewById(R.id.watch_list_recycler);
            CardSlimAdapter adapter = new CardSlimAdapter(requireContext(), Arrays.asList(movies));

            adapter.setOnClickCheckbox((m, p) -> System.out.println("checking: " + m.isSeen()));
            adapter.setOnClickItem((m, p) -> {
                MainActivity activity = (MainActivity) requireActivity();
                Bundle bundle = new Bundle();
                bundle.putSerializable(SELECTED_MOVIE, m);
                activity.getNavigationController().navigate(R.id.movie_profile_fragment, bundle);
            });

            recyclerView.setAdapter(adapter);
        });
    }
}
