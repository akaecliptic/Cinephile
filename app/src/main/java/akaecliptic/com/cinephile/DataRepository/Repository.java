package akaecliptic.com.cinephile.DataRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import akaecliptic.com.cinephile.Model.Media;

public class Repository <T extends Media> {

    private List<T> mediaList;
    private Sort sortType;

    public Repository(){
        this.mediaList = new ArrayList<T>();
        this.sortType = Sort.DEFAULT;
    }

    public Repository(List<T> list){
        this.mediaList = list;
        this.sortType = Sort.DEFAULT;
    }

    public void setList(List<T> list){
        this.mediaList = list;
        this.sortType = Sort.DEFAULT;
    }

    public List<T> getItems(){
        return this.mediaList;
    }

    public void sortById(){
        this.mediaList.sort(Comparator.comparing(T::getId));
        this.sortType = Sort.ID;
    }

    public void sortByAlphabetically(){
        this.mediaList.sort(Comparator.comparing(T::getTitle));
        this.sortType = Sort.ALPHABETICALLY;
    }

    public void sortByRating(){
        this.mediaList.sort(Comparator.comparing(T::getRating).reversed());
        this.sortType = Sort.RATING;
    }

    public void sortByYear(){
        this.mediaList.sort(Comparator.comparing(T::getYear));
        this.sortType = Sort.YEAR;
    }

    public void sortBySortType(Sort sortOrder){
        if (sortOrder == Sort.ID){
            sortById();
        } else if(sortOrder == Sort.ALPHABETICALLY){
            sortByAlphabetically();
        } else if(sortOrder == Sort.RATING){
            sortByRating();
        } else if(sortOrder == Sort.YEAR){
            sortByYear();
        }
    }

    public String getSortTypeString(){
        return this.sortType.toString();
    }

    public Sort getSortType(){
        return this.sortType;
    }

    //TODO Change this Default enum value
    public enum Sort{
        ALPHABETICALLY(2), ID(0),
        RATING(1), YEAR(3), DEFAULT(0);

        private final Integer index;

        Sort(Integer index) {
            this.index = index;
        }

        public Integer getSortIndex(){
            return index;
        }

        public static Optional<Sort> valueOf(Integer value) {
            return Arrays.stream(values())
                    .filter(Sort -> Sort.index == value)
                    .findFirst();
        }
    }
}
