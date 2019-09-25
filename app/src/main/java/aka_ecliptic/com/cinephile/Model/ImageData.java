package aka_ecliptic.com.cinephile.Model;

import java.io.Serializable;

public class ImageData implements Serializable {
    private String posterImagePath; //The image path for movie poster
    private String backdropImagePath; //The image path for movie backdrop

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
    public ImageData(String backdropImagePath, String posterImagePath){
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
