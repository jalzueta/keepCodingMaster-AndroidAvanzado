
package com.fillingapps.twittnearby.utils.twitter;

import android.content.Context;

public class Globals {
	private static TwitterHelper twitterHelper;
	public static final Integer MODE_CONNECT_FIRST_TIME = 1;
	public static final Integer MODE_RECONNECT = 2;

	public static TwitterHelper getSharedTwitterHelper(Context ctx) {
		if (twitterHelper == null) {
			twitterHelper = new TwitterHelper(ctx);
		}
		return twitterHelper;
	}
}
