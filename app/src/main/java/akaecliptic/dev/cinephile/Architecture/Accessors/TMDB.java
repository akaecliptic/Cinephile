package akaecliptic.dev.cinephile.Architecture.Accessors;

import android.util.Log;

import com.google.gson.JsonElement;

import java.util.Map;

import akaecliptic.dev.cinephile.BuildConfig;
import dev.akaecliptic.TMDBCaller;
import dev.akaecliptic.core.MovieFactory;
import dev.akaecliptic.core.RequestException;
import dev.akaecliptic.core.TMDBContext;
import dev.akaecliptic.models.Movie;

/**
 * This class is responsible for accessing TMDB. Querying all relevant endpoints. These include
 * <ul>
 *     <li>GET Movie Details</li>
 *     <li>GET Movies Upcoming / Top Rated / Now Playing / Popular</li>
 *     <li>GET Search Movies</li>
 *     <li>GET Genres Movie List</li>
 *     <li>GET Configuration API</li>
 * </ul>
 *
 * <p>A newer implementation of {@link akaecliptic.dev.cinephile.Architecture.MovieApiDAO} (now deprecated).</p>
 *
 * <p>
 *     All queries are synchronous and single threaded. For now the decision is to leave the responsibility
 *     of off-loading work off the UI thread to the repository class. This may change in a later version,
 *     but as of now (09/2022), this is the favoured approach.
 * </p>
 */
public class TMDB {

    private final String TAG = getClass().getSimpleName();

    private static final String API_KEY = BuildConfig.ApiKey;

    private final TMDBCaller caller;

    public TMDB() {
        this.caller = new TMDBCaller(new TMDBContext(API_KEY));
    }

    /**
     * Query GET <i>/movie/{movie_id}</i>
     *
     * @param id The id of the movies details.
     * @return {@link Movie} representation of the query.
     */
    public Movie movie(int id) {
        JsonElement element = null;

        try {
            element = caller.movie(id);
        } catch (RequestException e) {
            Log.w(TAG, "Could not request movie with id = " + id + ".");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            e.printStackTrace();
        }

        if(element == null || element.isJsonNull()) return null;

        return MovieFactory.create(element);
    }

    /**
     * Query GET <i>/movie/upcoming</i>
     *
     * @param page The page of results to get.
     * @return Array of {@link Movie} representing the query.
     */
    public Movie[] upcoming(int page){
        JsonElement element = null;

        try {
            element = caller.upcoming(page);
        } catch (RequestException e) {
            Log.w(TAG, "Could not request upcoming page " + page + ".");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            e.printStackTrace();
        }

        if(element == null || element.isJsonNull()) return null;

        JsonElement results = element.getAsJsonObject().get("results");

        return MovieFactory.createList(results);
    }

    /**
     * Query GET <i>/movie/top_rated</i>
     *
     * @param page The page of results to get.
     * @return Array of {@link Movie} representing the query.
     */
    public Movie[] rated(int page){
        JsonElement element = null;

        try {
            element = caller.rated(page);
        } catch (RequestException e) {
            Log.w(TAG, "Could not request top rated page " + page + ".");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            e.printStackTrace();
        }

        if(element == null || element.isJsonNull()) return null;

        JsonElement results = element.getAsJsonObject().get("results");

        return MovieFactory.createList(results);
    }

    /**
     * Query GET <i>/movie/popular</i>
     *
     * @param page The page of results to get.
     * @return Array of {@link Movie} representing the query.
     */
    public Movie[] popular(int page){
        JsonElement element = null;

        try {
            element = caller.popular(page);
        } catch (RequestException e) {
            Log.w(TAG, "Could not request popular page " + page + ".");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            e.printStackTrace();
        }

        if(element == null || element.isJsonNull()) return null;

        JsonElement results = element.getAsJsonObject().get("results");

        return MovieFactory.createList(results);
    }

    /**
     * Query GET <i>/movie/now_playing</i>
     *
     * @param page The page of results to get.
     * @return Array of {@link Movie} representing the query.
     */
    public Movie[] playing(int page){
        JsonElement element = null;

        try {
            element = caller.playing(page);
        } catch (RequestException e) {
            Log.w(TAG, "Could not request now playing page " + page + ".");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            e.printStackTrace();
        }

        if(element == null || element.isJsonNull()) return null;

        JsonElement results = element.getAsJsonObject().get("results");

        return MovieFactory.createList(results);
    }

    /**
     * Query GET <i>/search/movie</i>
     *
     * @param param The search parameter.
     * @param page The page of results to get.
     * @return Array of {@link Movie} representing the query.
     */
    public Movie[] search(String param, int page) {
        JsonElement element = null;

        try {
            element = caller.search(param, page);
        } catch (RequestException e) {
            Log.w(TAG, "Could not request search for '" + param + "' and page " + page + ".");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            e.printStackTrace();
        }

        if(element == null || element.isJsonNull()) return null;

        JsonElement results = element.getAsJsonObject().get("results");

        return MovieFactory.createList(results);
    }

    /**
     * Query GET <i>/genre/movie/list</i>
     *
     * @return Map of Integer genre ids to String names.
     */
    public Map<Integer, String> genre() {
        JsonElement element = null;

        try {
            element = caller.genre();
        } catch (RequestException e) {
            Log.w(TAG, "Could not request movie genre list.");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            e.printStackTrace();
        }

        if(element == null || element.isJsonNull()) return null;

        return MovieFactory.createGenreMap(element);
    }
}
