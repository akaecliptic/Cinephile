package aka_ecliptic.com.cinephile.Model;

import android.support.annotation.Nullable;

import java.io.Serializable;

public class ImageData implements Serializable {
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
