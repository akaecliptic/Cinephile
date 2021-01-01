package aka_ecliptic.com.cinephile.Architecture;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
    private List<String> collectionHeadings;
    private Sort sortType = Sort.DEFAULT;

    Repository(Context context){
        sqLiteDAO = SQLiteDAO.getInstance(context);
        movieDAO = MovieApiDAO.getInstance(context);
        if(onlineList[0][0] == null)
            cacheExploreList();
        this.mediaList = sqLiteDAO.getAllMovies();
        this.collectionHeadings = sqLiteDAO.getCollectionHeadings();
    }

    public void setItems(List<Movie> list, Sort sort){
        this.mediaList = list;
        this.sortType = (sort == null) ? Sort.DEFAULT : sort;
    }

    public void setItems() {
        this.mediaList = sqLiteDAO.getAllMovies();
        this.sortType = Sort.DEFAULT;
    }

    List<Movie> reCacheItems() {
        this.mediaList = sqLiteDAO.getAllMovies();
        return this.mediaList;
    }

    boolean isMoviePresent(int id){
        return this.sqLiteDAO.isMoviePresent(id);
    }

    boolean isFavourited(int id){
        return this.sqLiteDAO.isFavourited(id);
    }

    Movie getItem(int id){
        return this.sqLiteDAO.getMovie(id);
    }

    List<Movie> getItems(){
        return this.mediaList;
    }

    List<Movie> getItemsLike(String query) {
        return sqLiteDAO.getMoviesLike(query);
    }

    List<Integer> getItemsLike(List<String> titles) {
        return sqLiteDAO.getMoviesLike(titles);
    }

    List<String> getCollectionHeadings() {
        return this.collectionHeadings;
    }

    void addCollection(String name) { this.sqLiteDAO.addCollection(name, 1); }

    List<String> getCollectionNames() {
        return sqLiteDAO.getCollectionNames();
    }

    List<String> getCollectionsIn(Movie movie) {
        return sqLiteDAO.getCollectionsIn(movie);
    }

    List<Movie> getItemsInCollection(String name) {
        return this.sqLiteDAO.getMoviesFromCollection(name);
    }

    Movie[][] getOnlineList(){
        return this.onlineList;
    }

    void requestMoviesType(MovieApiDAO.MovieType movieType, int page, ExploreFragment.RequestResult requestResult) {
        movieDAO.getMovies(page, movieType, (result) -> {
            Movie[] toReturn = {};
            try {
                JSONArray jsonArray = result.getJSONArray("results");
                toReturn = new Movie[jsonArray.length()];
                Gson gson = MediaJSONHelper.getGson();
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jso = (JSONObject) jsonArray.get(i);
                    Movie m = gson.fromJson(jso.toString(), Movie.class);

                    toReturn[i] = (m);
                }
            }catch (Exception e){
                Log.d(TAG,"Error "+ e + "found making an API request at " + TAG);
            }
            requestResult.onResolved(toReturn);
        });
    }

    void requestOnlineListLike(String query, int page, ExploreFragment.RequestResult requestResult) {
        movieDAO.queryMovie(query, page, (result) -> {
            Movie[] toReturn = {};
            try {
                JSONArray jsonArray = result.getJSONArray("results");
                toReturn = new Movie[jsonArray.length()];
                Gson gson = MediaJSONHelper.getGson();
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jso = (JSONObject) jsonArray.get(i);
                    Movie m = gson.fromJson(jso.toString(), Movie.class);

                    toReturn[i] = (m);
                }
            }catch (Exception e){
                Log.d(TAG,"Error "+ e + "found making an API request at " + TAG);
            }
            requestResult.onResolved(toReturn);
        });
    }

    private void requestExploreItems(int page, MovieApiDAO.MovieType movieType, ExploreFragment.RequestResult requestResult) {
        movieDAO.getMovies(page, movieType, (result) -> {
            Movie[] toReturn = new Movie[9];
            try {
                JSONArray jsonArray = result.getJSONArray("results");
                Gson gson = MediaJSONHelper.getGson();
                for(int i = 0; i < 9; i++){
                    JSONObject jso = (JSONObject) jsonArray.get(i);
                    Movie m = gson.fromJson(jso.toString(), Movie.class);

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
                    requestExploreItems(1, MovieApiDAO.MovieType.FAVOURITES, (list3) ->
                        onlineList[3] = list3
                    );
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

    void toggleFavourites(int id, boolean favourite) {
        sqLiteDAO.toggleFavourite(id, favourite);
    }

    void toggleCollection(String name, int movieId, boolean set) {
        sqLiteDAO.toggleCollection(name, movieId, set);
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

    void deleteCollection(String collection) {
        sqLiteDAO.deleteCollection(collection);
    }

    void cycleSort(){
        this.sortType = Sort.valueOf(this.sortType.getSortIndex() + 1);
        sortBySortType(this.sortType, null);
    }

    Sort cycleSort(Sort sort){
        return Sort.valueOf(sort.getSortIndex() + 1);
    }

    List<Movie> sortList(List<Movie> toSort, @Nullable Sort sortBy){
        sortBy = (sortBy == null) ? this.sortType : sortBy;
        sortBySortType(sortBy, toSort);
        toSort.removeIf(Objects::isNull);
        return toSort;
    }

    String getSortString(){
        return this.sortType.getSortType();
    }

    Sort getSortType(){
        return this.sortType;
    }

    private void sortBySortType(Sort sortOrder, @Nullable List<Movie> toSort){
        switch (sortOrder){
            case DEFAULT: sortDefault(toSort);
                break;
            case ALPHABETICALLY: sortAlphabetically(toSort);
                break;
            case RATING: sortByRating(toSort);
                break;
            case YEAR: sortByYear(toSort);
                break;
            case SEEN: sortBySeen(toSort);
                break;
        }
    }

    private void sortDefault(@Nullable List<Movie> toSort){
        if(toSort == null){
            this.mediaList.sort(Comparator.comparing(Movie::getId));
            this.sortType = Sort.DEFAULT;
        }else {
            toSort.sort(Comparator.comparing(Movie::getId));
        }
    }

    private void sortAlphabetically(@Nullable List<Movie> toSort){
        if(toSort == null) {
            this.mediaList.sort(Comparator.comparing(Movie::getTitle));
            this.sortType = Sort.ALPHABETICALLY;
        }else {
            toSort.sort(Comparator.comparing(Movie::getTitle));
        }
    }

    private void sortByRating(@Nullable List<Movie> toSort){
        if(toSort == null) {
            this.mediaList.sort(Comparator.comparing(Movie::getRating).reversed());
            this.sortType = Sort.RATING;
        } else {
            toSort.sort(Comparator.comparing(Movie::getRating).reversed());
        }
    }

    private void sortByYear(@Nullable List<Movie> toSort){
        if(toSort == null) {
            this.mediaList.sort(Comparator.comparing(Movie::getReleaseDate).reversed());
            this.sortType = Sort.YEAR;
        }else {
            toSort.sort(Comparator.comparing(Movie::getReleaseDate).reversed());
        }
    }

    private void sortBySeen(@Nullable List<Movie> toSort){
        if(toSort == null) {
            this.mediaList.sort(Comparator.comparing(Movie::isSeen).reversed());
            this.sortType = Sort.SEEN;
        }else {
            toSort.sort(Comparator.comparing(Movie::isSeen).reversed());
        }
    }

    public enum Sort{
        DEFAULT(0),
        RATING(1),
        ALPHABETICALLY(2),
        YEAR(3),
        SEEN(4);

        private final int index;

        Sort(int index) {
            this.index = index;
        }

        public Integer getSortIndex(){
            return index;
        }

        public static Sort valueOf(int value) {
            for (Sort sort : Sort.values()){
                if(sort.getSortIndex() == value)
                    return sort;
            }
            return Sort.DEFAULT;
        }

        public String getSortType(){
            switch (this){
                case RATING:
                    return "Sort by Rating";
                case ALPHABETICALLY:
                    return "Alphabetically Sorted";
                case YEAR:
                    return "Sorted by Year";
                case SEEN:
                    return "Sorted by Watched";
                default:
                    return "Default Sort";
            }
        }
    }
}
