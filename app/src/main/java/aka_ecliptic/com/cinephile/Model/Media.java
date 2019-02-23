package aka_ecliptic.com.cinephile.Model;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        this.year = 0000;
        this.title = "null";
        this.rating = 0;
        this.genre = Genre.NULL;
    }

    public Media(boolean seen, int year, String title, int rating, Genre genre){
        this.id = 0;
        this.seen = seen;
        this.year = year;
        this.title = title;
        this.rating = rating;
        this.genre = genre;
    }

    public Media(boolean seen, int year, String title, int rating){
        this.id = 0;
        this.seen = seen;
        this.year = year;
        this.title = title;
        this.rating = rating;
        this.genre = Genre.NULL;
    }

    public Media(int id, boolean seen, int year, String title, int rating, Genre genre){
        this.id = id;
        this.seen = seen;
        this.year = year;
        this.title = title;
        this.rating = rating;
        this.genre = genre;
    }

    public Media(List<String> list){
        this.id = 0;
        this.seen = convertSeen(list.get(0));
        this.year = Integer.parseInt(list.get(1));
        this.title = list.get(2);
        this.rating = Integer.parseInt(list.get(3));
        this.genre = Genre.valueOf(list.get(4));
    }

    public Media(int id, List<String> list){
        this.id = id;
        this.seen = convertSeen(list.get(0));
        this.year = Integer.parseInt(list.get(1));
        this.title = list.get(2);
        this.rating = Integer.parseInt(list.get(3));
        this.genre = Genre.valueOf(list.get(4));
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

    public List<String> asList(){

        ArrayList<String> list = new ArrayList<>();

        list.add(convertSeen(this.seen));
        list.add(Integer.toString(this.year));
        list.add(this.title);
        list.add(Integer.toString(this.rating));
        list.add(this.genre.toString());

        return list;
    }

    public static String convertSeen(boolean s){
        if(s){
            return "1";
        }else {
            return "0";
        }
    }

    public static boolean convertSeen(String s){
        if(s.equals("1")){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public int hashCode(){
        return this.id * 75 + this.rating * 75 + this.year * 75 +
               this.title.hashCode() * 75 + this.genre.toString().hashCode() * 75 +
               Boolean.hashCode(this.seen) * 75;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Media){
            Media m = (Media) o;
            return this.id == m.getId() && this.title.equals(m.getTitle()) &&
                this.year == m.getYear() && this.rating == m.getRating() &&
                this.genre.toString().equals(m.getGenre().toString()) && this.seen == m.isSeen();

        } else{
            return false;
        }
    }

    @Override
    public int compareTo(Media mediaObj){
            return Integer.compare(this.id, mediaObj.id);
    }

    public enum Genre{
        ACTION, ADVENTURE, COMEDY, THRILLER,
        DRAMA, ROMANTIC, HORROR, SCI_FI,
        NULL
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
