package aka_ecliptic.com.cinephile.Helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import aka_ecliptic.com.cinephile.Model.Genre;
import aka_ecliptic.com.cinephile.Model.ImageData;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.Model.Statistic;

public class MediaJSONHelper {

    public static Gson getGson(){
        GsonBuilder gsonB = new GsonBuilder();
        JsonDeserializer<Movie> jsonD = (json, typeOfT, context) -> {
            if (!json.isJsonNull()) {
                JsonObject jsonObject = json.getAsJsonObject();

                Genre[] genres = getGenres(jsonObject.getAsJsonArray("genre_ids"));

                Movie movie = new Movie(
                        jsonObject.get("id").getAsInt(),
                        false,
                        MediaObjectHelper.stringToDate((jsonObject.get("release_date") != null) ? jsonObject.get("release_date").getAsString() : ""),
                        jsonObject.get("title").getAsString(),
                        jsonObject.get("vote_average").getAsBigDecimal().round(new MathContext(1, RoundingMode.HALF_UP)).intValue(),
                        genres[0],
                        genres[1],
                        genres[2]
                );

                ImageData imageData = new ImageData();

                if(!jsonObject.get("backdrop_path").isJsonNull()){
                    imageData.setBackdropImagePath(jsonObject.get("backdrop_path").getAsString());
                }
                if(!jsonObject.get("poster_path").isJsonNull()){
                    imageData.setPosterImagePath(jsonObject.get("poster_path").getAsString());
                }

                Statistic statistic = new Movie.MovieStatistic(
                        jsonObject.get("overview").toString(),
                        jsonObject.get("vote_average").getAsBigDecimal().multiply(new BigDecimal(10)).intValue(),
                        0
                );

                movie.setImageData(imageData);
                movie.setStatistic(statistic);
                return movie;
            }
            return null;
        };

        gsonB.registerTypeAdapter(Movie.class, jsonD);
        return gsonB.create();
    }

    private static Genre[] getGenres(JsonArray jsonArray){
        Genre[] genres = new Genre[3];
        for (int i = 0; i < 3; i++) {
            if(i < jsonArray.size() && !jsonArray.isJsonNull()){
                if(jsonArray.get(i).isJsonObject()){
                    int id = jsonArray.get(i).getAsJsonObject().get("id").getAsInt();
                    genres[i] = Genre.getGenreById(id);
                }else {
                    genres[i] = Genre.getGenreById(jsonArray.get(i).getAsInt());
                }
            }else {
                genres[i] = Genre.NONE;
            }
        }
        return genres;
    }

    private static Date getReleaseDates(JsonArray jsonArray){
        for (JsonElement jsonElement : jsonArray) {
            JsonObject temp = jsonElement.getAsJsonObject();
            if(temp.get("iso_3166_1").getAsString().equals("GB")){
                String year = temp.get("release_dates").getAsJsonArray().get(0).getAsJsonObject()
                                    .get("release_date").getAsString();
                return getYear(year);
            }
        }
        return null;
    }

    private static Date getYear(String releaseDate) {
        if(releaseDate.length() > 9 ) {
            String date = releaseDate.substring(0, 10);
            return MediaObjectHelper.stringToDate(date);
        }else {
            Calendar cal = new GregorianCalendar();
            cal.set(1970, 0, 1);
            return cal.getTime();
        }
    }
}
