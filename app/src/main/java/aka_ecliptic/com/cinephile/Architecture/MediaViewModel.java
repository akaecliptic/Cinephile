package aka_ecliptic.com.cinephile.Architecture;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import aka_ecliptic.com.cinephile.Fragment.ExploreFragment;
import aka_ecliptic.com.cinephile.Model.Movie;

import static aka_ecliptic.com.cinephile.Architecture.Repository.Sort;

public class MediaViewModel extends AndroidViewModel {

    private Repository mediaRepository;

    public MediaViewModel(@NonNull Application application) {
        super(application);
        mediaRepository = new Repository(application);
    }

    public List<Movie> reCacheItems(){
        return mediaRepository.reCacheItems();
    }

    public void cycleSort(){
        this.mediaRepository.cycleSort();
    }

    public Sort cycleSort(Sort sort){
        return this.mediaRepository.cycleSort(sort);
    }

    public String getCurrentSortString(){
        return this.mediaRepository.getSortString();
    }

    public Sort getCurrentSort(){
        return this.mediaRepository.getSortType();
    }

    public List<Movie> sortList(List<Movie> toSort, Sort sortBy) { return this.mediaRepository.sortList(toSort, sortBy); }

    public boolean isMoviePresent(int id){
        return mediaRepository.isMoviePresent(id);
    }

    public Movie getItem(int id){
        return mediaRepository.getItem(id);
    }

    public List<Movie> getItems() { return mediaRepository.getItems(); }

    public List<Movie> getItemsLike(String query) { return mediaRepository.getItemsLike(query); }

    public List<String> getCollectionHeadings() { return mediaRepository.getCollectionHeadings(); }

    public List<Movie> getItemsInCollection(String name) { return mediaRepository.getItemsInCollection(name); }

    public Movie[][] requestMovies() {
        return mediaRepository.getOnlineList();
    }

    public void requestMoviesType(MovieApiDAO.MovieType movieType, int page, ExploreFragment.RequestResult requestResult){
        mediaRepository.requestMoviesType(movieType, page, requestResult);
    }

    public void requestMoviesLike(String query, int page, ExploreFragment.RequestResult requestResult) {
        mediaRepository.requestOnlineListLike(query, page, requestResult);
    }

    public String getImageConfig(MovieApiDAO.ImageType imageType) {
        return mediaRepository.getImageConfig(imageType);
    }

    public int updateItem(Movie movie) { return mediaRepository.updateItem(movie); }

    public int deleteItem(Movie movie) { return mediaRepository.removeItem(movie); }

    public void addItem(Movie movie) { mediaRepository.addItem(movie); }
}
