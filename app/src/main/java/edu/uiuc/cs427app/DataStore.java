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


    // defining authority so that other application can access the content provider
    // however "exported" is set to false in the Manifest for this app, most of the
    // functionality is not needed since we aren't exporting data and the app would
    // work just fine with direct calls to the SQLite db
    static final String PROVIDER_NAME = "edu.uiuc.cs427app.provider";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME);

    //paths that can be appended to the URI, representing different tables
    public static final String PATH_CITY = "city";
    public static final String PATH_PREF = "pref";
    public static final String PATH_WEATHER = "weather";

    //defines the URI and table/column names for the city information
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

    //defines the URI and table/column names for the user preference information
    //we ended up not using this functionality
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
    // TODO (post Milestone 4) add timestamp, long, and let columns to database for weather metric entries
    //defines the URI and table/column names for the weather information
    public static final class WeatherEntry implements BaseColumns {
        // Content URI represents the base location for the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI  + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_WEATHER;

        // Define the table schema
        public static final String TABLE_NAME = "WEATHER_TABLE";
        public static final String COL_CITY = "CITY";
        public static final String COL_TEMPERATURE = "TEMPERATURE";
        public static final String COL_TEMPERATUREMAX = "TEMPERATUREMAX";
        public static final String COL_TEMPERATUREMIN = "TEMPERATUREMIN";
        public static final String COL_DESCRIPTION = "DESCRIPTION";
        public static final String COL_WINDSPEED = "WINDSPEED";
        public static final String COL_HUMIDITY = "HUMIDITY";
        public static final String COL_DEWPOINT = "DEWPOINT";
        public static final String COL_UV = "UV";
        public static final String COL_AIRINDEX = "AIRINDEX";
        public static final String COL_PRECIPITATION = "PRECIPITATION";
        public static final String COL_PRECIPITATIONCHANCE = "PRECIPITATIONCHANCE";

        // Build a URI to find a specific city by it's identifier
        public static Uri buildWeatherUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    //define the different ID's used in the URI matcher so the matcher can build
    //the appropriate database table
    private static final int CITY = 10;
    private static final int CITY_ID = 11;
    private static final int PREF = 20;
    private static final int PREF_ID = 21;
    private static final int WEATHER = 30;
    private static final int WEATHER_ID = 31;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // initializes the dbOpenHelper object which creates/returns the db for calls
    private DatabaseHelper dbOpenHelper;


    /**
     * creates a new DB helper object which is used to access the db.
     */
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
        matcher.addURI(content, PATH_WEATHER, WEATHER);
        matcher.addURI(content, PATH_WEATHER + "/#", WEATHER_ID);

        return matcher;
    }
    /**
     * Builds a UriMatcher that is used to determine which database request is being made.
     * @param uri The Uri for which the type is being determined.
     * @return A string representing the type of database request to be made.
     */
    @Override
    public String getType(Uri uri) {
        // Use the UriMatcher to match the incoming Uri with predefined patterns
        switch(sUriMatcher.match(uri)){
            // If the Uri matches the general CITY pattern
            case CITY:
                // Return the content type for a list of cities
                return CityEntry.CONTENT_TYPE;
            // If the Uri matches the specific CITY_ID pattern
            case CITY_ID:
                // Return the content item type for a single city
                return CityEntry.CONTENT_ITEM_TYPE;
            // If the Uri matches the general PREF pattern
            case PREF:
                // Return the content type for a list of preferences
                return PrefEntry.CONTENT_TYPE;
            // If the Uri matches the specific PREF_ID pattern
            case PREF_ID:
                // Return the content item type for a single preference
                return PrefEntry.CONTENT_ITEM_TYPE;
            // If the Uri matches the general WEATHER pattern
            case WEATHER:
                // Return the content type for a list of weather entries
                return WeatherEntry.CONTENT_TYPE;
            // If the Uri matches the specific WEATHER_ID pattern
            case WEATHER_ID:
                // Return the content item type for a single weather entry
                return WeatherEntry.CONTENT_ITEM_TYPE;
            // If the Uri does not match any predefined pattern, throw an exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * A wrapper for a database INSERT.
     * @param uri The URI with content scheme.
     * @param values key/value pair data to update in db.
     * @return result URI
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Get a writable database instance
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        // Variables to store the result of the insert operation
        long _id;
        Uri returnUri;
        // Use a switch statement to determine the type of data to insert based on the URI
        switch(sUriMatcher.match(uri)){
            case CITY:
                // Insert data into the CityEntry table
                _id = db.insert(CityEntry.TABLE_NAME, null, values);
                if(_id > 0){
                    // If the insertion was successful, build and return the corresponding URI
                    returnUri =  CityEntry.buildCityUri(_id);
                } else{
                    // If the insertion was unsuccessful, throw an exception
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case PREF:
                // Insert data into the PrefEntry table
                _id = db.insert(PrefEntry.TABLE_NAME, null, values);
                if(_id > 0){
                    // If the insertion was successful, build and return the corresponding URI
                    returnUri = PrefEntry.buildPrefUri(_id);
                } else{
                    // If the insertion was unsuccessful, throw an exception
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case WEATHER:
                // Insert data into the WeatherEntry table
                _id = db.insert(WeatherEntry.TABLE_NAME, null, values);
                if(_id > 0){
                    // If the insertion was successful, build and return the corresponding URI
                    returnUri = WeatherEntry.buildWeatherUri(_id);
                } else{
                    // If the insertion was unsuccessful, throw an exception
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                // If the URI does not match any known cases, throw an exception
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the URI after the insertion
        return returnUri;
    }

    /**
     * A wrapper for a database DELETE.
     * @param uri The URI with content scheme.
     * @param selection a filter, formatted as a SQL WHERE clause
     * @param selectionArgs used to replace ?'s in selection.
     * @return the number of rows deleted
     */
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
            case WEATHER:
                rows = db.delete(WeatherEntry.TABLE_NAME, selection, selectionArgs);
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
    /**
     * A wrapper for a database UPDATE.
     * @param uri The URI with content scheme.
     * @param values key/value pair data to update in db.
     * @param selection a filter, formatted as a SQL WHERE clause
     * @param selectionArgs used to replace ?'s in selection.
     * @return the number of rows updated
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int rows;

        switch(sUriMatcher.match(uri)){
            case CITY:
                rows = db.update(CityEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PREF:
                rows = db.update(PrefEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case WEATHER:
                rows = db.update(WeatherEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }
    /**
     * A wrapper for a database QUERY.
     * @param uri The URI with content scheme.
     * @param projection a list of database columns to return.
     * @param selection a filter, formatted as a SQL WHERE clause
     * @param selectionArgs used to replace ?'s in selection.
     * @param sortOrder how to order rows, formatted as SQL ORDER BY clause.
     * @return a cursor with the result rows from query
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
            case WEATHER:
                retCursor = db.query(
                        WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case WEATHER_ID:
                _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        WeatherEntry.TABLE_NAME,
                        projection,
                        WeatherEntry._ID + " = ?",
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
                + ", "+CityEntry.COL_LATITUDE+" REAL NOT NULL"
                + ", "+CityEntry.COL_LONGITUDE+" REAL NOT NULL"
                + ");";
        // defining the structure of the Pref Table
        static final String CREATE_DB_PREF_TABLE = " CREATE TABLE " + PrefEntry.TABLE_NAME
                + " ("+PrefEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT"
                + ", "+PrefEntry.COL_USERNAME+" TEXT UNIQUE NOT NULL"
                + ", "+PrefEntry.COL_THEMENAME+" TEXT NOT NULL"
                + ");";
        // defining the structure of the Weather Table
        static final String CREATE_DB_WEATHER_TABLE = " CREATE TABLE " + WeatherEntry.TABLE_NAME
                + " ("+WeatherEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT"
                + ", "+WeatherEntry.COL_CITY+" TEXT NOT NULL"
                + ", "+WeatherEntry.COL_TEMPERATURE+" REAL NOT NULL"
                + ", "+WeatherEntry.COL_TEMPERATUREMAX+" REAL NOT NULL"
                + ", "+WeatherEntry.COL_TEMPERATUREMIN+" REAL NOT NULL"
                + ", "+WeatherEntry.COL_DESCRIPTION+" TEXT NOT NULL"
                + ", "+WeatherEntry.COL_WINDSPEED+" REAL NOT NULL"
                + ", "+WeatherEntry.COL_HUMIDITY+" REAL NOT NULL"
                + ", "+WeatherEntry.COL_DEWPOINT+" REAL NOT NULL"
                + ", "+WeatherEntry.COL_UV+" REAL NOT NULL"
                + ", "+WeatherEntry.COL_AIRINDEX+" REAL NOT NULL"
                + ", "+WeatherEntry.COL_PRECIPITATION+" REAL NOT NULL"
                + ", "+WeatherEntry.COL_PRECIPITATIONCHANCE+" REAL NOT NULL"
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
            db.execSQL(CREATE_DB_WEATHER_TABLE);
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
            db.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
            onCreate(db);
        }
    }


}
