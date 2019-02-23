package aka_ecliptic.com.cinephile.Model;

import java.util.List;

public class Movie extends Media {

    public Movie(){
        super();
    }

    public Movie(boolean seen, int year, String title, int rating, Genre genre){
        super(seen, year, title, rating, genre);
    }

    public Movie(boolean seen, int year, String title, int rating){
        super(seen, year, title, rating);
    }

    public Movie(int id, boolean seen, int year, String title, int rating, Genre genre){
        super(id, seen, year, title, rating, genre);
    }

    public Movie(int id, List<String> list){
        super(id, list);
    }

    public Movie(List<String> list){
        super(list);
    }
}
