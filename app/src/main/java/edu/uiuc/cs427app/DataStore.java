package edu.uiuc.cs427app;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

public class DataStore extends ContentProvider {

    public DataStore() {
    }

    //TODO  -issue with inserting a preference for user that already exists
    //      - need to check if exists and perform either an update or an insert and delete


    // defining authority so that other application can access it
    static final String PROVIDER_NAME = "edu.uiuc.cs427app.provider";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME);
    public static final String PATH_CITY = "city";
    public static final String PATH_PREF = "pref";


    public static final class CityEntry implements BaseColumns {
        // Content URI represents the base location for the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CITY).build();

        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI  + "/" + PATH_CITY;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_CITY;

        // Define the table schema
        public static final String TABLE_NAME = "CITY_TABLE";
        public static final String COL_USERNAME = "USERNAME";
        public static final String COL_CITY = "CITY";
        public static final String COL_STATE = "STATE";
        public static final String COL_LATITUDE = "LATITUDE";
        public static final String COL_LONGITUDE = "LONGITUDE";

        // Build a URI to find a specific city by it's identifier
        public static Uri buildCityUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class PrefEntry implements BaseColumns {
        // Content URI represents the base location for the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PREF).build();

        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI  + "/" + PATH_PREF;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_PREF;

        // Define the table schema
        public static final String TABLE_NAME = "PREF_TABLE";
        public static final String COL_USERNAME = "USERNAME";
        public static final String COL_THEMENAME = "THEMENAME";

        // Build a URI to find a specific pref by it's identifier
        public static Uri buildPrefUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    private static final int CITY = 10;
    private static final int CITY_ID = 11;
    private static final int PREF = 20;
    private static final int PREF_ID = 21;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHelper dbOpenHelper;

    @Override
    public boolean onCreate() {
        dbOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    /**
     * Builds a UriMatcher that is used to determine which database request is being made.
     */
    public static UriMatcher buildUriMatcher(){
        String content = PROVIDER_NAME;

        // All paths to the UriMatcher have a corresponding code to return
        // when a match is found (the ints above).
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, PATH_CITY, CITY);
        matcher.addURI(content, PATH_CITY + "/#", CITY_ID);
        matcher.addURI(content, PATH_PREF, PREF);
        matcher.addURI(content, PATH_PREF + "/#", PREF_ID);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)){
            case CITY:
                return CityEntry.CONTENT_TYPE;
            case CITY_ID:
                return CityEntry.CONTENT_ITEM_TYPE;
            case PREF:
                return PrefEntry.CONTENT_TYPE;
            case PREF_ID:
                return PrefEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch(sUriMatcher.match(uri)){
            case CITY:
                _id = db.insert(CityEntry.TABLE_NAME, null, values);
                if(_id > 0){
                    returnUri =  CityEntry.buildCityUri(_id);
                } else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case PREF:
                _id = db.insert(PrefEntry.TABLE_NAME, null, values);
                if(_id > 0){
                    returnUri = PrefEntry.buildPrefUri(_id);
                } else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int rows; // Number of rows effected

        switch(sUriMatcher.match(uri)){
            case CITY:
                rows = db.delete(CityEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PREF:
                rows = db.delete(PrefEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because null could delete all rows:
        if(selection == null || rows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int rows;

        switch(sUriMatcher.match(uri)){
            case CITY:
                rows = db.delete(CityEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PREF:
                rows = db.delete(PrefEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }
    /** TODO finish this comment
     * Called whenever DATABASE_VERSION is incremented. This is used whenever schema changes need
     * to be made or new tables are added. It just deletes the tables.
     * @param uri The database being updated.
     * @param projection The previous version of the database.
     * @param selection The new version of the database.
     * @param selectionArgs The new version of the database.
     * @param sortOrder The new version of the database.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case CITY:
                retCursor = db.query(
                        CityEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CITY_ID:
                long _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        CityEntry.TABLE_NAME,
                        projection,
                        CityEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case PREF:
                retCursor = db.query(
                        PrefEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PREF_ID:
                _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        PrefEntry.TABLE_NAME,
                        projection,
                        PrefEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants. By descendants, we mean any URI that begins
        // with this path.
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /**
     *  Helper class to define database table structure and methods to create a db.
     *  Uses the column names from the contract classes above
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        // declaring version of the database
        private static final int DATABASE_VERSION = 1;

        // declaring name of the database
        private static final String DATABASE_NAME = "Team44DB";

        // defining the structure of the City Table
        static final String CREATE_DB_CITY_TABLE = " CREATE TABLE " + CityEntry.TABLE_NAME
                + " ("+CityEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT"
                + ", "+CityEntry.COL_USERNAME+" TEXT NOT NULL"
                + ", "+CityEntry.COL_CITY+" TEXT NOT NULL"
//                + ", "+CityEntry.COL_STATE+" TEXT NOT NULL"
                + ", "+CityEntry.COL_LATITUDE+" REAL NOT NULL"
                + ", "+CityEntry.COL_LONGITUDE+" REAL NOT NULL"
                + ");";
        // defining the structure of the Pref Table
        static final String CREATE_DB_PREF_TABLE = " CREATE TABLE " + PrefEntry.TABLE_NAME
                + " ("+PrefEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT"
                + ", "+PrefEntry.COL_USERNAME+" TEXT UNIQUE NOT NULL"
                + ", "+PrefEntry.COL_THEMENAME+" TEXT NOT NULL"
                + ");";

        /**
         * Default constructor.
         * @param context The application context using this database.
         */
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Called when the database is first created.
         * @param db The database being created, which all SQL statements will be executed on.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_CITY_TABLE);
            db.execSQL(CREATE_DB_PREF_TABLE);
        }
        /**
         * Called whenever DATABASE_VERSION is incremented. This is used whenever schema changes need
         * to be made or new tables are added. It just deletes the tables.
         * @param db The database being updated.
         * @param oldVersion The previous version of the database.
         * @param newVersion The new version of the database.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // sql query to drop a table
            // having similar name
            db.execSQL("DROP TABLE IF EXISTS " + CityEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + PrefEntry.TABLE_NAME);
            onCreate(db);
        }
    }


}
