package com.fillingapps.twitt_nearby.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fillingapps.twitt_nearby.model.Tweet;
import com.fillingapps.twitt_nearby.model.db.DBHelper;

import java.lang.ref.WeakReference;
import java.util.Date;

import static com.fillingapps.twitt_nearby.model.db.DBConstants.*;

public class TweetDAO implements DAOPersistable<Tweet> {

    private final WeakReference<Context> context;
    public static final String[] allColumns = {
            KEY_TWEET_ID,
            KEY_TWEET_USERNAME,
            KEY_TWEET_TEXT,
            KEY_TWEET_CREATION_DATE,
            KEY_TWEET_LATITUDE,
            KEY_TWEET_LONGITUDE,
            KEY_TWEET_HAS_COORDINATES,
            KEY_TWEET_ADDRESS,
    };

    public TweetDAO(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    public long insert(@NonNull Tweet data) {
        if (data == null){
            return DBHelper.INVALID_ID;
        }
        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDB(dbHelper);

        db.beginTransaction();
        long id = DBHelper.INVALID_ID;

        try {
            id = db.insert(TABLE_TWEET, null, getContentValues(data));
            // data.setId(id);

            // Hacemos el commit: setTransactionSuccessful()
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }

        dbHelper.close();

        return id;
    }

    public static ContentValues getContentValues(Tweet data) {

        if (data.getCreationDate() == null) {
            data.setCreationDate(new Date());
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TWEET_USERNAME, data.getUserName());
        contentValues.put(KEY_TWEET_TEXT, data.getText());
        contentValues.put(KEY_TWEET_CREATION_DATE, DBHelper.convertDateToLong(data.getCreationDate()));
        contentValues.put(KEY_TWEET_LATITUDE, String.format("%f", data.getLatitude()));
        contentValues.put(KEY_TWEET_LONGITUDE, String.format("%f", data.getLongitude()));

        Boolean hasCoordinates = data.isHasCoordinates();
        contentValues.put(KEY_TWEET_HAS_COORDINATES, String.format("%d", DBHelper.convertBooleanToInt(hasCoordinates)));
        contentValues.put(KEY_TWEET_ADDRESS, data.getAddress());

        return contentValues;
    }

    @Override
    public void update(long id, @NonNull Tweet data) {
        if (data == null){
            return;
        }
        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDB(dbHelper);

        db.beginTransaction();

        try {
            // Forma 1: mas intuitiva
//            db.update(TABLE_NOTE, getContentValues(data), KEY_NOTE_ID + "=" + id, null);
            // Forma 2: evitas la insercion de codigo SQL malicioso
            db.update(TABLE_TWEET, getContentValues(data), KEY_TWEET_ID + "=?", new String[]{ "" + id });

            // Hacemos el commit: setTransactionSuccessful()
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }

        dbHelper.close();
    }

    @Override
    public void delete(long id) {
        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDB(dbHelper);

        if (id == DBHelper.INVALID_ID){
            // Borro todos los registros
            db.delete(TABLE_TWEET, null, null);
        }
        else{
            db.delete(TABLE_TWEET, KEY_TWEET_ID + "=?", new String[]{ "" + id });
        }

        db.close();
    }

    @Override
    public void delete(@NonNull Tweet data) {
        if (data != null){
            delete(data.getId());
        }
    }

    @Override
    public void deleteAll() {
        delete(DBHelper.INVALID_ID);
    }

    @Nullable
    @Override
    public Cursor queryCursor() {
        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDB(dbHelper);

        // Ordenados por fecha de insercion (a traves del KEY_NOTEBOOK_ID)
        Cursor cursor = db.query(TABLE_TWEET, allColumns, null, null, null, null, KEY_TWEET_ID);
        return cursor;
    }

    @Override
    public Tweet query(long id) {
        Tweet tweet = null;

        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDB(dbHelper);

        final String whereClause = KEY_TWEET_ID + "=" + id;
        Cursor cursor = db.query(TABLE_TWEET, allColumns, whereClause, null, null, null, KEY_TWEET_ID);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                // Nos movemos al primer registro del cursor (inicialmente está en una posicion "beforeFirst")
                cursor.moveToFirst();

                tweet = new Tweet(cursor.getString(cursor.getColumnIndex(KEY_TWEET_USERNAME)),
                        userImageUrl, cursor.getString(cursor.getColumnIndex(KEY_TWEET_TEXT)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_TWEET_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_TWEET_LONGITUDE)),
                        DBHelper.convertLongToDate(cursor.getLong(cursor.getColumnIndex(KEY_TWEET_CREATION_DATE))));
                tweet.setId(cursor.getLong(cursor.getColumnIndex(KEY_TWEET_ID)));

                String address = cursor.getString(cursor.getColumnIndex(KEY_TWEET_ADDRESS));
                tweet.setAddress(address);
            }
        }

        cursor.close();
        db.close();

        return tweet;
    }
}
