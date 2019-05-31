package aka_ecliptic.com.cinephile.Model;

import java.util.Date;

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

    public Movie(int id, boolean seen, Date releaseDate, String title, int rating, Genre genre, Genre subGenre, Genre minGenre){
        super(id, seen, releaseDate, title, rating, genre);
        this.subGenre = subGenre;
        this.minGenre = minGenre;
    }

    public Genre getSubGenre() {
        return subGenre;
    }

    public void setSubGenre(Genre subGenre) {
        this.subGenre = subGenre;
    }

    public Genre getMinGenre() {
        return minGenre;
    }

    public void setMinGenre(Genre minGenre) {
        this.minGenre = minGenre;
    }

    public boolean movieEquals(Movie m) {
        return super.equals2(m) && this.subGenre.equals(m.getSubGenre()) && this.minGenre.equals(m.minGenre);
    }
//TODO add movieDescriptor
}
