package akaecliptic.com.cinephile.Handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import akaecliptic.com.cinephile.Model.Media;
import akaecliptic.com.cinephile.Model.Movie;
import akaecliptic.com.cinephile.DataRepository.Repository;

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

        //TODO Add more relevant attributes later
        String exe3 = "CREATE TABLE IF NOT EXISTS 'movie_descriptors' (" +
                "'MovieID' INTEGER NOT NULL UNIQUE," +
                "'Description' TEXT," +
                "FOREIGN KEY(MovieID) REFERENCES table_of_movies(Id))";

        db.execSQL(exe1);
        db.execSQL(exe2);
        db.execSQL(exe3);
    }


    //House Cleaning stuff
    public void tempMethod(){

        //SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("DELETE FROM " + DB_POSTER_TABLE + " WHERE 1;");

        /*SQLiteDatabase db = getWritableDatabase();
        String t = "DROP TABLE temp";
        db.execSQL(t);*/

        /*String exe1 = "CREATE TABLE IF NOT EXISTS 'temp' ( " +
                "`MovieId` INTEGER NOT NULL UNIQUE, " +
                "`Backdrop` TEXT, " +
                "`ProfilePoster` TEXT, " +
                "FOREIGN KEY(MovieId) REFERENCES table_of_movies(Id))";

        String exe2 = "INSERT INTO 'temp' (MovieId, Backdrop, ProfilePoster)" +
                "SELECT MovieId,Backdrop,Profileposter FROM movie_posters";

         String exe3 = "DROP TABLE movie_posters";

        String exe4 = "ALTER TABLE 'temp' RENAME TO movie_posters";

        db.execSQL(exe1);
        db.execSQL(exe2);
        db.execSQL(exe3);
        db.execSQL(exe4);*/

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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

    public void newEntry(Media mediaObj){

        ContentValues value = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        for(int i = 0; i < movieTableHeadings.size(); i++){
            value.put(movieTableHeadings.get(i), mediaObj.asList().get(i));
        }
        if(mediaObj.getId() != 0){
            value.put("ID", mediaObj.getId());
        }

        db.insert(DB_MOVIE_TABLE, null, value);

        if(mediaObj.getImageData() != null){
            ContentValues posterValues = new ContentValues();
            posterValues.put(posterTableHeadings.get(0), mediaObj.getId());
            posterValues.put(posterTableHeadings.get(1), mediaObj.getImageData().getBackdropImagePath());
            posterValues.put(posterTableHeadings.get(2), mediaObj.getImageData().getPosterImagePath());
            db.insert(DB_POSTER_TABLE, null, posterValues);
        }

        if(mediaObj.getDescriptor() != null){
            ContentValues descriptorValues = new ContentValues();
            descriptorValues.put(descriptorTableHeading.get(0), mediaObj.getId());
            descriptorValues.put(descriptorTableHeading.get(1), mediaObj.getDescriptor().getDescription());
            db.insert(DB_DESCRIPTOR_TABLE, null, descriptorValues);
        }

        db.close();
    }

    public void deleteEntry(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + DB_MOVIE_TABLE + " WHERE Id = " + id + ";");
        db.execSQL("DELETE FROM " + DB_POSTER_TABLE+ " WHERE MovieId = " + id + ";");
        db.execSQL("DELETE FROM " + DB_DESCRIPTOR_TABLE+ " WHERE MovieId = " + id + ";");
    }

    public void updateEntryFromOnline(int id,Media mediaObj){
        ContentValues value = new ContentValues();
        ContentValues pValues = new ContentValues();
        ContentValues dValues = new ContentValues();

        SQLiteDatabase db = getWritableDatabase();
        List<String> list = mediaObj.asList();

        int newId = mediaObj.getId();

        value.put("Id", newId);
        for(int i = 0; i < movieTableHeadings.size(); i++){
            value.put(movieTableHeadings.get(i), list.get(i));
        }

        db.update(DB_MOVIE_TABLE,  value, "Id = " + id, null);


        if(mediaObj.getImageData() != null){
            pValues.put(posterTableHeadings.get(0), newId);
            pValues.put(posterTableHeadings.get(1), mediaObj.getImageData().getBackdropImagePath());
            pValues.put(posterTableHeadings.get(2), mediaObj.getImageData().getPosterImagePath());
            if(postersExist(id, newId)) {
                db.update(DB_POSTER_TABLE, pValues, "MovieID = " + id + " OR MovieID = " + newId,
                        null);
            }else {
                db.insert(DB_POSTER_TABLE, null, pValues);
            }
        }

        if(mediaObj.getDescriptor() != null){
            dValues.put(descriptorTableHeading.get(0), newId);
            dValues.put(descriptorTableHeading.get(1), mediaObj.getDescriptor().getDescription());
            if(descriptorExists(id, mediaObj.getId())){
                db.update(DB_DESCRIPTOR_TABLE, dValues, "MovieID = " + id + " OR MovieID = " +
                        newId, null);
            }else{
                db.insert(DB_DESCRIPTOR_TABLE, null, dValues);
            }
        }

        db.close();
    }

    public void updateEntry(Media mediaObj){
        ContentValues value = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        List<String> list = mediaObj.asList();

        for(int i = 0; i < movieTableHeadings.size(); i++){
            value.put(movieTableHeadings.get(i), list.get(i));
        }

        db.update(DB_MOVIE_TABLE,  value, "Id = " + mediaObj.getId(), null);
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

    private void getPosterData(Media media,int id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT Backdrop, ProfilePoster FROM " + DB_POSTER_TABLE + " WHERE MovieID = " + id;
        Cursor c = db.rawQuery(query, null);

        Media.ImageData imageData = null;
        c.moveToFirst();

        if(c.getCount() > 0){
            imageData = new Media.ImageData(c.getString(0), c.getString(1));
        }
        c.close();
        media.setImageData(imageData);
    }

    private void getDescriptor(Media media, int id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT Description FROM " + DB_DESCRIPTOR_TABLE + " WHERE MovieID = " + id;
        Cursor c = db.rawQuery(query, null);

        Media.Descriptor descriptor = null;
        c.moveToFirst();

        if(c.getCount() > 0){
            descriptor = new Media.Descriptor(c.getString(0));
        }

        c.close();
        media.setDescriptor(descriptor);
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

        List<Media> temp = fillList().getItems();
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

    public Repository<Media> fillList(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + DB_MOVIE_TABLE + " WHERE 1";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        ArrayList<Media> movies = new ArrayList<>();
        while (!c.isAfterLast()){
            ArrayList<String> temp = new ArrayList<>();
            int id  = c.getInt(0);
            for(int i = 1; i < c.getColumnCount(); i++){
                temp.add(c.getString(i));
            }
            Movie m = new Movie(id, temp);

            movies.add(m);
            c.moveToNext();
        }

        c.close();

        for (Media movie: movies){
            getPosterData(movie, movie.getId());
            getDescriptor(movie, movie.getId());
        }

        return new Repository<>(movies);
    }
}

