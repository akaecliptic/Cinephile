package akaecliptic.dev.cinephile.model;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

@Deprecated
public class ImageData implements Serializable {
    @SerializedName("poster_path") private String posterImagePath; //The image path for movie poster
    @SerializedName("backdrop_path") private String backdropImagePath; //The image path for movie backdrop

    /**
     *
     */
    public ImageData(){
        this.posterImagePath = "null";
        this.backdropImagePath = "null";
    }

    /**
     *
     * @param backdropImagePath
     * @param posterImagePath
     */
    public ImageData(String posterImagePath, String backdropImagePath){
        this.posterImagePath = posterImagePath;
        this.backdropImagePath = backdropImagePath;

    }

    /**
     *
     * @return
     */
    public String getPosterImagePath() {
        return posterImagePath;
    }

    /**
     *
     * @param posterImagePath
     */
    public void setPosterImagePath(String posterImagePath) {
        this.posterImagePath = posterImagePath;
    }

    /**
     *
     * @return
     */
    public String getBackdropImagePath() {
        return backdropImagePath;
    }

    /**
     *
     * @param backdropImagePath
     */
    public void setBackdropImagePath(String backdropImagePath) {
        this.backdropImagePath = backdropImagePath;
    }
}
