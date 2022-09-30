package akaecliptic.dev.cinephile.Architecture.Repository;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import akaecliptic.dev.cinephile.Architecture.Accessors.SQLite;
import akaecliptic.dev.cinephile.Architecture.Accessors.TMDB;
import akaecliptic.dev.cinephile.Architecture.MovieRepository;
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

//    private final SQLite sqlite;
    private final TMDB tmdb;

    private Movie[] upcoming;
    private Movie[] rated;
    private Movie[] popular;
    private Movie[] playing;

    private List<Movie> myList;

    public Repository(Context context) {
//        this.sqlite = SQLite.getInstance(context);
        this.tmdb = new TMDB();
        initialise();
    }

    private void initialise() {
//        executor.execute(() -> this.myList = this.sqlite.selectAll());
        executor.execute(() -> this.upcoming = this.tmdb.upcoming(1));
        executor.execute(() -> this.rated = this.tmdb.rated(1));
        executor.execute(() -> this.popular = this.tmdb.popular(1));
        executor.execute(() -> this.playing = this.tmdb.playing(1));
    }
}
