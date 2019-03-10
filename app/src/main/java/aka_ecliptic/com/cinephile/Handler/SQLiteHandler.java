package aka_ecliptic.com.cinephile.Handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import aka_ecliptic.com.cinephile.Helper.MediaListConverter;
import aka_ecliptic.com.cinephile.Helper.MySQLiteHelper;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static String TAG = "DataBaseHelper";
    private static String DB_NAME = "Movies.db";

    private static String DB_MOVIE_TABLE = "table_of_movies";
    private static String DB_POSTER_TABLE = "movie_posters";
    private static String DB_DESCRIPTOR_TABLE = "movie_descriptors";

    private static SQLiteDatabase movieDB;
    private static SQLiteHandler sqLiteHandler;

    private final List<String> movieTableHeadings;
    private final List<String> posterTableHeadings;
    private final List<String> descriptorTableHeading;

    private SQLiteHandler(Context context) {
        super(context, DB_NAME, null, 1);

        movieTableHeadings = new ArrayList<>();
        movieTableHeadings.add("Seen");
        movieTableHeadings.add("Year");
        movieTableHeadings.add("Title");
        movieTableHeadings.add("Rating");
        movieTableHeadings.add("Genre");

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
        String exe1 = "CREATE TABLE IF NOT EXISTS `table_of_movies` (" +
                "`Id`	INTEGER NOT NULL PRIMARY KEY," +
                "`Seen`	TEXT NOT NULL," +
                "`Year`	TEXT NOT NULL," +
                "`Title`	TEXT NOT NULL," +
                "`Rating`	TEXT NOT NULL," +
                "`Genre`	TEXT NOT NULL)";

        String exe2 = "CREATE TABLE IF NOT EXISTS 'movie_posters' ( " +
                "`MovieId` INTEGER NOT NULL UNIQUE, " +
                "`Backdrop` TEXT, " +
                "`ProfilePoster` TEXT, " +
                "FOREIGN KEY(MovieId) REFERENCES table_of_movies(Id))";

        String exe3 = "CREATE TABLE IF NOT EXISTS 'movie_descriptors' (" +
                "'MovieId' INTEGER NOT NULL UNIQUE," +
                "'Description' TEXT," +
                "FOREIGN KEY(MovieID) REFERENCES table_of_movies(Id))";

        db.execSQL(exe1);
        db.execSQL(exe2);
        db.execSQL(exe3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateTables();
    }

    public void updateTables(){
        if(tableNotExisting(DB_POSTER_TABLE) ||
           tableNotExisting(DB_MOVIE_TABLE) ||
           tableNotExisting(DB_DESCRIPTOR_TABLE)){
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

    public void updateEntryFromOnline(int id,Media mediaObj){
        ContentValues value = new ContentValues();
        ContentValues pValues = new ContentValues();
        ContentValues dValues = new ContentValues();

        SQLiteDatabase db = getWritableDatabase();
        List<String> list = MediaListConverter.asList(mediaObj);

        int newId = mediaObj.getId();

        value.put("Id", newId);
        for(int i = 0; i < movieTableHeadings.size(); i++){
            value.put(movieTableHeadings.get(i), list.get(i));
        }

        db.update(MySQLiteHelper.MOVIE_TABLE,  value, "Id = " + id, null);


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

    public void updateEntry(Media mediaObj){
        ContentValues value = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        List<String> list = MediaListConverter.asList(mediaObj);

        for(int i = 0; i < movieTableHeadings.size(); i++){
            value.put(movieTableHeadings.get(i), list.get(i));
        }

        db.update(MySQLiteHelper.MOVIE_TABLE,  value, "Id = " + mediaObj.getId(), null);
        db.close();
    }

    private boolean descriptorExists(int id1, int id2){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT Description FROM " + DB_DESCRIPTOR_TABLE + " WHERE MovieID = " + id1 +
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
        String query = "SELECT Backdrop, ProfilePoster FROM " + DB_POSTER_TABLE + " WHERE MovieID = " + id1 +
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


    public boolean isInDB(Media m){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT Id FROM " + DB_MOVIE_TABLE + " WHERE Id = " + m.getId();

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        if(c.getCount() > 0){
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    public boolean isInDB(String title){
        ArrayList<String> temp = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT Title FROM " + DB_MOVIE_TABLE + " WHERE 1";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            String t  = c.getString(0);
            temp.add(t);

            c.moveToNext();
        }
        c.close();
        for(String t : temp){
            String inDB = t.toLowerCase().trim().replace(" ", "");
            String toCompare = title.toLowerCase().trim().replace(" ", "");
            if(inDB.equals(toCompare)){
                return true;
            }
        }
        return false;
    }

    public boolean isInDB(String title, int year){

        List<Media> temp = getList();
        for(Media t : temp){
            String inDB = t.getTitle().toLowerCase().trim().replace(" ", "");
            String toCompare = title.toLowerCase().trim().replace(" ", "");
            if(inDB.equals(toCompare) && t.getYear() == year){
                return true;
            }
        }
        return false;
    }

    public Media getInDB(Media base, Media toCheck){

        String titleToCheck = toCheck.getTitle().toLowerCase().trim().replace(" ", "");
        String titleBase = base.getTitle().toLowerCase().trim().replace(" ", "");

        if(titleBase.equals(titleToCheck)){
            return base;
        }
        return null;
    }

    public void deleteEntry(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MySQLiteHelper.MOVIE_TABLE, "Id = ?", new String[] {String.valueOf(id)});
        db.delete(MySQLiteHelper.POSTER_TABLE, "MovieId = ?", new String[] {String.valueOf(id)});
        db.delete(MySQLiteHelper.DESCRIPTOR_TABLE, "MovieId = ?", new String[] {String.valueOf(id)});
    }

    /**
     *
     * @param mediaObj
     */
    public void newEntry(Media mediaObj){

        ContentValues value = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        for(int i = 0; i < movieTableHeadings.size(); i++){
            value.put(movieTableHeadings.get(i), MediaListConverter.asList(mediaObj).get(i));
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
     * Primary Select method used to get movie from the DB,
     * used only really by a Repository object.
     *
     * @return List<Media> A list of media objects
     */
    public List<Media> getList(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_ALL_MOVIES, null);
        ArrayList<Media> movies = new ArrayList<>();

        c.moveToFirst();
        while (!c.isAfterLast()){
            ArrayList<String> temp = new ArrayList<>();
            int id  = c.getInt(0);
            for(int i = 1; i < c.getColumnCount(); i++){
                temp.add(c.getString(i));
            }

            System.out.println(db.getPath());
            Movie m = MediaListConverter.fromList(id, temp);

            movies.add(m);
            c.moveToNext();
        }

        c.close();

        for (Media movie: movies){
            getPosterData(movie, movie.getId());
            getDescriptor(movie, movie.getId());
        }

        return movies;
    }

    /**
     * Called by getList, if a poster is retrieved it will be added to the movie parsed.
     *
     * @param media The movie that the poster should be attached to.
     * @param id Used to find the appropriate poster
     */
    private void getPosterData(Media media,int id){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_POSTERS_BY_ID, new String[] {String.valueOf(id)});

        Media.ImageData imageData = null;
        c.moveToFirst();

        if(c.getCount() > 0){
            imageData = new Media.ImageData(c.getString(0), c.getString(1));
        }
        c.close();
        media.setImageData(imageData);
    }

    /**
     * Called by getList, if a descriptor is retrieved it will be added to the movie parsed.
     *
     * @param media The movie that the poster should be attached to.
     * @param id Used to find the appropriate poster
     */
    private void getDescriptor(Media media, int id){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(MySQLiteHelper.SELECT_DESCRIPTORS_BY_ID, new String[] {String.valueOf(id)});

        Media.Descriptor descriptor = null;
        c.moveToFirst();

        if(c.getCount() > 0){
            descriptor = new Media.Descriptor(c.getString(0));
        }

        c.close();
        media.setDescriptor(descriptor);
    }
}

