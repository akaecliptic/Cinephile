package akaecliptic.dev.cinephile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Movie extends Media {
    @SerializedName("sub_genre") private Genre subGenre;
    @SerializedName("min_genre") private Genre minGenre;

    public Movie(){
        super();
        this.subGenre = Genre.NONE;
        this.minGenre = Genre.NONE;
    }

    public Movie(int id, boolean seen, Date releaseDate, String title, int rating, Genre genre){
        super(id, seen, releaseDate, title, rating, genre);
        this.subGenre = Genre.NONE;
        this.minGenre = Genre.NONE;
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

    @Override
    public MovieStatistic getStatistic() {
        //TODO: Reconsider.
        return (this.statistic instanceof MovieStatistic) ? (MovieStatistic) this.statistic : new MovieStatistic(this.statistic.getDescription(), this.statistic.getSiteRating(), -1);
    }

    public boolean movieEquals(Movie m) {
        return super.equals2(m) && this.subGenre.equals(m.getSubGenre()) && this.minGenre.equals(m.minGenre);
    }

    public static class MovieStatistic extends Statistic{
        @SerializedName("runtime") private int runtime; //The runtime of the movie in minutes, as provided by API

        public MovieStatistic(){
            super();
            this.runtime = -1;
        }

        public MovieStatistic(String description, int siteRating, int runtime){
            super(description, siteRating);
            this.runtime = runtime;
        }

        /**
         *
         * @return
         */
        public int getRuntime() {
            return runtime;
        }

        /**
         *
         * @param runtime
         */
        public void setRuntime(int runtime) {
            this.runtime = runtime;
        }
    }
}
