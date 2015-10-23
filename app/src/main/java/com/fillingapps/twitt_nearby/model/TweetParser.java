package com.fillingapps.twitt_nearby.model;

import java.util.Date;

import twitter4j.Status;

public class TweetParser {

    public static Tweet createTweet(Status status) {
        String userName = status.getUser().getName();
        String text = status.getText();
        Double latitude = Double.MIN_VALUE;
        Double longitude = Double.MIN_VALUE;
        if (status.getGeoLocation() != null) {
            latitude = status.getGeoLocation().getLatitude();
            longitude = status.getGeoLocation().getLongitude();
        }
        Date creationDate = new Date();
        Tweet tweet = new Tweet(userName, text, latitude, longitude, creationDate);

        return tweet;
    }

}
