package aka_ecliptic.com.cinephile.Model;

import java.util.Date;

public class Movie extends Media {

    public Movie(){
        super();
    }

    public Movie(boolean seen, Date releaseDate, String title, int rating, Genre genre){
        super(seen, releaseDate, title, rating, genre);
    }

    public Movie(int id, boolean seen, Date releaseDate, String title, int rating, Genre genre){
        super(id, seen, releaseDate, title, rating, genre);
    }

    //TODO add movieDescriptor
}
