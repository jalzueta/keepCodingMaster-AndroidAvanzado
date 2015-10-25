package com.fillingapps.twitt_nearby;

import android.database.Cursor;

import com.fillingapps.twitt_nearby.model.Tweet;
import com.fillingapps.twitt_nearby.model.dao.TweetDAO;
import com.fillingapps.twitt_nearby.model.db.DBHelper;

import java.util.Date;

public class TweetDAOTests extends ApplicationTest {

    String userName = "userName";
    String text = "texto del tweet, menos de 140 caracteres";
    Double latitud = 42.6745;
    Double longitude = -1.61534;
    Date creationDate = new Date();

    public void testInsertNullTweetReturnsInvalidId() {
        Tweet tweet = null;

        TweetDAO tweetDAO = new TweetDAO(getContext());
        long id = tweetDAO.insert(tweet);

        assertEquals(id, DBHelper.INVALID_ID);
    }

    public void testInsertNotebookReturnsValidId(){
        Tweet tweet = new Tweet(userName, userImageUrl, text, latitud, longitude, creationDate);

        TweetDAO tweetDAO = new TweetDAO(getContext());
        long id = tweetDAO.insert(tweet);

        assertTrue(id > 0);
    }

    public void testQueryAllTweets() {
        insertTweetStubs(10);

        TweetDAO tweetDAO = new TweetDAO(getContext());
        final Cursor cursor = tweetDAO.queryCursor();
        final int notebookCount = cursor.getCount();

        assertTrue(notebookCount > 9);
    }

    private void insertTweetStubs(final int notebooksToInsert) {

        TweetDAO tweetDAO = new TweetDAO(getContext());

        for (int i = 0; i < notebooksToInsert; i++){
            final Tweet tweet = new Tweet(userName, userImageUrl, text, latitud, longitude, creationDate);
            final long id = tweetDAO.insert(tweet);
        }
    }

    public void testDeleteAllTweets() {
        insertTweetStubs(10);

        final TweetDAO tweetDAO = new TweetDAO(getContext());
        tweetDAO.deleteAll();

        final Cursor cursor = tweetDAO.queryCursor();
        final int notebookCount = cursor.getCount();

        assertEquals(0, notebookCount);
    }



}
