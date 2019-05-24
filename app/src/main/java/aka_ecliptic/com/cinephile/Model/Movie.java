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

    public Movie(boolean seen, int releaseDate, String title, int rating, Genre genre){
        super(seen, processDate(releaseDate), title, rating, genre);
    }

    //TODO remove this, only here to bypass parsing errors while refactoring
    private static Date processDate(int releaseDate) {
        Calendar cal = new GregorianCalendar();
        cal.set(releaseDate, 0, 1);
        return cal.getTime();
    }

    public Movie(int id, boolean seen, int releaseDate, String title, int rating, Genre genre){
        super(id, seen, processDate(releaseDate), title, rating, genre);
    }

    //In place for future database changes.
    public Movie(int id, boolean seen, int releaseDate, String title, int rating, Genre genre, Genre subGenre, Genre minGenre){
        super(id, seen, processDate(releaseDate), title, rating, genre);
    }

    //TODO add movieDescriptor
}
