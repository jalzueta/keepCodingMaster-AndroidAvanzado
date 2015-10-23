package com.fillingapps.twitt_nearby.providers;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.fillingapps.twitt_nearby.TwittnearbyApp;
import com.fillingapps.twitt_nearby.model.Tweet;
import com.fillingapps.twitt_nearby.model.dao.TweetDAO;
import com.fillingapps.twitt_nearby.model.db.DBHelper;

public class TwittnearbyProviderHelper {

    public static String getIdFromUri(Uri uri) {
        String rowID = uri.getPathSegments().get(1);
        return rowID;
    }

    public static Cursor getAllTweets() {
        // ContentResolver: es un "encontrador" de ContentProvider. No le dices qué ContentProvider,
        // el sabe a cual mandarselo en funcion de la URI que va en la query
        ContentResolver cr = TwittnearbyApp.getAppContext().getContentResolver();

        Cursor cursor = cr.query(TwittnearbyProvider.TWEETS_URI, TweetDAO.allColumns, null, null, null);

        return cursor;
    }

    public static Uri insertTweet(Tweet tweet) {
        if (tweet == null) {
            return null;
        }
        ContentResolver cr = TwittnearbyApp.getAppContext().getContentResolver();

        Uri uri = cr.insert(TwittnearbyProvider.TWEETS_URI, TweetDAO.getContentValues(tweet));
        tweet.setId(Long.parseLong(getIdFromUri(uri)));
        return uri;
    }

    public static int updateTweet(Tweet tweet) {
        if (tweet == null) {
            return (int) DBHelper.INVALID_ID;
        }
        ContentResolver cr = TwittnearbyApp.getAppContext().getContentResolver();

        String sUri = TwittnearbyProvider.TWEETS_URI.toString() + "/" + tweet.getId();
        Uri uri = Uri.parse(sUri);
        int updatedNotebooks = cr.update(uri, TweetDAO.getContentValues(tweet), null, null);
        return updatedNotebooks;
    }

    public static void deleteTweet(Tweet tweet) {
        ContentResolver cr = TwittnearbyApp.getAppContext().getContentResolver();
        String sUri = TwittnearbyProvider.TWEETS_URI.toString() + "/" + tweet.getId();
        Uri uri = Uri.parse(sUri);
        cr.delete(uri, null, null);
    }

    public static void deleteAllTweets() {
        ContentResolver cr = TwittnearbyApp.getAppContext().getContentResolver();

        String sUri = TwittnearbyProvider.TWEETS_URI.toString();
        Uri uri = Uri.parse(sUri);
        cr.delete(uri, null, null);
    }
}
