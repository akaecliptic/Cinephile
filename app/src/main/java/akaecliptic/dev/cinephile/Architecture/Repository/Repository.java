package akaecliptic.dev.cinephile.Architecture.Repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import akaecliptic.dev.cinephile.Architecture.Accessors.SQLite;
import akaecliptic.dev.cinephile.Architecture.Accessors.TMDB;
import akaecliptic.dev.cinephile.Architecture.MovieRepository;
import akaecliptic.dev.cinephile.Interface.InitialisationCallback;
import akaecliptic.dev.cinephile.Interface.TMDBCallback;
import dev.akaecliptic.models.Configuration;
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
    private Configuration configuration;

    private List<Movie> watchlist;

    /*
        Hmm, I like the idea of hooking into the initialisation process through a pseudo broadcaster system.
        However, this seems unnecessary and convoluted. There are other approaches but, really not a fan of them.
        Will look into this some more, but will keep this for now, or at least until the possible issues occur.

        Kind of trolling, but I like it.
        2022-10-07

        With every commit I am closer to deleting this feature. Soon...
        2022-10-08
     */
    private Map<Integer, List<InitialisationCallback>> channels;

    public Repository(Context context) {
        this.sqlite = SQLite.getInstance(context);
        this.tmdb = new TMDB();
        setup();
        init();
    }

    private void setup() {
        this.watchlist = new ArrayList<>();
        this.channels = new HashMap<>();
        /*
            Should probably use enums here, but I want to keep this simple as this might get torn out.
            The other channels will most likely not see much use, just 0 if any.
         */
        this.channels.put(0, new LinkedList<>());
        this.channels.put(1, new LinkedList<>());
        this.channels.put(2, new LinkedList<>());
    }

    private void init() {
        executor.execute(() -> {
            this.watchlist.addAll(this.sqlite.selectAll());

            handler.post(() -> broadcast(0));
        });

        executor.execute(() -> {
            this.upcoming = this.tmdb.upcoming(1);
            this.rated = this.tmdb.rated(1);
            this.popular = this.tmdb.popular(1);
            this.playing = this.tmdb.playing(1);

            handler.post(() -> broadcast(1));
        });

        executor.execute(() -> {
            this.genres = this.tmdb.genre();
            this.configuration = this.tmdb.config();

            handler.post(() -> broadcast(2));
        });
    }

    /*          PUB / SUB          */

    public void subscribe(int channel, InitialisationCallback callback) {
        List<InitialisationCallback> subscribers = this.channels.get(channel);
        if (subscribers == null) return;

        subscribers.add(callback);
    }

    private void broadcast(int channel) {
        List<InitialisationCallback> subscribers = this.channels.get(channel);
        if (subscribers == null) return;

        subscribers.forEach(InitialisationCallback::onInit);
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

    public Configuration config() {
        return this.configuration;
    }

    public List<Movie> watchlist() {
        return this.watchlist;
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

    public void insert(Movie movie) {
        executor.execute(() -> {
            this.sqlite.insertMovie(movie);
            this.watchlist.add(movie);
        });
    }

    public void insert(Pair<Integer, Information> information) {
        executor.execute(() -> this.sqlite.insertInformation(information));
    }

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

    public void updateSeen(Movie movie) {
        executor.execute(() -> this.sqlite.updateSeen(movie));
    }
}
