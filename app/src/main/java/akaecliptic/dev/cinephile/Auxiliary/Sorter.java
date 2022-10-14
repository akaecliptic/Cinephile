package akaecliptic.dev.cinephile.Auxiliary;

import java.util.Comparator;
import java.util.List;

import dev.akaecliptic.models.Media;
import dev.akaecliptic.models.Movie;

public class Sorter {

    private Sort sort;
    private String message;

    public Sorter() {
        this.sort = Sort.DEFAULT;
    }

    public Sorter(Sort sort) {
        this.sort = sort;
    }

    public void cycle(List<Movie> list) {
        this.sort = sort.cycle();
        sort(list);
    }

    public String getMessage() {
        return message;
    }

    public void sort(List<Movie> list) {
        switch (sort) {
            default:
            case DEFAULT:
                list.sort(Comparator.comparingInt(Media::getId));
                message = "Default sort";
                break;
            case TITLE:
                list.sort(Comparator.comparing(Media::getTitle));
                message = "Sorting by title";
                break;
            case U_RATING:
                list.sort(Comparator.comparingInt(Media::getUserRating).reversed());
                message = "Sorting by user rating";
                break;
            case N_RATING:
                list.sort(Comparator.comparingInt(Media::getNativeRating).reversed());
                message = "Sorting by tmdb rating";
                break;
            case RELEASE:
                list.sort(Comparator.comparing(Media::getRelease).reversed());
                message = "Sorting by release date";
                break;
        }
    }

    public void sort(List<Movie> list, Sort sort) {
        switch (sort) {
            default:
            case DEFAULT:
                list.sort(Comparator.comparingInt(Media::getId));
                break;
            case TITLE:
                list.sort(Comparator.comparing(Media::getTitle));
                break;
            case U_RATING:
                list.sort(Comparator.comparingInt(Media::getUserRating).reversed());
                break;
            case N_RATING:
                list.sort(Comparator.comparingInt(Media::getNativeRating).reversed());
                break;
            case RELEASE:
                list.sort(Comparator.comparing(Media::getRelease).reversed());
                break;
        }
    }

    public enum Sort {
        DEFAULT(0), TITLE(1),
        U_RATING(2), N_RATING(3),
        RELEASE(4);

        private final int type;

        Sort(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public static Sort valueOf(int value) {
            for (Sort sort : Sort.values()) {
                if (sort.getType() == value) return sort;
            }
            return DEFAULT;
        }

        public Sort cycle() {
            int next = this.type + 1;
            return valueOf(next);
        }
    }
}
