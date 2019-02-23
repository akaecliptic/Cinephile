package aka_ecliptic.com.cinephile.Model;

public class Movie extends Media {

    public Movie(){
        super();
    }

    public Movie(boolean seen, int year, String title, int rating, Genre genre){
        super(seen, year, title, rating, genre);
    }

    public Movie(int id, boolean seen, int year, String title, int rating, Genre genre){
        super(id, seen, year, title, rating, genre);
    }
}
