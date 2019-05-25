package aka_ecliptic.com.cinephile.Handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Model.Descriptor;
import aka_ecliptic.com.cinephile.Model.Genre;
import aka_ecliptic.com.cinephile.Model.ImageData;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;

public class GsonMovieConverter {

    public static Gson getCustomGson(){
        GsonBuilder gsonB = new GsonBuilder();
        JsonDeserializer<Movie> jsonD = (json, typeOfT, context) -> {
        if (!json.isJsonNull()) {
                JsonObject jsonObject = json.getAsJsonObject();

                Movie movie = new Movie(
                        jsonObject.get("id").getAsInt(),
                        false,
                        getYear(jsonObject.get("release_date").getAsString()),
                        jsonObject.get("title").getAsString(),
                        jsonObject.get("vote_average").getAsBigDecimal().multiply(new BigDecimal(10)).intValue(),
                        Genre.ACTION
                );

                ImageData imageData = new ImageData();

                if(!jsonObject.get("backdrop_path").isJsonNull()){
                    imageData.setBackdropImagePath(jsonObject.get("backdrop_path").getAsString());
                }
                if(!jsonObject.get("poster_path").isJsonNull()){
                    imageData.setPosterImagePath(jsonObject.get("poster_path").getAsString());
                }

                Descriptor descriptor = new Descriptor(
                        jsonObject.get("overview").toString()
                );

                movie.setImageData(imageData);
                movie.setDescriptor(descriptor);
                return movie;
            }
            return null;
        };

        gsonB.registerTypeAdapter(Movie.class, jsonD);
        return gsonB.create();
    }

    private static Date getYear(String releaseDate) {
        if(releaseDate.length() > 9 ) {
            String date = releaseDate.substring(0, 10);
            return MediaObjectHelper.parseDate(date);
        }else {
            Calendar cal = new GregorianCalendar();
            cal.set(1970, 0, 1);
            return cal.getTime();
        }
    }
}
