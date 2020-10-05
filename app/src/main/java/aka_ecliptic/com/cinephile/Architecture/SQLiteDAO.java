package aka_ecliptic.com.cinephile.Architecture;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Helper.MySQLiteHelper;
import aka_ecliptic.com.cinephile.Model.Genre;
import aka_ecliptic.com.cinephile.Model.ImageData;
import aka_ecliptic.com.cinephile.Model.Movie;

class SQLiteDAO extends SQLiteOpenHelper {

    private static String TAG = "SQLDataBase";
    private static String DB_NAME = "Cinephile.db";

    private static SQLiteDatabase movieDB;
    private static SQLiteDAO sqLiteDAO;

    private SQLiteDAO(Context context) {
        super(context, DB_NAME, null, 1);
    }

    static synchronized SQLiteDAO getInstance(Context context){
        if (sqLiteDAO == null){
            sqLiteDAO = new SQLiteDAO(context);
        }
        return sqLiteDAO;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        generateTables(db);
    }

    private void generateTables(SQLiteDatabase db) {
        String exe1 = "CREATE TABLE IF NOT EXISTS 'movies' (" +
                "'ID'	INTEGER NOT NULL PRIMARY KEY," +
                "'Seen'	INTEGER NOT NULL," +
                "'ReleaseDate'	TEXT NOT NULL," +
                "'Title'	TEXT NOT NULL," +
                "'Rating'	INTEGER NOT NULL," +
                "'Genre'	TEXT NOT NULL," +
                "'SubGenre' TEXT NOT NULL," +
                "'MinGenre' TEXT NOT NULL)";

        String exe2 = "CREATE TABLE IF NOT EXISTS 'movie_statistics' (" +
                "'MovieID' INTEGER NOT NULL UNIQUE," +
                "'Description' TEXT," +
                "'SiteRating' INTEGER," +
                "'Runtime' INTEGER," +
                "FOREIGN KEY(MovieID) REFERENCES movies(ID))";

        String exe3 = "CREATE TABLE IF NOT EXISTS 'movie_images' ( " +
                "'MovieID' INTEGER NOT NULL UNIQUE, " +
                "'PosterPath' TEXT, " +
                "'BackdropPath' TEXT, " +
                "FOREIGN KEY(MovieID) REFERENCES movies(ID))";

        db.execSQL(exe1);
        db.execSQL(exe2);
        db.execSQL(exe3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public synchronized void close() {
        if(movieDB != null)
            movieDB.close();
        super.close();
    }

    /**
     *  Updates the accompanying data for Movie object, i.e ImageData and Statistics.
     *
     * @param id The ID of the movie that the data belongs to.
     * @param statistic The Statistics object to be updated.
     * @param imageData The ImageData object to be updated.
     */
    public void updateMovieData(int id, Movie.MovieStatistic statistic, ImageData imageData){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues posterValues = new ContentValues();
        posterValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_IMAGES[1], imageData.getPosterImagePath()); //PosterPath
        posterValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_IMAGES[2], imageData.getBackdropImagePath()); //BackdropPath
        db.update(MySQLiteHelper.TABLE_NAMES[0],  posterValues, "ID = ?", new String[] {String.valueOf(id)});

        ContentValues statsValues = new ContentValues();
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[1], statistic.getDescription()); //Description
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[2], statistic.getSiteRating()); //SiteRating
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[3], statistic.getRuntime()); //Runtime
        db.update(MySQLiteHelper.TABLE_NAMES[0],  statsValues, "ID = ?", new String[] {String.valueOf(id)});

        db.close();
    }

    /**
     *  Updates changes made to movie object to database
     *
     * @param movie The movie to be updated.
     */
    void updateMovie(Movie movie){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[1], MediaObjectHelper.isSeen(movie.isSeen())); //Seen
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[2], MediaObjectHelper.dateToString(movie.getReleaseDate())); //ReleaseDate
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[3], movie.getTitle()); //Title
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[4], movie.getRating()); //Rating
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[5], movie.getGenre().toString()); //Genre
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[6], movie.getSubGenre().toString()); //SubGenre
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[7], movie.getMinGenre().toString()); //MinGenre

        db.update(MySQLiteHelper.TABLE_NAMES[0],  values, "ID = ?", new String[] {String.valueOf(movie.getId())});

        db.close();
    }

    /**
     *  Deletes movie and all associated data from database
     *
     * @param id The id of the movie to be deleted.
     */
    void deleteMovie(int id){
        SQLiteDatabase db = getWritableDatabase();

        db.delete(MySQLiteHelper.TABLE_NAMES[0], "ID = ?", new String[] {String.valueOf(id)});
        db.delete(MySQLiteHelper.TABLE_NAMES[1], "MovieID = ?", new String[] {String.valueOf(id)});
        db.delete(MySQLiteHelper.TABLE_NAMES[2], "MovieID = ?", new String[] {String.valueOf(id)});

        db.close();
    }

    /**
     *  Adds a new movie to database, with associated poster and statistic data.
     *
     * @param movie The movie to be added.
     */
    void addMovie(Movie movie){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[0], movie.getId()); //ID
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[1], MediaObjectHelper.isSeen(movie.isSeen())); //Seen
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[2], MediaObjectHelper.dateToString(movie.getReleaseDate())); //ReleaseDate
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[3], movie.getTitle()); //Title
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[4], movie.getRating()); //Rating
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[5], movie.getGenre().toString()); //Genre
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[6], movie.getSubGenre().toString()); //SubGenre
        values.put(MySQLiteHelper.TABLE_HEADING_MOVIE[7], movie.getMinGenre().toString()); //MinGenre

        db.insert(MySQLiteHelper.TABLE_NAMES[0], null, values);

        ContentValues posterValues = new ContentValues();
        posterValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_IMAGES[0], movie.getId()); //MovieID
        posterValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_IMAGES[1], movie.getImageData().getPosterImagePath()); //PosterPath
        posterValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_IMAGES[2], movie.getImageData().getBackdropImagePath()); //BackdropPath

        db.insert(MySQLiteHelper.TABLE_NAMES[2], null, posterValues);

        ContentValues statsValues = new ContentValues();
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[0], movie.getId()); //MovieID
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[1], movie.getStatistic().getDescription()); //Description
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[2], movie.getStatistic().getSiteRating()); //SiteRating
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[3], movie.getStatistic().getRuntime()); //Runtime

        db.insert(MySQLiteHelper.TABLE_NAMES[1], null, statsValues);

        db.close();
    }

    boolean isMoviePresent(int id){
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_MOVIE_ID, new String[]{String.valueOf(id)});
        c.moveToFirst();

        boolean isPresent = c.getCount() != 0;

        c.close();
        db.close();
        return isPresent;
    }

    Movie getMovie(int id){
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_MOVIE_BY_ID, new String[]{String.valueOf(id)});
        Movie movie;

        c.moveToFirst();

        if (c.getCount() == 0){
            c.close();
            db.close();
            return null;
        }

        movie = new Movie(
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

        movie.setStatistic(statistic);
        movie.setImageData(imageData);

        c.close();
        db.close();
        return movie;
    }

    /**
     * Primary Select method, retrieves all movie data from database, this include poster and
     * statistic data. Used to fill repository with data.
     *
     * @return List<Movie> A list of movie objects.
     */
    List<Movie> getAllMovies(){
        SQLiteDatabase db = getWritableDatabase();

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
        db.close();
        return movies;
    }

    List<Movie> getMoviesLike(String query) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_MOVIE_LIKE, new String[]{"%" + query + "%"});
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
        db.close();
        return movies;
    }
}

