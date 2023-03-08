package akaecliptic.dev.cinephile.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import akaecliptic.dev.cinephile.data.accessor.SQLite;
import akaecliptic.dev.cinephile.data.accessor.TMDB;
import akaecliptic.dev.cinephile.data.MovieRepository;
import akaecliptic.dev.cinephile.interaction.callback.CrossAccessorCallback;
import akaecliptic.dev.cinephile.interaction.callback.SQLiteCallback;
import akaecliptic.dev.cinephile.interaction.callback.TMDBCallback;
import akaecliptic.dev.cinephile.model.Collection;
import dev.akaecliptic.models.Configuration;
import dev.akaecliptic.models.Information;
import dev.akaecliptic.models.Movie;
import dev.akaecliptic.models.Page;

/**
 * This class is responsible for interfacing with various underlying data accessors
 * {@link akaecliptic.dev.cinephile.data.accessor.SQLite} and
 * {@link akaecliptic.dev.cinephile.data.accessor.TMDB}. Responsibilities include:
 * <ul>
 *     <li>Accessing data</li>
 *     <li>Caching results</li>
 *     <li>Keeping operations off UI thread</li>
 * </ul>
 *
 * <p>A newer implementation of {@link MovieRepository} (now deprecated).</p>
 */
public class Repository {

    private static final int THREAD_POOL = 2;

    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL);
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final SQLite sqlite;
    private final TMDB tmdb;

    private List<Movie> upcoming;
    private List<Movie> rated;
    private List<Movie> popular;
    private List<Movie> playing;

    private Map<Integer, String> genres;
    private Configuration configuration;

    private List<Movie> watchlist;
    private List<Collection> collections;

    public Repository(Context context) {
        this.sqlite = SQLite.getInstance(context);
        this.tmdb = new TMDB();
        setup();
        init();
    }

    private void setup() {
        this.upcoming = new ArrayList<>();
        this.rated = new ArrayList<>();
        this.popular = new ArrayList<>();
        this.playing = new ArrayList<>();

        this.watchlist = new ArrayList<>();
        this.collections = new ArrayList<>();
    }

    private void init() {
        executor.execute(() -> {
            this.watchlist.addAll(this.sqlite.selectMovies());
            this.collections.addAll(this.sqlite.selectCollections());
        });

        executor.execute(() -> {
            this.upcoming.addAll(this.tmdb.upcoming(1).results());
            this.rated.addAll(this.tmdb.rated(1).results());
            this.popular.addAll(this.tmdb.popular(1).results());
            this.playing.addAll(this.tmdb.playing(1).results());
        });

        executor.execute(() -> {
            this.genres = this.tmdb.genre();
            this.configuration = this.tmdb.config();
        });
    }

    /*          MEMBER VARIABLE GETTERS          */

    public List<Movie> upcoming() {
        return this.upcoming;
    }

    public List<Movie> rated() {
        return this.rated;
    }

    public List<Movie> popular() {
        return this.popular;
    }

    public List<Movie> playing() {
        return this.playing;
    }

    public Map<Integer, String> genres() {
        return this.genres;
    }

    public Configuration config() {
        return this.configuration;
    }

    public List<Movie> watchlist() {
        return this.watchlist;
    }

    public List<Collection> collections() {
        return this.collections;
    }

    /*          TMDB INTERFACE          */

    public void movie(int id, TMDBCallback<Movie> callback) {
        executor.execute(() -> {
            Movie movie = this.tmdb.movie(id);
            handler.post(() -> callback.onResponse(movie));
        });
    }

    public void upcoming(int page, TMDBCallback<Page> callback) {
        executor.execute(() -> {
            Page result = this.tmdb.upcoming(page);
            handler.post(() -> callback.onResponse(result));
        });
    }

    public void rated(int page, TMDBCallback<Page> callback) {
        executor.execute(() -> {
            Page result = this.tmdb.rated(page);
            handler.post(() -> callback.onResponse(result));
        });
    }

    public void popular(int page, TMDBCallback<Page> callback) {
        executor.execute(() -> {
            Page result = this.tmdb.popular(page);
            handler.post(() -> callback.onResponse(result));
        });
    }

    public void playing(int page, TMDBCallback<Page> callback) {
        executor.execute(() -> {
            Page result = this.tmdb.playing(page);
            handler.post(() -> callback.onResponse(result));
        });
    }

    public void search(String param, int page, TMDBCallback<Page> callback) {
        executor.execute(() -> {
            Page result = this.tmdb.search(param, page);
            handler.post(() -> callback.onResponse(result));
        });
    }

    public void genres(TMDBCallback<Map<Integer, String>> callback) {
        executor.execute(() -> {
            Map<Integer, String> genres = this.tmdb.genre();
            handler.post(() -> callback.onResponse(genres));
        });
    }

    public void config(TMDBCallback<Configuration> callback) {
        executor.execute(() -> {
            Configuration configuration = this.tmdb.config();
            handler.post(() -> callback.onResponse(configuration));
        });
    }

    /*          SQLITE INTERFACE          */

    public void insert(Movie movie) {
        executor.execute(() -> {
            this.sqlite.insertMovie(movie);
            this.watchlist.add(movie);
        });
    }

    public void insert(Pair<Integer, Information> information) {
        executor.execute(() -> this.sqlite.insertInformation(information));
    }

    public void movies(SQLiteCallback<List<Movie>> callback) {
        executor.execute(() -> {
            List<Movie> movies = this.sqlite.selectMovies();
            handler.post(() -> callback.onResponse(movies));
        });
    }

    public Movie movie(int id) {
        /*
            Main thread query operations...
            Luckily this method is only used for testing, but will fix.
            2023-03-08
        */
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

    public void updateSeen(Movie movie) {
        executor.execute(() -> this.sqlite.updateSeen(movie));
    }

    public void updateRating(Movie movie) {
        executor.execute(() -> this.sqlite.updateRating(movie));
    }

    public Collection collection(String name) {
        return this.sqlite.selectCollection(name);
    }

    public void insertCollection(Collection collection) {
        executor.execute(() -> this.sqlite.insertCollection(collection));
    }

    public void updateCollection(Collection collection) {
        executor.execute(() -> this.sqlite.updateCollection(collection));
    }

    public void addToCollection(int id, String name) {
        executor.execute(() -> this.sqlite.addToCollection(id, name));
    }

    public void removeFromCollection(int id, String name) {
        executor.execute(() -> this.sqlite.removeFromCollection(id, name));
    }

    public void query(String query, SQLiteCallback<List<Movie>> callback) {
        executor.execute(() -> {
            List<Movie> movies = this.sqlite.query(query);
            handler.post(() -> callback.onResponse(movies));
        });
    }

    /*          MORE INTERFACE          */

    public void querySearch(String query, int page, CrossAccessorCallback<List<Movie>, Page> callback) {
        executor.execute(() -> {
            List<Movie> database = this.sqlite.query(query);
            Page result = this.tmdb.search(query, page);
            handler.post(() -> callback.onResponses(database, result));
        });
    }
}
