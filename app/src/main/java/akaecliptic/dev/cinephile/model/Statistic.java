package akaecliptic.dev.cinephile.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

//Rename class to appropriately reflect functionality.
@Deprecated
public class Statistic implements Serializable {
    @SerializedName("description") private String description; //The description of media Object, as provided by API
    @SerializedName("site_rating") private int siteRating; //The rating given on API website

    /**
     * Default constructor returns a Statistic with 'invalid' values, to be changed later
     */
    public Statistic(){
        this.description = "null";
        this.siteRating = -1;
    }

    /**
     * Main constructor returns a Statistic with specified values.
     */
    public Statistic(String description, int siteRating){
        this.description = description;
        this.siteRating = siteRating;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public int getSiteRating() {
        return siteRating;
    }

    /**
     *
     * @param siteRating
     */
    public void setSiteRating(int siteRating) {
        this.siteRating = siteRating;
    }
}