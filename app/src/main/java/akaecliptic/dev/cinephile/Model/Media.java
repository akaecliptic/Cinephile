package akaecliptic.dev.cinephile.Model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import akaecliptic.dev.cinephile.Helper.MediaObjectHelper;

@Deprecated
public abstract class Media implements Comparable<Media>, Serializable {
    @SerializedName("id") protected int id;
    @SerializedName("seen") private boolean seen;
    @SerializedName("release_date") private Date releaseDate;
    @SerializedName("title") private String title;
    @SerializedName("rating") private int rating;
    @SerializedName("genre") private Genre genre;
    @SerializedName("image_data") private ImageData imageData;
    @SerializedName("statistics") protected Statistic statistic;

    Media() {
        this.id = -1;
        this.seen = false;
        this.releaseDate = new Date();
        this.title = "null";
        this.rating = 0;
        this.genre = Genre.NONE;
    }

    Media(int id, boolean seen, Date releaseDate, String title, int rating, Genre genre) {
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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
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

    public ImageData getImageData() {
        return this.imageData;
    }

    public void setImageData(ImageData imageData) {
        this.imageData = imageData;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public void setStatistic(Statistic statistic) {
        this.statistic = statistic;
    }

    @Override
    public int compareTo(@NonNull Media mediaObj) {
        return Integer.compare(this.id, mediaObj.id);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Media) {
            Media m = (Media) o;
            return this.id == m.getId() && MediaObjectHelper.releaseDateEquals(this.getReleaseDate(), m.getReleaseDate()) &&
                    this.title.equals(m.getTitle());
        }
        return false;
    }

    boolean equals2(Object o) {
        if (o instanceof Media) {
            Media m = (Media) o;
            return this.id == m.getId() && this.seen == m.isSeen() && MediaObjectHelper.releaseDateEquals(this.getReleaseDate(), m.getReleaseDate()) &&
                    this.title.equals(m.getTitle()) && this.rating == m.getRating() &&
                    this.genre.equals(m.getGenre());
        }
        return false;
    }
}
