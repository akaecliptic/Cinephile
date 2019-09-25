package aka_ecliptic.com.cinephile.DataRepository;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import aka_ecliptic.com.cinephile.Handler.SQLiteHandler;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;

public class Repository <T extends Media> {

    private List<T> mediaList;
    private Sort sortType;

    public Repository(){
        this.mediaList = new ArrayList<>();
        this.sortType = Sort.ID;
    }

    public Repository(Context context){
        this.mediaList = (List) SQLiteHandler.getInstance(context).getMovieList();
        this.sortType = Sort.ID;
    }

    public void setList(List<T> list, Sort sort){
        this.mediaList = list;
        if (sort == null){
            this.sortType = Sort.UNSORTED;
        }else{
            this.sortType = sort;
        }

    }

    public void setList(Context context) {
        this.mediaList = (List) SQLiteHandler.getInstance(context).getMovieList();
    }

    public List<T> getItems(){
        return this.mediaList;
    }

    public int replaceItem(Movie item, Context context) {
        SQLiteHandler.getInstance(context).updateMovie(item);

        int index = -1;

        for (T m : this.mediaList) {
            if(m.getId() == item.getId()){
                index = this.mediaList.indexOf(m);
                this.mediaList.set(index, (T) item);
            }
        }

        return index;
    }

    public void addItem(Movie movie, Context context) {
        SQLiteHandler.getInstance(context).newMovie(movie);
        this.mediaList.add((T) movie);

    }

    public static <T extends Movie>  void addToDB(Context context, T movie){
        SQLiteHandler.getInstance(context).newMovie(movie);
    }

    public Sort getSortType(){
        return this.sortType;
    }

    public void sortBySortType(Sort sortOrder){
        switch (sortOrder){
            case ID: sortById();
                break;
            case ALPHABETICALLY: sortAlphabetically();
                break;
            case RATING: sortByRating();
                break;
            case YEAR: sortByYear();
                break;
        }
    }

    private void sortById(){
        this.mediaList.sort(Comparator.comparing(T::getId));
        this.sortType = Sort.ID;
    }

    private void sortAlphabetically(){
        this.mediaList.sort(Comparator.comparing(T::getTitle));
        this.sortType = Sort.ALPHABETICALLY;
    }

    private void sortByRating(){
        this.mediaList.sort(Comparator.comparing(T::getRating).reversed());
        this.sortType = Sort.RATING;
    }

    private void sortByYear(){
        this.mediaList.sort(Comparator.comparing(T::getReleaseDate).reversed());
        this.sortType = Sort.YEAR;
    }

    public enum Sort{
        ALPHABETICALLY(2), ID(0),
        RATING(1), YEAR(3), UNSORTED(99);

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
