package aka_ecliptic.com.cinephile.Architecture;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import aka_ecliptic.com.cinephile.Model.Movie;

public class MediaViewModel extends AndroidViewModel {

    private Repository mediaRepository;

    public MediaViewModel(@NonNull Application application) {
        super(application);
        mediaRepository = new Repository(application);
    }

    public List<Movie> getItems() { return mediaRepository.getItems(); }

    public Movie[][] requestMovies() {
        return mediaRepository.getOnlineList();
    }

    public String getImageConfig(MovieApiDAO.ImageType imageType) {
        return mediaRepository.getImageConfig(imageType);
    }

    public int updateItem(Movie movie) { return mediaRepository.updateItem(movie); }

    public int deleteItem(Movie movie) { return mediaRepository.removeItem(movie); }

    public void addItem(Movie movie) { mediaRepository.addItem(movie); }
}
