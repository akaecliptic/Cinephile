package aka_ecliptic.com.cinephile.Model;

import android.support.annotation.Nullable;

import java.io.Serializable;

public abstract class Media implements Comparable<Media>, Serializable {

    protected int id;
    protected boolean seen;
    protected int year;
    protected String title;
    protected int rating;
    protected Genre genre;
    protected ImageData imageData;
    protected Descriptor descriptor;

    public Media(){
        this.id = 0;
        this.seen = false;
        this.year = 1888;
        this.title = "null";
        this.rating = 0;
        this.genre = Genre.ACTION;
    }

    public Media(boolean seen, int year, String title, int rating, Genre genre){
        this.id = 0;
        this.seen = seen;
        this.year = year;
        this.title = title;
        this.rating = rating;
        this.genre = genre;
    }

    public Media(int id, boolean seen, int year, String title, int rating, Genre genre){
        this.id = id;
        this.seen = seen;
        this.year = year;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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
    public int compareTo(Media mediaObj){
            return Integer.compare(this.id, mediaObj.id);
    }


//    int id;
//    protected boolean seen;
//    protected int year;
//    protected String title;
//    protected int rating;
//    protected Genre genre;
    @Override
    public boolean equals(Object o){
        if(o instanceof Media)  {
            Media m = (Media) o;
            return this.id == m.getId() && this.seen == m.isSeen() && this.year == m.getYear() &&
                    this.title.equals(m.getTitle()) && this.rating == m.getRating() &&
                    this.genre.equals(m.getGenre());
        }
        return false;
    }

    public enum Genre{
        ACTION, ADVENTURE, COMEDY, THRILLER,
        DRAMA, ROMANTIC, HORROR, SCI_FI;
    }

    public static class ImageData implements Serializable{
        private String posterImagePath;
        private String backdropImagePath;

        public ImageData(@Nullable String backdropImagePath, @Nullable String posterImagePath){
            this.posterImagePath = posterImagePath;
            this.backdropImagePath = backdropImagePath;

        }

        public ImageData(){

        }

        public String getPosterImagePath() {
            return posterImagePath;
        }

        public void setPosterImagePath(String posterImagePath) {
            this.posterImagePath = posterImagePath;
        }

        public String getBackdropImagePath() {
            return backdropImagePath;
        }

        public void setBackdropImagePath(String backdropImagePath) {
            this.backdropImagePath = backdropImagePath;
        }
    }

    //TODO Add more relevant parameters
    public static class Descriptor implements Serializable{
        private String description;

        public Descriptor(String description){
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
