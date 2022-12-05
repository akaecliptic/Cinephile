package akaecliptic.dev.cinephile.Auxiliary;

import static akaecliptic.dev.cinephile.Helper.MediaObjectHelper.stringToDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import akaecliptic.dev.cinephile.Model.Genre;
import akaecliptic.dev.cinephile.Model.ImageData;
import akaecliptic.dev.cinephile.Model.Movie;
import akaecliptic.dev.cinephile.Model.Statistic;

public abstract class Factory {

    public static List<Movie> movies(List<String> lines, boolean header) {
        List<Movie> out = new ArrayList<>();
        int start = (header) ? 1 : 0;

        for (int i = start; i < lines.size(); i++) {
            String[] line = lines.get(i).split("\\|");

            int id = Integer.parseInt(line[0]);
            boolean seen = Integer.parseInt(line[1]) == 1;
            Date release = stringToDate(line[2]);
            String title = line[3];
            int rating = Integer.parseInt(line[4]);
            Genre genre = Genre.valueOf(line[5]);
            Genre sub = Genre.valueOf(line[6]);
            Genre min = Genre.valueOf(line[7]);

            String back = line[8];
            String poster = line[9];
            String description = line[10];

            Movie movie = new Movie(id, seen, release, title, rating, genre, sub, min);
            ImageData image = new ImageData(poster, back);
            Statistic statistic = new Statistic(description, 0);

            movie.setImageData(image);
            movie.setStatistic(statistic);

            out.add(movie);
        }

        return out;
    }
}
