package akaecliptic.dev.cinephile.Architecture.Repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import akaecliptic.dev.cinephile.Architecture.Accessors.SQLite;
import akaecliptic.dev.cinephile.Architecture.Accessors.TMDB;
import akaecliptic.dev.cinephile.Architecture.MovieRepository;
import akaecliptic.dev.cinephile.Callback.TMDBCallback;
import dev.akaecliptic.models.Information;
import dev.akaecliptic.models.Movie;

/**
 * This class is responsible for interfacing with various underlying data accessors
 * {@link akaecliptic.dev.cinephile.Architecture.Accessors.SQLite} and
 * {@link akaecliptic.dev.cinephile.Architecture.Accessors.TMDB}. Responsibilities include:
 * <ul>
 *     <li>Accessing data</li>
 *     <li>Caching results</li>
 *     <li>Keeping operations off UI thread</li>
 * </ul>
 *
 * <p>A newer implementation of {@link MovieRepository} (now deprecated).</p>
 */
public class Repository {

    private final String TAG = getClass().getSimpleName();

    private static final int THREAD_POOL = 2;

    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL);
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final SQLite sqlite;
    private final TMDB tmdb;

    private Movie[] upcoming;
    private Movie[] rated;
    private Movie[] popular;
    private Movie[] playing;

    private Map<Integer, String> genres;

    private List<Movie> myList;

    public Repository(Context context) {
        this.sqlite = SQLite.getInstance(context);
        this.tmdb = new TMDB();
        initialise();
    }

    private void initialise() {
        executor.execute(() -> this.myList = this.sqlite.selectAll());
        executor.execute(() -> this.upcoming = this.tmdb.upcoming(1));
        executor.execute(() -> this.rated = this.tmdb.rated(1));
        executor.execute(() -> this.popular = this.tmdb.popular(1));
        executor.execute(() -> this.playing = this.tmdb.playing(1));
        executor.execute(() -> this.genres = this.tmdb.genre());
    }

    /*          MEMBER VARIABLE GETTERS          */

    public Movie[] upcoming() {
        return this.upcoming;
    }

    public Movie[] rated() {
        return this.rated;
    }

    public Movie[] popular() {
        return this.popular;
    }

    public Movie[] playing() {
        return this.playing;
    }

    public Map<Integer, String> genres() {
        return this.genres;
    }

    public List<Movie> myList() {
        return this.myList;
    }

    /*          TMDB INTERFACE          */

    public void movie(int id, TMDBCallback<Movie> callback) {
        executor.execute(() -> {
            Movie movie = this.tmdb.movie(id);
            handler.post(() -> callback.onResponse(movie));
        });
    }

    public void upcoming(int page, TMDBCallback<Movie[]> callback) {
        executor.execute(() -> {
            Movie[] movies = this.tmdb.upcoming(page);
            handler.post(() -> callback.onResponse(movies));
        });
    }

    public void rated(int page, TMDBCallback<Movie[]> callback) {
        executor.execute(() -> {
            Movie[] movies = this.tmdb.rated(page);
            handler.post(() -> callback.onResponse(movies));
        });
    }

    public void popular(int page, TMDBCallback<Movie[]> callback) {
        executor.execute(() -> {
            Movie[] movies = this.tmdb.popular(page);
            handler.post(() -> callback.onResponse(movies));
        });
    }

    public void playing(int page, TMDBCallback<Movie[]> callback) {
        executor.execute(() -> {
            Movie[] movies = this.tmdb.playing(page);
            handler.post(() -> callback.onResponse(movies));
        });
    }

    public void search(String param, int page, TMDBCallback<Movie[]> callback) {
        executor.execute(() -> {
            Movie[] movies = this.tmdb.search(param, page);
            handler.post(() -> callback.onResponse(movies));
        });
    }

    /*          SQLITE INTERFACE          */

    public Movie movie(int id) {
        return this.sqlite.selectMovie(id);
    }

    public Information information(int id) {
        return this.sqlite.selectInformation(id);
    }

    public void updateMovie(Movie... movie) {
        executor.execute(() -> this.sqlite.updateMovie(movie));
    }

    public void updateInformation(Map<Integer, Information> map) {
        executor.execute(() -> this.sqlite.updateInformation(map));
    }

    public void deleteMovie(int id) {
        executor.execute(() -> this.sqlite.deleteMovie(id));
    }

    public void deleteInformation(int id) {
        executor.execute(() -> this.sqlite.deleteInformation(id));
    }

}
