package aka_ecliptic.com.cinephile.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import aka_ecliptic.com.cinephile.Adapter.MovieListAdapter;
import aka_ecliptic.com.cinephile.Architecture.MediaViewModel;
import aka_ecliptic.com.cinephile.Architecture.MovieApiDAO;
import aka_ecliptic.com.cinephile.MainActivity;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.R;
import aka_ecliptic.com.cinephile.SearchActivity;

import static aka_ecliptic.com.cinephile.Fragment.MyListFragment.SELECTED_MOVIE;
import static aka_ecliptic.com.cinephile.Fragment.MyListFragment.SELECTED_SAVED;
import static aka_ecliptic.com.cinephile.Fragment.MyListFragment.SELECTED_TYPE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment {

    private MediaViewModel mediaViewModel;
    private MovieApiDAO.MovieType movieType;

    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setUpViewModelLink();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_movie_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(requireActivity().getClass() == SearchActivity.class){
            setUpRecyclerSearch();
        }
        else if(requireActivity().getClass() == MainActivity.class){
            getBundle();
            setUpRecyclerMain();
        }
    }

    private void setUpViewModelLink() {
        mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
    }

    private void setUpRecyclerSearch() {
        SearchActivity.setRequestListener((movies, ids) -> {
            String imageConfig = mediaViewModel.getImageConfig(MovieApiDAO.ImageType.PROFILE);

            RecyclerView recyclerView = requireActivity().findViewById(R.id.movie_list_recycler);
            MovieListAdapter adapter = new MovieListAdapter(requireContext(), movies, imageConfig, ids);

            adapter.setItemClickListener((v, m) -> {
                Bundle bundle = new Bundle();

                bundle.putSerializable(SELECTED_MOVIE, m);
                bundle.putBoolean(SELECTED_SAVED, mediaViewModel.isMoviePresent(m.getId()));

                Navigation.findNavController(requireView())
                        .navigate(R.id.action_movie_list_fragment_to_movie_profile_fragment2, bundle);

                Snackbar snackbar = Snackbar.make(requireActivity().findViewById(R.id.search_coordinator),
                        "Opening '" + m.getTitle() + "'",
                        Snackbar.LENGTH_SHORT);
                snackbar.getView().setBackgroundColor(requireActivity().getColor(R.color.colorSecondaryDark));
                snackbar.show();
            });

            adapter.setAddClickListener((v, m) -> {
                mediaViewModel.addItem(m);

                adapter.updateItem(m);

                String message = "'" + m.getTitle() + "' Has been added to your list";
                Snackbar snackbar = Snackbar.make(requireActivity().findViewById(R.id.search_coordinator),
                        message,
                        Snackbar.LENGTH_SHORT);
                snackbar.getView().setBackgroundColor(requireActivity().getColor(R.color.colorSecondaryDark));
                snackbar.show();
            });

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        });
    }

    private void getBundle() {
        if(getArguments() != null) {
            movieType = (MovieApiDAO.MovieType) getArguments().getSerializable(SELECTED_TYPE);
        }
    }

    private void setUpRecyclerMain() {
        String imageConfig = mediaViewModel.getImageConfig(MovieApiDAO.ImageType.PROFILE);

        mediaViewModel.requestMoviesType(movieType, 1, movies -> {
            Set<Integer> savedSet = Arrays.stream(movies).filter(mediaViewModel.getItems()::contains).map(Media::getId).collect(Collectors.toSet());

            RecyclerView recyclerView = requireActivity().findViewById(R.id.movie_list_recycler);
            MovieListAdapter adapter = new MovieListAdapter(requireContext(), Arrays.asList(movies), imageConfig, savedSet);

            adapter.setItemClickListener((v, m) -> {
                Bundle bundle = new Bundle();

                bundle.putSerializable(SELECTED_MOVIE, m);
                bundle.putBoolean(SELECTED_SAVED, mediaViewModel.isMoviePresent(m.getId()));

                Navigation.findNavController(requireView())
                        .navigate(R.id.action_movie_list_fragment_to_movie_profile_fragment, bundle);

                Snackbar snackbar = Snackbar.make(requireActivity().findViewById(R.id.main_coordinator),
                        "Opening '" + m.getTitle() + "'",
                        Snackbar.LENGTH_SHORT);
                snackbar.getView().setBackgroundColor(requireActivity().getColor(R.color.colorSecondaryDark));
                snackbar.show();
            });

            adapter.setAddClickListener((v, m) -> {
                mediaViewModel.addItem(m);

                adapter.updateItem(m);

                String message = "'" + m.getTitle() + "' Has been added to your list";
                Snackbar snackbar = Snackbar.make(requireActivity().findViewById(R.id.main_coordinator),
                        message,
                        Snackbar.LENGTH_SHORT);
                snackbar.getView().setBackgroundColor(requireActivity().getColor(R.color.colorSecondaryDark));
                snackbar.show();
            });

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        });
    }
}
