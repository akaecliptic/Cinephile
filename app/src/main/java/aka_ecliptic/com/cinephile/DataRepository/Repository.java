package aka_ecliptic.com.cinephile.DataRepository;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import aka_ecliptic.com.cinephile.Handler.SQLiteHandler;
import aka_ecliptic.com.cinephile.Model.Media;

public class Repository <T extends Media> {

    private List<T> mediaList;
    private Sort sortType;

    public Repository(){
        this.mediaList = new ArrayList<>();
        this.sortType = Sort.ID;
    }

    public Repository(Context context){
        this.mediaList = (List) SQLiteHandler.getInstance(context).getList();
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
        this.mediaList = (List) SQLiteHandler.getInstance(context).getList();
    }

    public List<T> getItems(){
        return this.mediaList;
    }

    public int replaceItem(T item, Context context) {
        SQLiteHandler.getInstance(context).updateEntry(item);

        int index = -1;

        for (T m : this.mediaList) {
            if(m.getId() == item.getId()){
                index = this.mediaList.indexOf(m);
                this.mediaList.set(index, item);
            }
        }

        return index;
    }

    public void addItem(T movie, Context context) {
        SQLiteHandler.getInstance(context).newEntry(movie);
        this.mediaList.add(movie);

    }

    public static <T extends Media>  void addToDB(Context context, T movie){
        SQLiteHandler.getInstance(context).newEntry(movie);
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
        this.mediaList.sort(Comparator.comparing(T::getYear));
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
