package akaecliptic.dev.cinephile.Architecture;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import akaecliptic.dev.cinephile.Helper.MediaObjectHelper;
import akaecliptic.dev.cinephile.Helper.MySQLiteHelper;
import akaecliptic.dev.cinephile.Model.Genre;
import akaecliptic.dev.cinephile.Model.ImageData;
import akaecliptic.dev.cinephile.Model.Movie;

/**
 * Old Version of Database DAO. Deprecating for newer version.
 */
@Deprecated
public class SQLiteDAO extends SQLiteOpenHelper {

    private static String TAG = "SQLDataBase";
    private static String DB_NAME = "Cinephile.db";
    private static int DB_VERSION = 2;

    private static SQLiteDatabase movieDB;
    private static SQLiteDAO sqLiteDAO;

    private SQLiteDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    static synchronized SQLiteDAO getInstance(Context context){
        if (sqLiteDAO == null){
            sqLiteDAO = new SQLiteDAO(context);
        }
        return sqLiteDAO;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        generateTablesCurrent(db);
    }

    private void generateTablesPrev(SQLiteDatabase db) {
        for (int i = 0; i < 3; i++)
            db.execSQL(MySQLiteHelper.TABLE_CREATES[i]);
    }

    private void generateTablesCurrent(SQLiteDatabase db) {
        Arrays.stream(MySQLiteHelper.TABLE_CREATES).forEach(db::execSQL);
        db.execSQL(MySQLiteHelper.DEFAULT_INSERT_COLLECTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == DB_VERSION)
            generateTablesCurrent(db);
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
        db.update(MySQLiteHelper.TABLE_NAMES.get("m_images"),  posterValues, "ID = ?", new String[] {String.valueOf(id)});

        ContentValues statsValues = new ContentValues();
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[1], statistic.getDescription()); //Description
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[2], statistic.getSiteRating()); //SiteRating
        statsValues.put(MySQLiteHelper.TABLE_HEADING_MOVIE_STATS[3], statistic.getRuntime()); //Runtime
        db.update(MySQLiteHelper.TABLE_NAMES.get("m_stats"),  statsValues, "ID = ?", new String[] {String.valueOf(id)});

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

        db.update(MySQLiteHelper.TABLE_NAMES.get("movies"),  values, "ID = ?", new String[] {String.valueOf(movie.getId())});

        db.close();
    }

    /**
     *  Deletes movie and all associated data from database
     *
     * @param id The id of the movie to be deleted.
     */
    void deleteMovie(int id){
        SQLiteDatabase db = getWritableDatabase();

        db.delete(MySQLiteHelper.TABLE_NAMES.get("movies"), "ID = ?", new String[] {String.valueOf(id)});
        db.delete(MySQLiteHelper.TABLE_NAMES.get("m_stats"), "MovieID = ?", new String[] {String.valueOf(id)});
        db.delete(MySQLiteHelper.TABLE_NAMES.get("m_images"), "MovieID = ?", new String[] {String.valueOf(id)});
        db.delete(MySQLiteHelper.TABLE_NAMES.get("c_m_link"), "MovieID = ?", new String[] {String.valueOf(id)});

        db.close();
    }

    void deleteCollection(String name){
        SQLiteDatabase db = getWritableDatabase();

        if(name.equals("Favourites"))
            return;

        db.execSQL(MySQLiteHelper.DELETE_COLLECTION_LINK, new String[]{name});
        db.delete(MySQLiteHelper.TABLE_NAMES.get("collections"), "Name = ?", new String[] {name});

        db.close();
    }

    private void removeMovieFromCollection(String collection, Integer movieId){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL(MySQLiteHelper.DELETE_FROM_COLLECTION, new Object[]{collection, movieId});

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

        db.close();
    }

    void toggleFavourite(int id, boolean favourite) {
        if(favourite){
            addMovieToCollection(MySQLiteHelper.DEFAULT_COLLECTIONS[1], id);
        }else {
            removeMovieFromCollection(MySQLiteHelper.DEFAULT_COLLECTIONS[1], id);
        }
    }

    void toggleCollection(String name, int movieId, boolean set) {
        if(set){
            addMovieToCollection(name, movieId);
        }else {
            removeMovieFromCollection(name, movieId);
        }
    }

    void addCollection(String name, int type){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_HEADING_COLLECTIONS[1], name); //Name
        values.put(MySQLiteHelper.TABLE_HEADING_COLLECTIONS[2], type); //Type

        db.insert(MySQLiteHelper.TABLE_NAMES.get("collections"), null, values);

        db.close();
    }

    private void addMovieToCollection(String collection, Integer movieId){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL(MySQLiteHelper.INSERT_COLLECTION_MOVIE, new Object[]{collection, movieId});

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

    boolean isFavourited(int id){
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_MOVIE_ID_FAVOURITES, new String[]{String.valueOf(id)});
        c.moveToFirst();

        boolean isPresent = c.getCount() != 0;

        c.close();
        db.close();
        return isPresent;
    }

    List<String> getCollectionNames(){
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_COLLECTION_NAMES, null);
        ArrayList<String> names = new ArrayList<>();

        c.moveToFirst();
        while (!c.isAfterLast()){
            names.add(c.getString(0));
            c.moveToNext();
        }

        c.close();
        db.close();
        return names;
    }

    List<String> getCollectionHeadings(){
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_COLLECTION_HEADINGS, null);
        ArrayList<String> names = new ArrayList<>();

        c.moveToFirst();
        while (!c.isAfterLast()){
            names.add(c.getString(0));
            c.moveToNext();
        }

        c.close();
        db.close();

        return names;
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

    List<Integer> getMoviesLike(List<String> titles) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_IDS_WITH_TITLES_IN(titles.size()), titles.toArray(new String[0]));
        ArrayList<Integer> ids = new ArrayList<>();

        c.moveToFirst();
        while (!c.isAfterLast()){
            ids.add(c.getInt(0)); //ID
            c.moveToNext();
        }

        c.close();
        db.close();
        return ids;
    }

    List<Movie> getMoviesFromCollection(String name) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_MOVIE_FROM_LIST, new String[]{name});
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

    List<Integer> getMovieIDsFromCollection(String name) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_MOVIE_ID_FROM_LIST, new String[]{name});
        ArrayList<Integer> ids = new ArrayList<>();

        c.moveToFirst();
        while (!c.isAfterLast()){
            ids.add(c.getInt(0));
            c.moveToNext();
        }

        c.close();
        db.close();
        return ids;
    }

    List<String> getCollectionsIn(Movie movie) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_ASSOCIATED_COLLECTIONS, new String[]{String.valueOf(movie.getId())});
        ArrayList<String> collections = new ArrayList<>();

        c.moveToFirst();
        while (!c.isAfterLast()){
            collections.add(c.getString(0));
            c.moveToNext();
        }

        c.close();
        db.close();
        return collections;
    }
}

