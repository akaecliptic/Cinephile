package akaecliptic.dev.cinephile.Auxiliary;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import akaecliptic.dev.cinephile.Helper.MediaObjectHelper;
import akaecliptic.dev.cinephile.Helper.MySQLiteHelper;
import akaecliptic.dev.cinephile.Model.Genre;
import akaecliptic.dev.cinephile.Model.ImageData;
import akaecliptic.dev.cinephile.Model.Movie;

public class MockSQLiteDAO {

    private final SQLiteDatabase db;

    public MockSQLiteDAO(String database) {
        this.db = SQLiteDatabase.openDatabase(database, null, 0);
        init();
    }

    private void init() {
        Arrays.stream(MySQLiteHelper.TABLE_CREATES).forEach(db::execSQL);
        db.execSQL(MySQLiteHelper.DEFAULT_INSERT_COLLECTIONS);
    }

    public SQLiteDatabase database() {
        return this.db;
    }

    public void close() {
        if(db != null) db.close();
    }

    public void add(Movie movie){

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[0], movie.getId()); //ID
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[1], MediaObjectHelper.isSeen(movie.isSeen())); //Seen
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[2], MediaObjectHelper.dateToString(movie.getReleaseDate())); //ReleaseDate
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[3], movie.getTitle()); //Title
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[4], movie.getRating()); //Rating
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[5], movie.getGenre().toString()); //Genre
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[6], movie.getSubGenre().toString()); //SubGenre
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[7], movie.getMinGenre().toString()); //MinGenre

        db.insert(MySQLiteHelper.TABLE_NAMES.get("movies"), null, values);

        ContentValues posterValues = new ContentValues();
        posterValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_IMAGES[0], movie.getId()); //MovieID
        posterValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_IMAGES[1], movie.getImageData().getPosterImagePath()); //PosterPath
        posterValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_IMAGES[2], movie.getImageData().getBackdropImagePath()); //BackdropPath

        db.insert(MySQLiteHelper.TABLE_NAMES.get("m_images"), null, posterValues);

        ContentValues statsValues = new ContentValues();
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[0], movie.getId()); //MovieID
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[1], movie.getStatistic().getDescription()); //Description
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[2], movie.getStatistic().getSiteRating()); //SiteRating
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[3], movie.getStatistic().getRuntime()); //Runtime

        db.insert(MySQLiteHelper.TABLE_NAMES.get("m_stats"), null, statsValues);
    }

    public List<Movie> all(){
        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_ALL_MOVIE_DATA, null);
        ArrayList<Movie> movies = new ArrayList<>();

        c.moveToFirst();
        while (!c.isAfterLast()){

            Movie m = new Movie(
                    c.getInt(0), //ID
                    MediaObjectHelper.isSeen(c.getInt(1)), //Seen
                    MediaObjectHelper.stringToDate(c.getString(2)), //ReleaseDate
                    c.getString(3), //Title
                    c.getInt(4), //Rating
                    Genre.valueOf(c.getString(5)), //Genre
                    Genre.valueOf(c.getString(6)), //SubGenre
                    Genre.valueOf(c.getString(7)) //MinGenre
            );

            Movie.MovieStatistic statistic = new Movie.MovieStatistic(
                    c.getString(c.getColumnIndex(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[1])), //Description
                    c.getInt(c.getColumnIndex(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[2])), //SiteRating
                    c.getInt(c.getColumnIndex(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[3])) //Runtime
            );

            ImageData imageData = new ImageData(
                    c.getString(c.getColumnIndex(MySQLiteHelper.TABLE_HEADING_MOVIE_IMAGES[1])), //PosterPath
                    c.getString(c.getColumnIndex(MySQLiteHelper.TABLE_HEADING_MOVIE_IMAGES[2])) //BackdropPath
            );


            m.setStatistic(statistic);
            m.setImageData(imageData);
            movies.add(m);
            c.moveToNext();
        }

        c.close();
        return movies;
    }
}
