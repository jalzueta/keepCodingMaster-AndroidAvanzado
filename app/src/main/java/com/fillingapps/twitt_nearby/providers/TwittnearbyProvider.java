package com.fillingapps.twitt_nearby.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.fillingapps.twitt_nearby.model.db.DBConstants;
import com.fillingapps.twitt_nearby.model.db.DBHelper;

public class TwittnearbyProvider extends ContentProvider {


    public static final String TWITTNEARBY_PROVIDER = "com.fillingapps.twitt_nearby.provider";

    // content://com.fillingapps.twitt_nearby.provider/tweets
    public static final Uri TWEETS_URI = Uri.parse("content://" + TWITTNEARBY_PROVIDER + "/tweets");

    // Create the constants used to differentiate between the different URI requests.
    private static final int ALL_TWEETS = 1;
    private static final int SINGLE_TWEET = 2;

    // UriMatcher: se encarga de comprobar de si la URI cuadra con alguna de las que se añaden abajo
    private static final UriMatcher uriMatcher;
    // Populate the UriMatcher object, where a URI ending in elements will correspond to a request for all items, and elements/[rowID] represents a single row.
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TWITTNEARBY_PROVIDER, "tweets", ALL_TWEETS);
        uriMatcher.addURI(TWITTNEARBY_PROVIDER, "tweets/#", SINGLE_TWEET);
    }

    private DBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Open the database
        SQLiteDatabase db = DBHelper.getDB(dbHelper);

        // Replace these with valid SQL statements if necessary.
        String groupBy = null;
        String having = null;

        // Use an SQLite Query Builder to simplify constructing the database query.
        // SQLiteQueryBuilder es equivalente a un NSFetchRequest de iOS
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(getTableName(uri));

        // If this is a row query, limit the result set to the passed in row.
        String rowID = null;
        switch (uriMatcher.match(uri)) {
            case SINGLE_TWEET :
                rowID = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(DBConstants.KEY_TWEET_ID + "=" + rowID);
                break;
            default: break;
        }

        // Specify the table on which to perform the query. This can // be a specific table or a join as required. queryBuilder.setTables(MySQLiteOpenHelper.DATABASE_TABLE);
        // Execute the query.
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
        // Return the result Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ALL_TWEETS:
                return "vnd.android.cursor.dir/vnd.fillingapp.tweet";
            case SINGLE_TWEET:
                return "vnd.android.cursor.item/vnd.fillingapp.tweet";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    // Recibe: content://com.fillingapps.twitt_nearby.provider/tweets
    // Devuelve: content://com.fillingapps.twitt_nearby.provider/tweets/89574
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Open a read / write database to support the transaction.
        SQLiteDatabase db = DBHelper.getDB(dbHelper);
        String tableName = getTableName(uri);

        // Insert the values into the table
        long id = db.insert(tableName, null, values);

        // Construct and return the URI of the newly inserted row.
        if (id > -1) {
            // Construct and return the URI of the newly inserted row.
            Uri insertedUri = null;
            switch (uriMatcher.match(uri)) {
                case ALL_TWEETS:
                    insertedUri = ContentUris.withAppendedId(TWEETS_URI, id);
                    break;
                case SINGLE_TWEET :
                    insertedUri = ContentUris.withAppendedId(TWEETS_URI, id);
                    break;
                default: break;
            }

            // Notify any observers of the change in the data set.
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(insertedUri, null);

            return insertedUri;
        } else {
            return null;
        }
    }

    // Recibe: content://com.fillingapps.twitt_nearby.provider/tweets/89574
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Open a read / write database to support the transaction.

        SQLiteDatabase db = DBHelper.getDB(dbHelper);
        String tableName = getTableName(uri);
        String rowID = null;

        // If this is a row URI, limit the deletion to the specified row.
        switch (uriMatcher.match(uri)) {
            case SINGLE_TWEET:
                rowID = uri.getPathSegments().get(1);
                selection = DBConstants.KEY_TWEET_ID + "=" + rowID;
                break;
            default:
                break;
        }

        // Perform the deletion.
        int deleteCount = db.delete(tableName, selection, selectionArgs);
        // Notify any observers of the change in the data set.
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the number of deleted items.
        return deleteCount;
    }

    // Recibe: content://com.fillingapps.twitt_nearby.provider/tweets/89574
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = DBHelper.getDB(dbHelper);

        String rowID = null;
        // If this is a row URI, limit the deletion to the specified row.
        switch (uriMatcher.match(uri)) {
            case SINGLE_TWEET :
                rowID = uri.getPathSegments().get(1);
                selection = DBConstants.KEY_TWEET_ID + "=" + rowID;
                break;
            default:
                break;
        }

        if (rowID == null) {
            return -1;
        }

        int updateCount = db.update(getTableName(uri), values, selection, selectionArgs);

        // Notify any observers of the change in the data set.
        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }


    private String getTableName(Uri uri) {
        String tableName = null;
        switch (uriMatcher.match(uri)) {
            case ALL_TWEETS:
                tableName = DBConstants.TABLE_TWEET;
                break;
            case SINGLE_TWEET :
                tableName = DBConstants.TABLE_TWEET;
                break;
            default: break;
        }
        return tableName;
    }
}
