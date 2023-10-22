package edu.uiuc.cs427app;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import java.util.HashMap;
public class DataStore extends ContentProvider {

    public DataStore() {
    }
    // defining authority so that other application can access it
    static final String PROVIDER_NAME = "edu.uiuc.cs427app.provider";

    // defining content URI
    static final String URL = "content://" + PROVIDER_NAME + "/city_table";

    // parsing the content URI
    static final Uri CONTENT_URI = Uri.parse(URL);
    static final int uriCode = 1;
    static final UriMatcher uriMatcher;
    private static HashMap<String, String> values;

    static {

        // to match the content URI
        // every time user access table under content provider
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // to access whole table
        uriMatcher.addURI(PROVIDER_NAME, "city_table", uriCode);

        // to access a particular row
        // of the table
        uriMatcher.addURI(PROVIDER_NAME, "city_table/*", uriCode);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                count = db.delete(CITY_TABLE, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case uriCode:
                return "vnd.android.cursor.dir/city_table";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(CITY_TABLE, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLiteException("Failed to add a record into " + uri);
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        if (db != null) {
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CITY_TABLE);
        switch (uriMatcher.match(uri)) {
            case uriCode:
                qb.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == "") {
            sortOrder = COL_ID;
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                count = db.update(CITY_TABLE, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    // creating object of database
    // to perform query
    private SQLiteDatabase db;

    // declaring name of the database
//    static final String DATABASE_NAME = "UserDB";

    static final String DATABASE_NAME = "Team44DB";

    // declaring table name of the database
//    static final String TABLE_NAME = "Users";

    static final String CITY_TABLE = "CITY_TABLE";
    static final String COL_ID = "ID";
    static final String COL_USERNAME = "USERNAME";
    static final String COL_CITY = "CITY";
    static final String COL_STATE = "STATE";
    static final String COL_LATITUDE = "LATITUDE";
    static final String COL_LONGITUDE = "LONGITUDE";

    static final String PREF_TABLE = "PREF_TABLE";
    static final String COL_THEMENAME = "THEMENAME";




    // declaring version of the database
    static final int DATABASE_VERSION = 1;

    static final String CREATE_DB_CITY_TABLE = " CREATE TABLE " + CITY_TABLE
            + " ("+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT"
            + ", "+COL_USERNAME+" TEXT NOT NULL"
            + ", "+COL_CITY+" TEXT NOT NULL"
//            + ", "+COL_STATE+" TEXT NOT NULL"
//            + ", "+COL_LATITUDE+" REAL NOT NULL"
//            + ", "+COL_LONGITUDE+" REAL NOT NULL"
            + ");";
    static final String CREATE_DB_PREF_TABLE = " CREATE TABLE " + CITY_TABLE
            + " ("+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT"
            + ", "+COL_USERNAME+" TEXT NOT NULL"
            + ", "+COL_THEMENAME+" TEXT NOT NULL"
            + ");";



    // creating a database
    private static class DatabaseHelper extends SQLiteOpenHelper {

        // defining a constructor
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // creating a table in the database
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_CITY_TABLE);
            db.execSQL(CREATE_DB_PREF_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // sql query to drop a table
            // having similar name
            db.execSQL("DROP TABLE IF EXISTS " + CITY_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PREF_TABLE);
            onCreate(db);
        }
    }

}
