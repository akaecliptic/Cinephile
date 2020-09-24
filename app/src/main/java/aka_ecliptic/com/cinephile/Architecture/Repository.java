package aka_ecliptic.com.cinephile.Architecture;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import aka_ecliptic.com.cinephile.Fragment.ExploreFragment;
import aka_ecliptic.com.cinephile.Helper.MediaJSONHelper;
import aka_ecliptic.com.cinephile.Model.Movie;

// TODO: 22/09/2020 CONSIDER A MORE VIABLE APPROACH TO MAKING THIS GENERIC.
public class Repository{

    private static final String TAG = "Repository";
    private SQLiteDAO sqLiteDAO;
    private MovieApiDAO movieDAO;
    private List<Movie> mediaList;
    private Movie[][] onlineList = new Movie[4][9];
    private Sort sortType = Sort.DEFAULT;

    Repository(Context context){
        sqLiteDAO = SQLiteDAO.getInstance(context);
        movieDAO = MovieApiDAO.getInstance(context);
        if(onlineList[0][0] == null)
            cacheExploreList();
        this.mediaList = sqLiteDAO.getAllMovies();
    }

    public void setItems(List<Movie> list, Sort sort){
        this.mediaList = list;
        this.sortType = (sort == null) ? Sort.DEFAULT : sort;
    }

    public void setItems() {
        this.mediaList = sqLiteDAO.getAllMovies();
        this.sortType = Sort.DEFAULT;
    }

    List<Movie> getItems(){
        return this.mediaList;
    }

    Movie[][] getOnlineList(){
        return this.onlineList;
    }

    private void requestExploreItems(int page, MovieApiDAO.MovieType movieType, ExploreFragment.RequestResult requestResult) {
        movieDAO.getMovies(page, movieType, (result) -> {
            Movie[] toReturn = new Movie[9];
            try {
                JSONArray jsonArray = result.getJSONArray("results");
                for(int i = 0; i < 9; i++){
                    JSONObject jso = (JSONObject) jsonArray.get(i);
                    Gson gson = MediaJSONHelper.getGson();
                    Movie m = gson.fromJson(jso.toString(),Movie.class);

                    toReturn[i] = m;
                }
            }catch (Exception e){
                Log.d(TAG,"Error "+ e + "found making an API request at " + TAG);
            }
            requestResult.onResolved(toReturn);
        });
    }

    private void cacheExploreList() {
        requestExploreItems(1, MovieApiDAO.MovieType.TRENDING, (list0) -> {
            onlineList[0] = list0;
            requestExploreItems(1, MovieApiDAO.MovieType.RECENT, (list1) -> {
                onlineList[1] = list1;
                requestExploreItems(1, MovieApiDAO.MovieType.UPCOMING, (list2) -> {
                    onlineList[2] = list2;
                    requestExploreItems(1, MovieApiDAO.MovieType.FAVOURITES, (list3) -> {
                        onlineList[3] = list3;
                    });
                });
            });
        });
    }

    String getImageConfig(MovieApiDAO.ImageType imageType) {
        return movieDAO.getImageConfig(imageType);
    }

    int updateItem(Movie item) {
        sqLiteDAO.updateMovie(item);

        int index = -1;

        for (Movie m : this.mediaList) {
            if(m.getId() == item.getId()){
                index = this.mediaList.indexOf(m);
                this.mediaList.set(index, item);
                break;
            }
        }

        return index;
    }

    void addItem(Movie movie) {
        sqLiteDAO.addMovie(movie);
        this.mediaList.add(movie);

    }

    int removeItem(Movie item) {
        int index = -1;

        for (Movie m : this.mediaList) {
            if(m.getId() == item.getId()){
                index = this.mediaList.indexOf(m);
                mediaList.remove(index);
                sqLiteDAO.deleteMovie(item.getId());
                break;
            }
        }

        return index;
    }

    public Sort getSortType(){
        return this.sortType;
    }

    public void sortBySortType(Sort sortOrder){
        switch (sortOrder){
            case DEFAULT: sortDefault();
                break;
            case ALPHABETICALLY: sortAlphabetically();
                break;
            case RATING: sortByRating();
                break;
            case YEAR: sortByYear();
                break;
        }
    }

    private void sortDefault(){
        this.mediaList.sort(Comparator.comparing(Movie::getId));
        this.sortType = Sort.DEFAULT;
    }

    private void sortAlphabetically(){
        this.mediaList.sort(Comparator.comparing(Movie::getTitle));
        this.sortType = Sort.ALPHABETICALLY;
    }

    private void sortByRating(){
        this.mediaList.sort(Comparator.comparing(Movie::getRating).reversed());
        this.sortType = Sort.RATING;
    }

    private void sortByYear(){
        this.mediaList.sort(Comparator.comparing(Movie::getReleaseDate).reversed());
        this.sortType = Sort.YEAR;
    }

    public enum Sort{
        DEFAULT(0),
        RATING(1),
        ALPHABETICALLY(2),
        YEAR(3);

        private final Integer index;

        Sort(Integer index) {
            this.index = index;
        }

        public Integer getSortIndex(){
            return index;
        }

        public static Optional<Sort> valueOf(Integer value) {
            return Arrays.stream(values())
                    .filter(Sort -> Sort.index.equals(value))
                    .findFirst();
        }
    }
}
