package akaecliptic.dev.cinephile.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Map;

import akaecliptic.dev.cinephile.auxil.Sorter;
import akaecliptic.dev.cinephile.data.repository.Repository;
import akaecliptic.dev.cinephile.interaction.callback.CrossAccessorCallback;
import akaecliptic.dev.cinephile.interaction.callback.SQLiteCallback;
import akaecliptic.dev.cinephile.interaction.callback.TMDBCallback;
import akaecliptic.dev.cinephile.model.Collection;
import dev.akaecliptic.models.Configuration;
import dev.akaecliptic.models.Movie;
import dev.akaecliptic.models.Page;

/**
 * This class is responsible for maintaining scope of data between views, and serves a wrapper for
 * interfacing with {@link Repository} data.
 */
public class ViewModel extends AndroidViewModel {

    private static final Sorter sorter;

    private final Repository repository;

    public ViewModel(@NonNull Application application) {
        super(application);
        this.repository = new Repository(application);
    }

    /*          STATIC MEMBERS          */

    static {
        sorter = new Sorter();
    }

    public static String cycleSort(List<Movie> list) {
        sorter.cycle(list);
        return sorter.getMessage();
    }

    public static void sort(List<Movie> list) {
        sorter.sort(list);
    }

    public static void sort(List<Movie> list, Sorter.Sort sort) {
        sorter.sort(list, sort);
    }

    /*          CACHED GETTERS          */

    public List<Movie> upcoming() {
        return this.repository.upcoming();
    }

    public List<Movie> rated() {
        return this.repository.rated();
    }

    public List<Movie> popular() {
        return this.repository.popular();
    }

    public List<Movie> playing() {
        return this.repository.playing();
    }

    public Map<Integer, String> genres() {
        return this.repository.genres();
    }

    public Configuration config() {
        return this.repository.config();
    }

    public String[] backdrops() {
        return this.repository.config().backdrops();
    }

    public String[] posters() {
        return this.repository.config().posters();
    }

    public LiveData<List<Movie>> watchlist() {
        return this.repository.watchlist();
    }

    public LiveData<List<Collection>> collections() {
        return this.repository.collections();
    }

    public String image(String size, String path) {
        return this.repository.config().image(size, path);
    }

    /*          TMDB INTERFACE          */

    public void movie(int id, TMDBCallback<Movie> callback) {
        this.repository.movie(id, callback);
    }

    public void upcoming(int page, TMDBCallback<Page> callback) {
        this.repository.upcoming(page, callback);
    }

    public void rated(int page, TMDBCallback<Page> callback) {
        this.repository.rated(page, callback);
    }

    public void popular(int page, TMDBCallback<Page> callback) {
        this.repository.popular(page, callback);
    }

    public void playing(int page, TMDBCallback<Page> callback) {
        this.repository.playing(page, callback);
    }

    public void search(String param, int page, TMDBCallback<Page> callback) {
        this.repository.search(param, page, callback);
    }

    public void genres(TMDBCallback<Map<Integer, String>> callback) {
        this.repository.genres(callback);
    }

    public void config(TMDBCallback<Configuration> callback) {
        this.repository.config(callback);
    }

    /*          SQLITE INTERFACE          */

    public void insert(Movie movie) {
        this.repository.insert(movie);
    }

    public void movies(SQLiteCallback<List<Movie>> callback) {
        this.repository.movies(callback);
    }

    public void movie(int id, SQLiteCallback<Movie> callback) {
        this.repository.movie(id, callback);
    }

    public void deleteMovie(int id) {
        this.repository.deleteMovie(id);
    }

    public void updateSeen(Movie movie) {
        this.repository.updateSeen(movie);
    }

    public void updateRating(Movie movie) {
        this.repository.updateRating(movie);
    }

    public void collection(String name, SQLiteCallback<Collection> callback) {
        this.repository.collection(name, callback);
    }

    public void insertCollection(Collection collection) {
        this.repository.insertCollection(collection);
    }

    public void updateCollection(Collection collection) {
        this.repository.updateCollection(collection);
    }

    public void deleteCollection(String name) {
        this.repository.deleteCollection(name);
    }

    public void addToCollection(int id, String name) {
        this.repository.addToCollection(id, name);
    }

    public void removeFromCollection(int id, String name) {
        this.repository.removeFromCollection(id, name);
    }

    public void selectMoviesFromCollection(String name, SQLiteCallback<List<Movie>> callback) {
        this.repository.selectMoviesFromCollection(name, callback);
    }

    public void query(String query, SQLiteCallback<List<Movie>> callback) {
        this.repository.query(query, callback);
    }

    /*          MORE INTERFACE          */

    /*
        Actually, this is not necessary right now, given that watchlist is always stored in memory.
        There may be cases where a TMDB search does not overlap with an SQLite query.
        For that very niche case, I will keep this, although, it has yet to occur.

        2022-10-12
     */
    public void querySearch(String query, int page, CrossAccessorCallback<List<Movie>, Page> callback) {
        this.repository.querySearch(query, page, callback);
    }
}
