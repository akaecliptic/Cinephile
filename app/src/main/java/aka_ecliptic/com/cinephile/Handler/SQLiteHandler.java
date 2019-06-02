package aka_ecliptic.com.cinephile.Handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Helper.MySQLiteHelper;
import aka_ecliptic.com.cinephile.Model.Descriptor;
import aka_ecliptic.com.cinephile.Model.ImageData;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static String TAG = "DataBaseHelper";
    private static String DB_NAME = "Cinephile.db";

    private static SQLiteDatabase movieDB;
    private static SQLiteHandler sqLiteHandler;

    private final List<String> movieTableHeadings;
    private final List<String> posterTableHeadings;
    private final List<String> descriptorTableHeading;

    private SQLiteHandler(Context context) {
        super(context, DB_NAME, null, 1);

        movieTableHeadings = new ArrayList<>();
        movieTableHeadings.add("Seen");
        movieTableHeadings.add("ReleaseDate");
        movieTableHeadings.add("Title");
        movieTableHeadings.add("Rating");
        movieTableHeadings.add("Genre");
        movieTableHeadings.add("SubGenre");
        movieTableHeadings.add("MinGenre");

        posterTableHeadings = new ArrayList<>();
        posterTableHeadings.add("MovieID");
        posterTableHeadings.add("Backdrop");
        posterTableHeadings.add("ProfilePoster");

        descriptorTableHeading = new ArrayList<>();
        descriptorTableHeading.add("MovieID");
        descriptorTableHeading.add("Description");

    }

    public static synchronized SQLiteHandler getInstance(Context context){
        if (sqLiteHandler == null){
            sqLiteHandler = new SQLiteHandler(context);
        }

        return sqLiteHandler;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        generateTables(db);
    }

    private void generateTables(SQLiteDatabase db) {
        String exe1 = "CREATE TABLE IF NOT EXISTS 'movie_data' (" +
                "'ID'	INTEGER NOT NULL PRIMARY KEY," +
                "'Seen'	TEXT NOT NULL," +
                "'Year'	TEXT NOT NULL," +
                "'Title'	TEXT NOT NULL," +
                "'Rating'	TEXT NOT NULL," +
                "'Genre'	TEXT NOT NULL," +
                "'SubGenre' TEXT NOT NULL," +
                "'MinGenre' TEXT NOT NULL)";

        String exe2 = "CREATE TABLE IF NOT EXISTS 'movie_posters' ( " +
                "'MovieID' INTEGER NOT NULL UNIQUE, " +
                "'Backdrop' TEXT, " +
                "'ProfilePoster' TEXT, " +
                "FOREIGN KEY(MovieID) REFERENCES movie_data(ID))";

        String exe3 = "CREATE TABLE IF NOT EXISTS 'movie_descriptors' (" +
                "'MovieID' INTEGER NOT NULL UNIQUE," +
                "'Description' TEXT," +
                "FOREIGN KEY(MovieID) REFERENCES movie_data(ID))";

        db.execSQL(exe1);
        db.execSQL(exe2);
        db.execSQL(exe3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateTables();
    }

    private void updateTables(){
        if(tableNotExisting(MySQLiteHelper.POSTER_TABLE) ||
           tableNotExisting(MySQLiteHelper.MOVIE_TABLE) ||
           tableNotExisting(MySQLiteHelper.DESCRIPTOR_TABLE)){
            generateTables(getWritableDatabase());
        }
    }

    private boolean tableNotExisting(String tableName) {

        if(movieDB == null || !movieDB.isOpen()) {
            movieDB = getReadableDatabase();
        }

        if(!movieDB.isReadOnly()) {
            movieDB.close();
            movieDB = getReadableDatabase();
        }

        Cursor cursor = movieDB.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+ tableName +"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return false;
            }
            cursor.close();
        }
        return true;
    }

    @Override
    public synchronized void close()
    {
        if(movieDB != null)
            movieDB.close();
        super.close();
    }

    private boolean descriptorExists(int id1, int id2){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT Description FROM " + MySQLiteHelper.DESCRIPTOR_TABLE + " WHERE MovieID = " + id1 +
                        " OR MovieID = " + id2;
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        if(c.getCount() > 0){
            c.close();
            return true;
        }

        c.close();
        return false;
    }

    private boolean postersExist(int id1, int id2){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT Backdrop, ProfilePoster FROM " + MySQLiteHelper.POSTER_TABLE + " WHERE MovieID = " + id1 +
                " OR MovieID = " + id2;
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        if(c.getCount() > 0){
            if(c.getString(0) != null || c.getString(1) != null){
                return true;
            }
            c.close();
            return false;
        }

        c.close();
        return false;
    }

    public void updateEntryFromOnline(int id, Media mediaObj){
        ContentValues value = new ContentValues();
        ContentValues pValues = new ContentValues();
        ContentValues dValues = new ContentValues();

        SQLiteDatabase db = getWritableDatabase();
        List<String> list = MediaObjectHelper.movieAsList((Movie) mediaObj);

        int newId = mediaObj.getId();

        value.put("ID", newId);
        for(int i = 0; i < movieTableHeadings.size(); i++){
            value.put(movieTableHeadings.get(i), list.get(i));
        }

        db.update(MySQLiteHelper.MOVIE_TABLE,  value, "ID = " + id, null);

        if(mediaObj.getImageData() != null){
            pValues.put(posterTableHeadings.get(0), newId);
            pValues.put(posterTableHeadings.get(1), mediaObj.getImageData().getBackdropImagePath());
            pValues.put(posterTableHeadings.get(2), mediaObj.getImageData().getPosterImagePath());
            if(postersExist(id, newId)) {
                db.update(MySQLiteHelper.POSTER_TABLE, pValues, "MovieID = " + id + " OR MovieID = " + newId,
                        null);
            }else {
                db.insert(MySQLiteHelper.POSTER_TABLE, null, pValues);
            }
        }

        if(mediaObj.getDescriptor() != null){
            dValues.put(descriptorTableHeading.get(0), newId);
            dValues.put(descriptorTableHeading.get(1), mediaObj.getDescriptor().getDescription());
            if(descriptorExists(id, mediaObj.getId())){
                db.update(MySQLiteHelper.DESCRIPTOR_TABLE, dValues, "MovieID = " + id + " OR MovieID = " +
                        newId, null);
            }else{
                db.insert(MySQLiteHelper.DESCRIPTOR_TABLE, null, dValues);
            }
        }

        db.close();
    }

    /**
     *  Updates changes made to movie object to database
     *
     * @param mediaObj The movie to be updated.
     */
    public void updateEntry(Media mediaObj){
        ContentValues value = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        List<String> list = MediaObjectHelper.movieAsList((Movie) mediaObj);

        for(int i = 0; i < movieTableHeadings.size(); i++){
            if(!movieTableHeadings.get(i).equals("Rating") && !movieTableHeadings.get(i).equals("Seen"))
                value.put(movieTableHeadings.get(i), list.get(i));
        }

        db.update(MySQLiteHelper.MOVIE_TABLE,  value, "ID = ?", new String[] {String.valueOf(mediaObj.getId())});
        db.close();
    }

    /**
     *  Deletes movie and all associated data from database
     *
     * @param id The id of the movie to be deleted.
     */
    public void deleteEntry(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MySQLiteHelper.MOVIE_TABLE, "ID = ?", new String[] {String.valueOf(id)});
        db.delete(MySQLiteHelper.POSTER_TABLE, "MovieID = ?", new String[] {String.valueOf(id)});
        db.delete(MySQLiteHelper.DESCRIPTOR_TABLE, "MovieID = ?", new String[] {String.valueOf(id)});
    }

    /**
     *  Adds a new movie to database, with associated poster and descriptor data, if present.
     *
     * @param mediaObj The movie to be added.
     */
    public void newEntry(Media mediaObj){

        ContentValues value = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        List<String> list = MediaObjectHelper.movieAsList((Movie) mediaObj);

        for(int i = 0; i < movieTableHeadings.size(); i++){
            value.put(movieTableHeadings.get(i), list.get(i));
        }
        if(mediaObj.getId() != 0){
            value.put("ID", mediaObj.getId());
        }

        db.insert(MySQLiteHelper.MOVIE_TABLE, null, value);

        if(mediaObj.getImageData() != null){
            ContentValues posterValues = new ContentValues();
            posterValues.put(posterTableHeadings.get(0), mediaObj.getId());
            posterValues.put(posterTableHeadings.get(1), mediaObj.getImageData().getBackdropImagePath());
            posterValues.put(posterTableHeadings.get(2), mediaObj.getImageData().getPosterImagePath());
            db.insert(MySQLiteHelper.POSTER_TABLE, null, posterValues);
        }

        if(mediaObj.getDescriptor() != null){
            ContentValues descriptorValues = new ContentValues();
            descriptorValues.put(descriptorTableHeading.get(0), mediaObj.getId());
            descriptorValues.put(descriptorTableHeading.get(1), mediaObj.getDescriptor().getDescription());
            db.insert(MySQLiteHelper.DESCRIPTOR_TABLE, null, descriptorValues);
        }

        db.close();
    }

    /**
     * Primary Select method, retrieves all movie data from database, this include poster and
     * descriptor data. Used to fill repository with data.
     *
     * @return List<Media> A list of media objects.
     */
    public List<Media> getList(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_ALL_MOVIE_INFO, null);
        ArrayList<Media> movies = new ArrayList<>();

        c.moveToFirst();
        while (!c.isAfterLast()){
            ArrayList<String> temp = new ArrayList<>();
            int id  = c.getInt(0);
            for(int i = 1; i <= movieTableHeadings.size(); i++){
                temp.add(c.getString(i));
            }

            Movie m = MediaObjectHelper.movieFromList(id, temp);

            if(!c.isNull(c.getColumnIndex(descriptorTableHeading.get(1))))
                m.setDescriptor(new Descriptor(c.getString(c.getColumnIndex(descriptorTableHeading.get(1)))));

            ImageData imageData = new ImageData();

            if(!c.isNull(c.getColumnIndex(posterTableHeadings.get(1))))
                imageData.setBackdropImagePath(c.getString(c.getColumnIndex(posterTableHeadings.get(1))));
            if(!c.isNull(c.getColumnIndex(posterTableHeadings.get(2))))
                imageData.setPosterImagePath(c.getString(c.getColumnIndex(posterTableHeadings.get(2))));

            m.setImageData(imageData);
            movies.add(m);
            c.moveToNext();
        }

        c.close();
        return movies;
    }
}

