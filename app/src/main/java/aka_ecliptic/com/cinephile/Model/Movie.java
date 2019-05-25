package aka_ecliptic.com.cinephile.Model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Movie extends Media {

    private Genre subGenre;
    private Genre minGenre;

    public Movie(){
        super();
    }

    public Movie(boolean seen, Date releaseDate, String title, int rating, Genre genre){
        super(seen, releaseDate, title, rating, genre);
    }

    public Movie(int id, boolean seen, Date releaseDate, String title, int rating, Genre genre){
        super(id, seen, releaseDate, title, rating, genre);
    }

    //In place for future database changes.
    public Movie(int id, boolean seen, Date releaseDate, String title, int rating, Genre genre, Genre subGenre, Genre minGenre){
        super(id, seen, releaseDate, title, rating, genre);
    }

    //TODO add movieDescriptor
}
