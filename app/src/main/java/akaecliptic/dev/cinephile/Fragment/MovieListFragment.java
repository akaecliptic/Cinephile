package akaecliptic.dev.cinephile.Fragment;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import akaecliptic.dev.cinephile.Adapter.MovieListAdapter;
import akaecliptic.dev.cinephile.Architecture.MediaViewModel;
import akaecliptic.dev.cinephile.Architecture.MovieApiDAO;
import akaecliptic.dev.cinephile.Architecture.MovieApiDAO.MovieType;
import akaecliptic.dev.cinephile.MainActivity;
import akaecliptic.dev.cinephile.Model.Media;
import akaecliptic.dev.cinephile.Model.Movie;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.SearchActivity;

import static akaecliptic.dev.cinephile.Fragment.MyListFragment.SELECTED_MOVIE;
import static akaecliptic.dev.cinephile.Fragment.MyListFragment.SELECTED_SAVED;
import static akaecliptic.dev.cinephile.Fragment.MyListFragment.SELECTED_TYPE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment {

    private MediaViewModel mediaViewModel;
    private ArrayList<Movie> cachedMovies;
    private boolean lockPagination = false;
    private MovieType movieType;
    private int pageCount = 1;
    private int scrollPosition = 0;
    private String searchQuery;

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
        SearchActivity.setRequestListener((movies, ids, query) -> {
            String imageConfig = mediaViewModel.getImageConfig(MovieApiDAO.ImageType.PROFILE);
            if (pageCount == 1){
                cachedMovies = new ArrayList<>(movies);
            }else {
                List<String> movieTitles = cachedMovies.stream().map(Media::getTitle).collect(Collectors.toList());
                ids = new HashSet<>(mediaViewModel.getItemsLike(movieTitles));
            }

            searchQuery = query;

            RecyclerView recyclerView = requireActivity().findViewById(R.id.movie_list_recycler);
            MovieListAdapter adapter = new MovieListAdapter(requireContext(), cachedMovies, imageConfig, ids);

            adapter.setItemClickListener((v, m) -> {
                Bundle bundle = new Bundle();

                bundle.putSerializable(SELECTED_MOVIE, m);
                bundle.putBoolean(SELECTED_SAVED, mediaViewModel.isMoviePresent(m.getId()));

                scrollPosition = cachedMovies.indexOf(m);

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

            lockPagination = (movies.size() < 20) || lockPagination;
            adapter.setLockPagination(lockPagination);

            setUpPaginationSearch(adapter);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.scrollToPosition(scrollPosition);
        });
    }

    private void setUpPaginationSearch(MovieListAdapter adapter){
        adapter.setPaginateContent(() -> {
            pageCount++;
            mediaViewModel.requestMoviesLike(searchQuery, pageCount, movies -> {
                List<Movie> online = Arrays.asList(movies);
                List<String> movieTitles = online.stream().map(Media::getTitle).collect(Collectors.toList());
                List<Movie> movieList = new ArrayList<>(mediaViewModel.getItemsLike(searchQuery));
                Set<Integer> savedSet = new HashSet<>(mediaViewModel.getItemsLike(movieTitles));

                movieList.addAll(online.stream().filter( m -> !movieList.contains(m)).collect(Collectors.toList()));

                lockPagination = (pageCount == 5 || movies.length < 20);
                adapter.appendContent(movieList, savedSet, lockPagination);
            });
        });
    }

    private void getBundle() {
        if(getArguments() != null) {
            movieType = (MovieType) getArguments().getSerializable(SELECTED_TYPE);
        }
    }

    private void setUpRecyclerMain() {
        String imageConfig = mediaViewModel.getImageConfig(MovieApiDAO.ImageType.PROFILE);

        mediaViewModel.requestMoviesType(movieType, pageCount, movies -> {
            Set<Integer> savedSet = Arrays.stream(movies).filter(mediaViewModel.getItems()::contains).map(Media::getId).collect(Collectors.toSet());
            if(pageCount == 1)
                cachedMovies = new ArrayList<>(Arrays.asList(movies));

            RecyclerView recyclerView = requireActivity().findViewById(R.id.movie_list_recycler);
            MovieListAdapter adapter = new MovieListAdapter(requireContext(), cachedMovies, imageConfig, savedSet);

            adapter.setItemClickListener((v, m) -> {
                Bundle bundle = new Bundle();

                bundle.putSerializable(SELECTED_MOVIE, m);
                bundle.putBoolean(SELECTED_SAVED, mediaViewModel.isMoviePresent(m.getId()));

                scrollPosition = cachedMovies.indexOf(m);

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

            lockPagination = (movies.length < 9) || lockPagination;
            adapter.setLockPagination(lockPagination);

            setUpPaginationMain(adapter);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.scrollToPosition(scrollPosition);
        });
    }

    private void setUpPaginationMain(MovieListAdapter adapter){
        adapter.setPaginateContent(() -> {
            pageCount++;
            mediaViewModel.requestMoviesType(movieType, pageCount, movies -> {
                Set<Integer> savedSet = Arrays.stream(movies).filter(mediaViewModel.getItems()::contains).map(Media::getId).collect(Collectors.toSet());

                lockPagination = (pageCount == 5 || movies.length < 20);
                adapter.appendContent(Arrays.asList(movies), savedSet, lockPagination);
            });
        });
    }
}
