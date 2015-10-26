

package com.fillingapps.twittnearby.utils.twitter;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


/**
 * Helper class to connect to Twitter using Twitter4J library
 * @author dfreniche
 *
 */
public class TwitterHelper {
	private static Twitter twitter;
	private static RequestToken requestToken;
	private static SharedPreferences sharedPreferences;

	/**
	 * 
	 * @param context inject a valid context
	 */
	public TwitterHelper(Context context) {
		sharedPreferences = context.getSharedPreferences(TwitterConsts.PREFERENCE_NAME, Context.MODE_PRIVATE);
	}


	/**
	 * Handle OAuth Callback
	 */
	public void handleOauthCallback(Uri uri) {
		if (uri != null && uri.toString().startsWith(TwitterConsts.CALLBACK_URL)) {
			String verifier = uri.getQueryParameter(TwitterConsts.IEXTRA_OAUTH_VERIFIER);
			try { 
				AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier); 
				Editor ed = sharedPreferences.edit();
				ed.putString(TwitterConsts.PREF_KEY_TOKEN, accessToken.getToken()); 
				ed.putString(TwitterConsts.PREF_KEY_SECRET, accessToken.getTokenSecret()); 
				ed.commit();
			} catch (Exception e) {
				Log.e("TwitterHelper", e.getMessage());
			}
		}
	}

	/**
	 * check if the account is authorized
	 * @return
	 */
	public boolean isConnected() {
		return sharedPreferences.getString(TwitterConsts.PREF_KEY_TOKEN, null) != null;
	}

	public void askOAuth(Context context) {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(TwitterPrivateKeyAndSecret.CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(TwitterPrivateKeyAndSecret.CONSUMER_SECRET);
		Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();

		try {
			requestToken = twitter.getOAuthRequestToken(TwitterConsts.CALLBACK_URL);
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove Token, Secret from preferences
	 */
	public void disconnectTwitter() {
		Editor editor = sharedPreferences.edit();
		editor.remove(TwitterConsts.PREF_KEY_TOKEN);
		editor.remove(TwitterConsts.PREF_KEY_SECRET);

		editor.commit();
	}

	public Twitter getTwitter() {
		twitter = (new TwitterFactory()).getInstance();
		twitter.setOAuthConsumer(TwitterPrivateKeyAndSecret.CONSUMER_KEY, TwitterPrivateKeyAndSecret.CONSUMER_SECRET);

		twitter.setOAuthAccessToken(loadAccessToken());
		
		return twitter;
	}
	
	private AccessToken loadAccessToken() {
		String oauthAccessToken = sharedPreferences.getString(TwitterConsts.PREF_KEY_TOKEN, "");
		String oAuthAccessTokenSecret = sharedPreferences.getString(TwitterConsts.PREF_KEY_SECRET, "");

		return new AccessToken(oauthAccessToken, oAuthAccessTokenSecret);
	}


	public AsyncTwitter getAsyncTwitter() {
		AsyncTwitterFactory factory = new AsyncTwitterFactory();
		AsyncTwitter asyncTwitter = factory.getInstance();
		
		asyncTwitter.setOAuthConsumer(TwitterPrivateKeyAndSecret.CONSUMER_KEY, TwitterPrivateKeyAndSecret.CONSUMER_SECRET);
		asyncTwitter.setOAuthAccessToken(loadAccessToken());
		
		return asyncTwitter;
	}

    /**
     * Public constants used
     */

    public static class TwitterConsts {
        static String PREFERENCE_NAME = "twitter_oauth";
        static final String PREF_KEY_SECRET = "oauth_token_secret";
        static final String PREF_KEY_TOKEN = "oauth_token";

        public static final String CALLBACK_URL = "oauth://t4jsample";

        static final String IEXTRA_AUTH_URL = "auth_url";
        static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
        static final String IEXTRA_OAUTH_TOKEN = "oauth_token";
    }
}
