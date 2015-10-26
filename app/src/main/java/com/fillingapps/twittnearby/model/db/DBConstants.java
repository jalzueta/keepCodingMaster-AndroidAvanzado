package com.fillingapps.twittnearby.model.db;

public class DBConstants {
    public static final String DROP_DATABASE = "";

    public static final String TABLE_TWEET = "TWEET";

    // Tweets Table field constants
    public static final String KEY_TWEET_ID = "_id";
    public static final String KEY_TWEET_USER_NAME = "userName";
    public static final String KEY_TWEET_USER_IMAGE_URL = "userImageUrl";
    public static final String KEY_TWEET_TEXT = "text";
    public static final String KEY_TWEET_CREATION_DATE = "creationDate";
    public static final String KEY_TWEET_LATITUDE = "latitude";
    public static final String KEY_TWEET_LONGITUDE = "longitude";
    public static final String KEY_TWEET_HAS_COORDINATES = "hasCoordinates";
    public static final String KEY_TWEET_ADDRESS = "address";
    public static final String SQL_CREATE_TWEET_TABLE =
            "create table "
                    + TABLE_TWEET + "( " + KEY_TWEET_ID + " integer primary key autoincrement, "
                    + KEY_TWEET_USER_NAME + " text not null,"
                    + KEY_TWEET_USER_IMAGE_URL + " text,"
                    + KEY_TWEET_TEXT + " text not null,"
                    + KEY_TWEET_CREATION_DATE + " INTEGER, "
                    + KEY_TWEET_LATITUDE + " real,"
                    + KEY_TWEET_LONGITUDE + " real, "
                    + KEY_TWEET_HAS_COORDINATES + " INTEGER, "
                    + KEY_TWEET_ADDRESS + " text "
                    + ");";

    public static final String[] CREATE_DATABASE = {
            SQL_CREATE_TWEET_TABLE
    };

}
