package aka_ecliptic.com.cinephile.Handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;

public class GsonMovieConverter {

    public static Gson getCustomGson(){
        GsonBuilder gsonB = new GsonBuilder();
        JsonDeserializer<Movie> jsonD = new JsonDeserializer<Movie>() {

            @Override
            public Movie deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonNull()) {
                    JsonObject jsonObject = json.getAsJsonObject();

                    Movie movie = new Movie(
                            jsonObject.get("id").getAsInt(),
                            false,
                            getYear(jsonObject.get("release_date").getAsString()),
                            jsonObject.get("title").getAsString(),
                            (int) jsonObject.get("vote_average").getAsDouble() * 10,
                            Media.Genre.NULL
                    );

                    Media.ImageData imageData = new Media.ImageData();

                    if(!jsonObject.get("backdrop_path").isJsonNull()){
                        imageData.setBackdropImagePath(jsonObject.get("backdrop_path").getAsString());
                    }
                    if(!jsonObject.get("poster_path").isJsonNull()){
                        imageData.setPosterImagePath(jsonObject.get("poster_path").getAsString());
                    }

                    Media.Descriptor descriptor = new Media.Descriptor(
                            jsonObject.get("overview").toString()
                    );

                    movie.setImageData(imageData);
                    movie.setDescriptor(descriptor);
                    return movie;
                }
                return null;
            }
        };

        gsonB.registerTypeAdapter(Movie.class, jsonD);
        return gsonB.create();
    }

    private static int getYear(String release_date) {
        if(release_date.length() > 4 ) {
            String year = release_date.substring(0, 4);
            return Integer.parseInt(year);
        }else {
            return 1988;
        }
    }


    /*private Gson gson;

    public GsonMovieConverter(){
        gson = new Gson();
    }

    public GsonMovieConverter(JsonArray json){
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
    }

    public void loadMovie(JsonArray json,Movie movie){

    }*/

}
