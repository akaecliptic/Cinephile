package akaecliptic.dev.cinephile.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import akaecliptic.dev.cinephile.data.accessor.SQLite;
import akaecliptic.dev.cinephile.data.accessor.TMDB;
import akaecliptic.dev.cinephile.interaction.callback.CrossAccessorCallback;
import akaecliptic.dev.cinephile.interaction.callback.SQLiteCallback;
import akaecliptic.dev.cinephile.interaction.callback.TMDBCallback;
import akaecliptic.dev.cinephile.interaction.callback.UpdatedQueryCallback;
import akaecliptic.dev.cinephile.model.Collection;
import dev.akaecliptic.models.Configuration;
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

    private MutableLiveData<List<Movie>> watchlist;
    private MutableLiveData<List<Collection>> collections;

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

        this.watchlist = new MutableLiveData<>();
        this.collections = new MutableLiveData<>();
    }

    private void init() {
        executor.execute(() -> {
            this.watchlist.postValue(this.sqlite.selectMovies());
            this.collections.postValue(this.sqlite.selectCollections());
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

    public LiveData<List<Movie>> watchlist() {
        return this.watchlist;
    }

    public LiveData<List<Collection>> collections() {
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
        runAndUpdateMovie(prev -> {
            prev.add(movie);
            this.sqlite.insertMovie(movie);
            return prev;
        });
    }

    public void movies(SQLiteCallback<List<Movie>> callback) {
        executor.execute(() -> {
            List<Movie> movies = this.sqlite.selectMovies();
            handler.post(() -> callback.onResponse(movies));
        });
    }

    public void movie(int id, SQLiteCallback<Movie> callback) {
        executor.execute(() -> {
            Movie movie = this.sqlite.selectMovie(id);
            callback.onResponse(movie);
        });
    }

    public void deleteMovie(int id) {
        runAndUpdateMovie(prev -> {
            this.sqlite.deleteMovie(id);
            prev.removeIf(m -> m.getId() == id);
            return prev;
        });
    }

    public void updateSeen(Movie movie) {
        runAndUpdateMovie(prev -> {
            this.sqlite.updateSeen(movie);
            for (Movie m : prev) {
                if (m.getId() != movie.getId()) continue;

                m.setSeen(movie.isSeen());
                break;
            }
            return prev;
        });
    }

    public void updateRating(Movie movie) {
        runAndUpdateMovie(prev -> {
            this.sqlite.updateRating(movie);
            for (Movie m : prev) {
                if (m.getId() != movie.getId()) continue;

                m.setUserRating(movie.getUserRating());
                m.setNativeRating(movie.getNativeRating());
                break;
            }
            return prev;
        });
    }

    public void collection(String name, SQLiteCallback<Collection> callback) {
        executor.execute(() -> {
            Collection collection = this.sqlite.selectCollection(name);
            callback.onResponse(collection);
        });
    }

    public void insertCollection(Collection collection) {
        runAndUpdateCollection(prev -> {
            prev.add(collection);
            this.sqlite.insertCollection(collection);
            for (Integer member : collection.getMembers()) {
                this.sqlite.addToCollection(member, collection.getName());
            }
            return prev;
        });
    }

    public void updateCollection(Collection collection) {
        runAndUpdateCollection(prev -> {
            this.sqlite.updateCollection(collection);
            return prev
                    .stream()
                    .map(c -> (c.getName().equals(collection.getName())) ? collection : c)
                    .collect(Collectors.toList());
        });
    }

    public void deleteCollection(String name) {
        runAndUpdateCollection(prev -> {
            this.sqlite.deleteCollection(name);
            prev.removeIf(c -> c.getName().equals(name));
            return prev;
        });
    }

    public void addToCollection(int id, String name) {
        runAndUpdateCollection(prev -> {
            this.sqlite.addToCollection(id, name);
            for (Collection collection : prev) {
                if (!collection.getName().equals(name)) continue;

                collection.getMembers().add(id);
                break;
            }
            return prev;
        });
    }

    public void removeFromCollection(int id, String name) {
        runAndUpdateCollection(prev -> {
            this.sqlite.removeFromCollection(id, name);
            for (Collection collection : prev) {
                if (!collection.getName().equals(name)) continue;

                collection.getMembers().remove(id);
                break;
            }
            return prev;
        });
    }

    /*          MORE INTERFACE          */

    public void query(String query, SQLiteCallback<List<Movie>> callback) {
        executor.execute(() -> {
            List<Movie> movies = this.sqlite.query(query);
            handler.post(() -> callback.onResponse(movies));
        });
    }

    public void querySearch(String query, int page, CrossAccessorCallback<List<Movie>, Page> callback) {
        executor.execute(() -> {
            List<Movie> database = this.sqlite.query(query);
            Page result = this.tmdb.search(query, page);
            handler.post(() -> callback.onResponses(database, result));
        });
    }

    /*          UTILITY          */

    private void runAndUpdateMovie(UpdatedQueryCallback<List<Movie>> callback) {
        executor.execute(() -> {
            List<Movie> prev = this.watchlist.getValue();
            prev = (prev == null) ? new ArrayList<>() : prev;
            this.watchlist.postValue(callback.query(prev));
        });
    }

    private void runAndUpdateCollection(UpdatedQueryCallback<List<Collection>> callback) {
        executor.execute(() -> {
            List<Collection> prev = this.collections.getValue();
            prev = (prev == null) ? new ArrayList<>() : prev;
            this.collections.postValue(callback.query(prev));
        });
    }
}
