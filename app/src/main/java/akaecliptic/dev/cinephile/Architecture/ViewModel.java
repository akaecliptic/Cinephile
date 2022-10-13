package akaecliptic.dev.cinephile.Architecture;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import akaecliptic.dev.cinephile.Architecture.Repository.Repository;
import akaecliptic.dev.cinephile.Interface.CrossAccessorCallback;
import akaecliptic.dev.cinephile.Interface.SQLiteCallback;
import akaecliptic.dev.cinephile.Interface.TMDBCallback;
import dev.akaecliptic.models.Configuration;
import dev.akaecliptic.models.Information;
import dev.akaecliptic.models.Movie;
import dev.akaecliptic.models.Page;

/**
 * This class is responsible for maintaining scope of data between views, and serves a wrapper for
 * interfacing with {@link Repository} data.
 *
 * <p>A newer implementation of {@link MovieViewModel} (now deprecated).</p>
 */
public class ViewModel extends AndroidViewModel {

    /*
     * Data between activities is a little tricky with this view model.
     * Again, I don't really want to use something like the old system.
     * i.e Having subscribers that listened to changes.
     *
     * Instead a static pool is being used. The idea is that at any given time the pool should never contain
     * enough items for there to be performance penalty. And, if the pool is accessed, it will be drained.
     *
     * This should work for now, as there are only two activities hence; two instances. A better solution
     * will be needed.
     * 2022-10-12
     */
    private static final List<Movie> pool;

    private final Repository repository;

    public ViewModel(@NonNull Application application) {
        super(application);
        this.repository = new Repository(application);
    }

    /*          STATIC MEMBERS          */

    static {
        pool = new LinkedList<>();
    }

    public static List<Movie> drain() {
        List<Movie> temp = new LinkedList<>(pool);
        pool.clear();
        return temp;
    }

    public static void pool(Movie movie) {
        pool.add(movie);
    }

    public static List<Movie> pool() {
        return pool;
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

    public List<Movie> watchlist() {
        return this.repository.watchlist();
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

    public void insert(Pair<Integer, Information> information) {
        this.repository.insert(information);
    }

    public void movies(SQLiteCallback<List<Movie>> callback) {
        this.repository.movies(callback);
    }

    public Movie movie(int id) {
        return this.repository.movie(id);
    }

    public Information information(int id) {
        return this.repository.information(id);
    }

    public void updateMovie(Movie... movie) {
        this.repository.updateMovie(movie);
    }

    public void updateInformation(Map<Integer, Information> map) {
        this.repository.updateInformation(map);
    }

    public void deleteMovie(int id) {
        this.repository.deleteMovie(id);
    }

    public void deleteInformation(int id) {
        this.repository.deleteInformation(id);
    }

    public void updateSeen(Movie movie) {
        this.repository.updateSeen(movie);
    }

    public void updateRating(Movie movie) {
        this.repository.updateRating(movie);
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
