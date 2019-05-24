package aka_ecliptic.com.cinephile.Model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public abstract class Media implements Comparable<Media>, Serializable {

    protected int id;
    protected boolean seen;
    protected Date releaseDate;
    protected String title;
    protected int rating;
    protected Genre genre;
    protected ImageData imageData;
    protected Descriptor descriptor;

    public Media(){
        this.id = 0;
        this.seen = false;
        this.releaseDate = new Date();
        this.title = "null";
        this.rating = 0;
        this.genre = Genre.NONE;
    }

    public Media(boolean seen, Date releaseDate, String title, int rating, Genre genre){
        this.id = 0;
        this.seen = seen;
        this.releaseDate = releaseDate;
        this.title = title;
        this.rating = rating;
        this.genre = genre;
    }

    public Media(int id, boolean seen, Date releaseDate, String title, int rating, Genre genre){
        this.id = id;
        this.seen = seen;
        this.releaseDate = releaseDate;
        this.title = title;
        this.rating = rating;
        this.genre = genre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public int getReleaseDate() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(releaseDate);
        return cal.get(Calendar.YEAR);
    }

    //TODO change this, only here to bypass parsing errors while refactoring
    public void setReleaseDate(int releaseDate) {
        Calendar cal = new GregorianCalendar();
        cal.set(releaseDate, 0, 1);
        this.releaseDate = cal.getTime();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public ImageData getImageData(){
        return this.imageData;
    }

    public void setImageData(@Nullable ImageData imageData){
        this.imageData = imageData;
    }

    public Descriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public int compareTo(@NonNull Media mediaObj){
            return Integer.compare(this.id, mediaObj.id);
    }

    //TODO change release date comparisons when changing release date getters and setters.
    @Override
    public boolean equals(Object o){
        if(o instanceof Media)  {
            Media m = (Media) o;
            return this.id == m.getId() && this.getReleaseDate() == m.getReleaseDate() &&
                    this.title.equals(m.getTitle());
        }
        return false;
    }

    public boolean equals2(Object o){
        if(o instanceof Media)  {
            Media m = (Media) o;
            return this.id == m.getId() && this.seen == m.isSeen() &&  this.getReleaseDate() == m.getReleaseDate() &&
                    this.title.equals(m.getTitle()) && this.rating == m.getRating() &&
                    this.genre.equals(m.getGenre());
        }
        return false;
    }
}
