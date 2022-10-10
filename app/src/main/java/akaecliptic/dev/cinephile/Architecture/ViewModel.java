package akaecliptic.dev.cinephile.Architecture;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;
import java.util.Map;

import akaecliptic.dev.cinephile.Architecture.Repository.Repository;
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

    private final Repository repository;

    public ViewModel(@NonNull Application application) {
        super(application);
        this.repository = new Repository(application);
    }

    /*          TMDB INTERFACE          */

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

    public String[] backdrops() {
        return this.repository.config().backdrops();
    }

    public String[] posters() {
        return this.repository.config().posters();
    }

    public List<Movie> watchlist() {
        return this.repository.watchlist();
    }

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

    public Configuration config() {
        return this.repository.config();
    }

    public String image(String size, String path) {
        return this.repository.config().image(size, path);
    }

    /*          SQLITE INTERFACE          */

    public void insert(Movie movie) {
        this.repository.insert(movie);
    }

    public void insert(Pair<Integer, Information> information) {
        this.repository.insert(information);
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
}
