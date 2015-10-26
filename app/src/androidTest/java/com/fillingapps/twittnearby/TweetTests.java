package com.fillingapps.twittnearby;

import android.test.AndroidTestCase;

import com.fillingapps.twittnearby.model.Tweet;

import java.util.Date;

public class TweetTests extends AndroidTestCase {

    String userName = "userName";
    String userImageUrl = "userImageUrl";
    String text = "texto del tweet, menos de 140 caracteres";
    Double latitud = 42.6745;
    Double longitude = -1.61534;
    Date creationDate = new Date();

    public void testCanCreateANote() {
        Tweet tweet = new Tweet(userName, userImageUrl, text, latitud, longitude, creationDate);

        assertNotNull(tweet);
    }
}
