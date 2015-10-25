package com.fillingapps.twitt_nearby.model;

import java.util.Date;

import twitter4j.Status;

public class TweetParser {

    public static Tweet createTweet(Status status) {
        String userName = status.getUser().getName();
        String userImageUrl = status.getUser().getMiniProfileImageURL();
        String text = status.getText();
        if (status.getGeoLocation() == null) {
            return null;
        }
        Double latitude = status.getGeoLocation().getLatitude();
        Double longitude = status.getGeoLocation().getLongitude();
        Date creationDate = new Date();
        return new Tweet(userName, userImageUrl, text, latitude, longitude, creationDate);
    }

}
