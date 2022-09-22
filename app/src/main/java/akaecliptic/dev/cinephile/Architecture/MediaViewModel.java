package akaecliptic.dev.cinephile.Architecture;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.List;

import akaecliptic.dev.cinephile.Fragment.ExploreFragment;
import akaecliptic.dev.cinephile.Model.Movie;

import static akaecliptic.dev.cinephile.Architecture.Repository.Sort;

public class MediaViewModel extends AndroidViewModel {

    private Repository mediaRepository;
    private List<OnNotifyClones> subscribers = new ArrayList<>();

    public MediaViewModel(@NonNull Application application) {
        super(application);
        mediaRepository = new Repository(application);
    }

    public List<Movie> reCacheItems(){
        return mediaRepository.reCacheItems();
    }

    public void cycleSort(){
        mediaRepository.cycleSort();
    }

    public Sort cycleSort(Sort sort){
        return mediaRepository.cycleSort(sort);
    }

    public String getCurrentSortString(){
        return mediaRepository.getSortString();
    }

    public Sort getCurrentSort(){
        return mediaRepository.getSortType();
    }

    public List<Movie> sortList(List<Movie> toSort, Sort sortBy) { return mediaRepository.sortList(toSort, sortBy); }

    public boolean isMoviePresent(int id){
        return mediaRepository.isMoviePresent(id);
    }

    public boolean isFavourited(int id){
        return mediaRepository.isFavourited(id);
    }

    public Movie getItem(int id){
        return mediaRepository.getItem(id);
    }

    public List<Movie> getItems() { return mediaRepository.getItems(); }

    public List<Movie> getItemsLike(String query) { return mediaRepository.getItemsLike(query); }

    public List<Integer> getItemsLike(List<String> titles) { return mediaRepository.getItemsLike(titles); }

    public List<String> getCollectionHeadings() { return mediaRepository.getCollectionHeadings(); }

    public void addCollection(String name) { this.mediaRepository.addCollection(name); }

    public List<String> getCollectionNames() { return mediaRepository.getCollectionNames(); }

    public List<String> getCollectionsIn(Movie movie) { return mediaRepository.getCollectionsIn(movie); }

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

    public void deleteCollection(String collection) {
        mediaRepository.deleteCollection(collection);
    }

    public void addItem(Movie movie) { mediaRepository.addItem(movie); }

    public void toggleFavourite(int id, boolean favourite) {
        mediaRepository.toggleFavourites(id, favourite);
    }

    public void toggleCollection(String name, int movieId, boolean set) {
        mediaRepository.toggleCollection(name, movieId, set);
    }

    public void addSubscriber(OnNotifyClones subscriber){
        subscribers.add(subscriber);
    }

    public void notifyClones(Movie update, boolean destructive){
        for (OnNotifyClones subscriber : subscribers) {
            subscriber.notifyClone(update, destructive);
        }
    }

    public interface OnNotifyClones {
        void notifyClone(Movie pos, boolean des);
    }
}
