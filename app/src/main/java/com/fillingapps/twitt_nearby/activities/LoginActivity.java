package com.fillingapps.twitt_nearby.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fillingapps.twitt_nearby.R;
import com.fillingapps.twitt_nearby.callbacks.OnInfoDialogCallback;
import com.fillingapps.twitt_nearby.fragments.dialogs.InfoDialogFragment;
import com.fillingapps.twitt_nearby.utils.ConnectionDetector;

import twitter4j.Twitter;

public class LoginActivity extends AppCompatActivity implements OnInfoDialogCallback{

    // Constants
    /**
     * Register your here app https://dev.twitter.com/apps/new and get your
     * consumer key and secret
     * */
    static String TWITTER_CONSUMER_KEY = "BNl6zaw6TaLv9EoHj1V0LvtW8";
    static String TWITTER_CONSUMER_SECRET = "pWWPWTor6MVhHvNGdy5wuZPNE1W7YwTbG1eiy6xMyOWZzRBsoI";

    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    // Internet Connection detector
    private ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            showInfoDialogFragment(R.string.no_connection_dialog_title, R.string.no_connection_dialog_message, R.string.no_connection_button_text);
            return;
        }
    }

    protected void showInfoDialogFragment(int titleId, int messageId, int buttonTextId) {
        InfoDialogFragment dialog = InfoDialogFragment.newInstance(titleId, messageId, buttonTextId);
        dialog.setOnInfoDialogCallback(this);
        dialog.show(getFragmentManager(), null);
    }

    @Override
    public void onInfoDialogClosed(InfoDialogFragment dialog) {
        dialog.dismiss();
    }
}
