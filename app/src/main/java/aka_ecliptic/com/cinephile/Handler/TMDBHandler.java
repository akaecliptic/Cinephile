package aka_ecliptic.com.cinephile.Handler;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import aka_ecliptic.com.cinephile.BuildConfig;

public class TMDBHandler {

    public static final String TAG = "TMDBHandlerActivity";

    private static TMDBHandler tmdbInstance;
    private RequestQueue tmdbRequestQueue;
    private static Context tmdbContext;

    private static final String API_KEY = BuildConfig.ApiKey;
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String QUERY = "&query=";
    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";

    private TMDBHandler(Context context){
        tmdbContext = context;
        tmdbRequestQueue = getRequestQueue();
    }

    public static synchronized TMDBHandler getInstance(Context context) {
        if (tmdbInstance == null) {
            tmdbInstance = new TMDBHandler(context);
        }
        return tmdbInstance;
    }

    private RequestQueue getRequestQueue() {
        if (tmdbRequestQueue == null) {
            tmdbRequestQueue = Volley.newRequestQueue(tmdbContext.getApplicationContext());
        }
        return tmdbRequestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void search(String query ,int page, final VolleyCallback callback){

        String url = BASE_URL + "search/movie?api_key=" + API_KEY + "&query=" + query + "&page=" + page;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , callback::onSuccess
                , error -> {
            Log.d(TAG,"Error "+ error + "found making an API request");
            Toast.makeText(tmdbContext, "There was an error making request",
                    Toast.LENGTH_SHORT).show();
        });

        TMDBHandler.getInstance(tmdbContext).addToRequestQueue(jsonObjectRequest);

    }

    public void getTrending(int page, TrendingType trendingType, final VolleyCallback callback){

        String url = BASE_URL + "movie/" + trendingType.type + "?api_key=" + API_KEY + "&language=en-GB&page=" + page + "&region=GB";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , callback::onSuccess
                , error -> {
                    Log.d(TAG,"Error "+ error + "found making an API request");
                    Toast.makeText(tmdbContext, "There was an error making request",
                                    Toast.LENGTH_SHORT).show();
                });

        TMDBHandler.getInstance(tmdbContext).addToRequestQueue(jsonObjectRequest);
    }

    public void getMovie(int movieId, final VolleyCallback callback){

        String url = BASE_URL + "movie/" + movieId + "?api_key=" + API_KEY + "&language=en-GB&append_to_response=release_dates";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , callback::onSuccess
                , error -> {
            Log.d(TAG,"Error "+ error + "found making an API request");
            Toast.makeText(tmdbContext, "There was an error making request",
                    Toast.LENGTH_SHORT).show();
        });

        TMDBHandler.getInstance(tmdbContext).addToRequestQueue(jsonObjectRequest);
    }

    public String getImageConfig(String size){
        return BASE_IMAGE_URL + size;
    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

    public enum TrendingType{
        POPULAR("popular"), UPCOMING("now_playing"),
        NOW_PLAYING("upcoming"), TOP_RATED("top_rated");

        private final String type;

        TrendingType(String type){
            this.type = type;
        }
    }
}
