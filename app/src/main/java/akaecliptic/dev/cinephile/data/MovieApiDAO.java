package akaecliptic.dev.cinephile.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import akaecliptic.dev.cinephile.BuildConfig;

/**
 * Old Version of TMDB API caller. Deprecating for newer version.
 */
@Deprecated
public class MovieApiDAO {

    private static final String TAG = "MovieApiDAO";

    private static MovieApiDAO tmdbInstance;
    private RequestQueue tmdbRequestQueue;

    private static final String API_KEY = "?api_key=" + BuildConfig.ApiKey;
    private static final String LANGUAGE = "&language=en-GB";
    private static final String REGION = "&region=GB";
    private static final String QUERY = "&query=";
    private static final String PAGE = "&page=";

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";

    static synchronized MovieApiDAO getInstance(Context context) {
        if (tmdbInstance == null) {
            tmdbInstance = new MovieApiDAO(context);
        }
        return tmdbInstance;
    }

    private MovieApiDAO(Context context){
        tmdbRequestQueue = getRequestQueue(context);
    }

    private RequestQueue getRequestQueue(Context context) {
        if (tmdbRequestQueue == null) {
            tmdbRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return tmdbRequestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req) {
        tmdbRequestQueue.add(req);
    }

    void getMovies(int page, MovieType movieType, final VolleyCallback callback){

        String url = BASE_URL + "movie/" + movieType.type + API_KEY + LANGUAGE + PAGE + page + REGION;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , callback::onSuccess
                , error -> Log.d(TAG,"Error "+ error + "found making an API request at " + TAG));

        tmdbInstance.addToRequestQueue(jsonObjectRequest);
    }

    void queryMovie(String query, int page, final VolleyCallback callback) {
        String url = BASE_URL + "search/movie" + API_KEY + LANGUAGE + QUERY + query + PAGE + page + REGION;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , callback::onSuccess
                , error -> Log.d(TAG,"Error "+ error + "found making an API request at " + TAG));

        tmdbInstance.addToRequestQueue(jsonObjectRequest);
    }

    String getImageConfig(ImageType imageType){
        return BASE_IMAGE_URL + imageType.type;
    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

    public enum ImageType {
        POSTER("w185"), BACKDROP("w1280"), PROFILE("w92");

        private final String type;

        ImageType(String type) {
            this.type = type;
        }
    }

    public enum MovieType {
        TRENDING("popular"), UPCOMING("upcoming"),
        RECENT("now_playing"), FAVOURITES("top_rated");

        private final String type;

        MovieType(String type){
            this.type = type;
        }
    }
}
