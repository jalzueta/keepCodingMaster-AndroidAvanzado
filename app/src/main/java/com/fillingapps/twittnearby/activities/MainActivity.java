package com.fillingapps.twittnearby.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fillingapps.twittnearby.R;
import com.fillingapps.twittnearby.callbacks.OnInfoDialogCallback;
import com.fillingapps.twittnearby.callbacks.OnMarkerImageDownloadedCallback;
import com.fillingapps.twittnearby.fragments.dialogs.InfoDialogFragment;
import com.fillingapps.twittnearby.model.Tweet;
import com.fillingapps.twittnearby.model.TweetParser;
import com.fillingapps.twittnearby.model.dao.TweetDAO;
import com.fillingapps.twittnearby.model.db.DBHelper;
import com.fillingapps.twittnearby.network.ImageDownloader;
import com.fillingapps.twittnearby.providers.TwittnearbyProvider;
import com.fillingapps.twittnearby.providers.TwittnearbyProviderHelper;
import com.fillingapps.twittnearby.utils.GeneralUtils;
import com.fillingapps.twittnearby.utils.GeolocationUtils;
import com.fillingapps.twittnearby.utils.NetworkHelper;
import com.fillingapps.twittnearby.utils.MapUtils;
import com.fillingapps.twittnearby.utils.SharedPreferencesUtils;
import com.fillingapps.twittnearby.utils.twitter.ConnectTwitterTask;
import com.fillingapps.twittnearby.utils.twitter.TwitterHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import twitter4j.AccountSettings;
import twitter4j.AsyncTwitter;
import twitter4j.Category;
import twitter4j.DirectMessage;
import twitter4j.Friendship;
import twitter4j.GeoLocation;
import twitter4j.IDs;
import twitter4j.OEmbed;
import twitter4j.PagableResponseList;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.SavedSearch;
import twitter4j.Status;
import twitter4j.Trends;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.api.HelpResources;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuth2Token;
import twitter4j.auth.RequestToken;

public class MainActivity extends AppCompatActivity implements OnInfoDialogCallback, TwitterListener, ConnectTwitterTask.OnConnectTwitterListener, LoaderManager.LoaderCallbacks<Cursor>, OnMarkerImageDownloadedCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = MainActivity.class.getName();
    private static final int ZOOM = 12;
    private static final int RADIUS = 20;
    private static final int NUMBER_OF_TWEETS = 50;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private ConnectTwitterTask twitterTask;
    private AsyncTwitter twitter;

    @Bind(R.id.my_position_button)
    FloatingActionButton mFloatingActionButton;

    private MapFragment mMapFragment;
    private GoogleMap map;
    private android.location.Location mLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Cursor tweetsCursor;

    //region Group: Ciclo de vida
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Handle intents from SearchBar
        handleIntent(getIntent());

        configureMap();
        configureFloatingActionButton();
        configureGoogleApiClient();
        configureLocationRequest();

        if (NetworkHelper.isNetworkConnectionOK(new WeakReference<>(getApplication()))) {
            twitterTask = new ConnectTwitterTask(this);
            twitterTask.setListener(this);
            twitterTask.execute();
        } else {
            showInfoDialogFragment(R.string.no_connection_dialog_title, R.string.no_connection_dialog_message, R.string.no_connection_button_text);
        }

        LoaderManager loader = getLoaderManager();
        loader.initLoader(0, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String textToSearch = intent.getStringExtra(SearchManager.QUERY);
            // Se lanza la request a Twitter usando el texto recibido desde la Search Bar
            showResults(textToSearch);
        } else{
            final Uri uri = intent.getData();
            if (uri != null && uri.toString().indexOf(TwitterHelper.TwitterConsts.CALLBACK_URL) != -1) {
                Log.d(TAG, "Retrieving Access Token. Callback received : " + uri);
                twitterTask = new ConnectTwitterTask(this, uri);
                twitterTask.setListener(this);
                twitterTask.execute();
            }
        }
    }
    //endregion

    //region Group: Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = null;
        if (searchMenuItem != null) {
            searchView = (SearchView) searchMenuItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            onSearchRequested();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region Group: Initial configurations
    private void configureMap() {
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (mMapFragment != null) {
            map = mMapFragment.getMap();
            map.setMyLocationEnabled(true);
        }
        if (map == null) {
            Toast.makeText(this, "Map died", Toast.LENGTH_LONG).show();
        }
    }

    private void configureFloatingActionButton() {
        if (mFloatingActionButton != null) {
            mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mLocation != null) {
                        requestTweetsInLocation(mLocation, RADIUS, NUMBER_OF_TWEETS);
                    } else{
                        hideFloatingActionButton();
                        showInfoDialogFragment(R.string.location_request_dialog_title, R.string.location_request_dialog_message, R.string.location_request_button_text);
                    }
                }
            });
            hideFloatingActionButton();
        }
    }

    private void configureGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void configureLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    //endregion

    //region Group: Floating Action Button
    private void showFloatingActionButton() {
        Handler showAddButton = new Handler();
        showAddButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mFloatingActionButton != null) {
                    mFloatingActionButton.show();
                }
            }
        }, 2000);
    }

    private void hideFloatingActionButton() {
        mFloatingActionButton.setVisibility(View.INVISIBLE);
    }
    //endregion

    //region Group: OnConnectTwitterListener
    @Override
    public void twitterConnectionFinished() {
        Toast.makeText(MainActivity.this, getString(R.string.twitter_auth_ok), Toast.LENGTH_SHORT).show();
        launchTwitter();
    }

    private void launchTwitter() {
        twitter = new TwitterHelper(this).getAsyncTwitter();
        twitter.addListener(this);
    }
    //endregion

    //region Group: Twitter Requests

    // Se lanza la request a Twitter usando el texto recibido desde la Search Bar
    private void showResults(String textToSearch) {
        if (twitter != null) {
            GeneralUtils.hideKeyboard(this);
            requestTweetsInAddress(textToSearch, RADIUS, NUMBER_OF_TWEETS);
        } else {
            Toast.makeText(this, "Twitter session not started", Toast.LENGTH_LONG).show();
        }
    }

    private void requestTweetsInHomeTimeline() {
        twitter.getHomeTimeline();
    }

    private void requestTweetsInUserTimeline() {
        twitter.getUserTimeline();
    }

    private void requestTweetsWithText(String textToSearch) {

        Query query = new Query(textToSearch);
        query.count(10); //You can also set the number of tweets to return per page, up to a max of 100
        twitter.search(query);
    }

    private void requestTweetsInAddress(String addressToSearch, int radiusInKm, int maxNumberOfTweets) {

        LatLng position = GeolocationUtils.getLocationFromAddress(this, addressToSearch);
        double latitude = position.latitude;
        double longitude = position.longitude;

        MapUtils.centerMap(map, latitude, longitude, ZOOM);
        launchTweetSearch(radiusInKm, maxNumberOfTweets, latitude, longitude);
    }

    private void requestTweetsInLocation(android.location.Location location, int radiusInKm, int maxNumberOfTweets) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        launchTweetSearch(radiusInKm, maxNumberOfTweets, latitude, longitude);
    }

    private void launchTweetSearch(int radiusInKm, int maxNumberOfTweets, double latitude, double longitude) {
        SharedPreferencesUtils.savePrefCenterLocation(this, latitude, longitude);
        Query query = new Query();
        query.setGeoCode(new GeoLocation(latitude, longitude), radiusInKm, Query.KILOMETERS);
        query.count(maxNumberOfTweets);
        twitter.search(query);
    }
    //endregion

    private void saveTweets(List<Status> statuses) {
        Log.d(TAG, "Number of tweets received: " + statuses.size());
        TwittnearbyProviderHelper.deleteAllTweets();
        for (Status s : statuses) {
            Tweet tweet = TweetParser.createTweet(s);
            TwittnearbyProviderHelper.insertTweet(tweet);
        }
    }

    private void showTweetsInMap(Cursor tweetsCursor) {
        //TODO: show tweets in map
        double centerLatitude = SharedPreferencesUtils.getPrefCenterLatitude(this);
        double centerLongitude = SharedPreferencesUtils.getPrefCenterLongitude(this);
        MapUtils.centerMap(map, centerLatitude, centerLongitude, ZOOM);
        while (tweetsCursor.moveToNext()) {
            String userName = tweetsCursor.getString(tweetsCursor.getColumnIndex("userName"));
            String userImageUrl = tweetsCursor.getString(tweetsCursor.getColumnIndex("userImageUrl"));
            String text = tweetsCursor.getString(tweetsCursor.getColumnIndex("text"));
            double latitude = tweetsCursor.getDouble(tweetsCursor.getColumnIndex("latitude"));
            double longitude = tweetsCursor.getDouble(tweetsCursor.getColumnIndex("longitude"));
            Date creationDate = DBHelper.convertLongToDate(tweetsCursor.getLong(tweetsCursor.getColumnIndex("creationDate")));
            if (latitude != Double.MIN_VALUE && longitude != Double.MIN_VALUE) {
                Tweet tweet = new Tweet(userName, userImageUrl, text, latitude, longitude, creationDate);
                addMarker(this, tweet);
            }
        }
    }

    public void addMarker(Context context, Tweet tweet) {
        // Icono por defecto
        ImageDownloader.ImageDownloaderParams params = new ImageDownloader.ImageDownloaderParams(tweet.getUserImageUrl(), tweet.getUserImageUrl());
        ImageDownloader imageDownloader = new ImageDownloader(context, R.drawable.default_user_icon, tweet, this);
        imageDownloader.execute(params);
    }

    @Override
    public void onMarkerImageDownloaded(BitmapDescriptor bitmapDescriptor, Tweet tweet) {
        LatLng position = new LatLng(tweet.getLatitude(), tweet.getLongitude());
        MarkerOptions marker = new MarkerOptions().position(position).title(tweet.getUserName()).snippet(tweet.getText());
        marker.icon(bitmapDescriptor);
        map.addMarker(marker);
    }

    //region Group: LoaderCallbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Creamos el cursor loader
        CursorLoader loader = new CursorLoader(this, TwittnearbyProvider.TWEETS_URI, TweetDAO.allColumns, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            tweetsCursor = data;
            showTweetsInMap(data);
            Log.d(TAG, "Number of tweets in DB: " + tweetsCursor.getCount());
        }
    }
    //endregion

    //region Group: InfoDialogs
    protected void showInfoDialogFragment(int titleId, int messageId, int buttonTextId) {
        InfoDialogFragment dialog = InfoDialogFragment.newInstance(titleId, messageId, buttonTextId);
        dialog.setOnInfoDialogCallback(this);
        dialog.show(getFragmentManager(), null);
    }

    @Override
    public void onInfoDialogClosed(InfoDialogFragment dialog) {
        dialog.dismiss();
    }
    //endregion

    //region Group: TwitterListener
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public void gotMentions(ResponseList<Status> statuses) {

    }

    @Override
    public void gotHomeTimeline(ResponseList<Status> statuses) {
        Log.d(TAG, "----------------------- (Home: " + statuses.size() + " results) -----------------------");
        saveTweets(statuses);
    }

    @Override
    public void gotUserTimeline(ResponseList<Status> statuses) {
        Log.d(TAG, "----------------------- (User: " + statuses.size() + " results) -----------------------");
        saveTweets(statuses);
    }

    @Override
    public void gotRetweetsOfMe(ResponseList<Status> statuses) {

    }

    @Override
    public void gotRetweets(ResponseList<Status> retweets) {

    }

    @Override
    public void gotShowStatus(Status status) {

    }

    @Override
    public void destroyedStatus(Status destroyedStatus) {

    }

    @Override
    public void updatedStatus(Status status) {

    }

    @Override
    public void retweetedStatus(Status retweetedStatus) {

    }

    @Override
    public void gotOEmbed(OEmbed oembed) {

    }

    @Override
    public void lookedup(ResponseList<Status> statuses) {

    }

    @Override
    public void searched(QueryResult queryResult) {
        Log.d(TAG, "----------------------- (Search: " + queryResult.getTweets().size() + " results) -----------------------");
        List<Status> statuses = queryResult.getTweets();
        saveTweets(statuses);
    }

    @Override
    public void gotDirectMessages(ResponseList<DirectMessage> messages) {

    }

    @Override
    public void gotSentDirectMessages(ResponseList<DirectMessage> messages) {

    }

    @Override
    public void gotDirectMessage(DirectMessage message) {

    }

    @Override
    public void destroyedDirectMessage(DirectMessage message) {

    }

    @Override
    public void sentDirectMessage(DirectMessage message) {

    }

    @Override
    public void gotFriendsIDs(IDs ids) {

    }

    @Override
    public void gotFollowersIDs(IDs ids) {

    }

    @Override
    public void lookedUpFriendships(ResponseList<Friendship> friendships) {

    }

    @Override
    public void gotIncomingFriendships(IDs ids) {

    }

    @Override
    public void gotOutgoingFriendships(IDs ids) {

    }

    @Override
    public void createdFriendship(User user) {

    }

    @Override
    public void destroyedFriendship(User user) {

    }

    @Override
    public void updatedFriendship(Relationship relationship) {

    }

    @Override
    public void gotShowFriendship(Relationship relationship) {

    }

    @Override
    public void gotFriendsList(PagableResponseList<User> users) {

    }

    @Override
    public void gotFollowersList(PagableResponseList<User> users) {

    }

    @Override
    public void gotAccountSettings(AccountSettings settings) {

    }

    @Override
    public void verifiedCredentials(User user) {

    }

    @Override
    public void updatedAccountSettings(AccountSettings settings) {

    }

    @Override
    public void updatedProfile(User user) {

    }

    @Override
    public void updatedProfileBackgroundImage(User user) {

    }

    @Override
    public void updatedProfileColors(User user) {

    }

    @Override
    public void updatedProfileImage(User user) {

    }

    @Override
    public void gotBlocksList(ResponseList<User> blockingUsers) {

    }

    @Override
    public void gotBlockIDs(IDs blockingUsersIDs) {

    }

    @Override
    public void createdBlock(User user) {

    }

    @Override
    public void destroyedBlock(User user) {

    }

    @Override
    public void lookedupUsers(ResponseList<User> users) {

    }

    @Override
    public void gotUserDetail(User user) {

    }

    @Override
    public void searchedUser(ResponseList<User> userList) {

    }

    @Override
    public void gotContributees(ResponseList<User> users) {

    }

    @Override
    public void gotContributors(ResponseList<User> users) {

    }

    @Override
    public void removedProfileBanner() {

    }

    @Override
    public void updatedProfileBanner() {

    }

    @Override
    public void gotMutesList(ResponseList<User> blockingUsers) {

    }

    @Override
    public void gotMuteIDs(IDs blockingUsersIDs) {

    }

    @Override
    public void createdMute(User user) {

    }

    @Override
    public void destroyedMute(User user) {

    }

    @Override
    public void gotUserSuggestions(ResponseList<User> users) {

    }

    @Override
    public void gotSuggestedUserCategories(ResponseList<Category> category) {

    }

    @Override
    public void gotMemberSuggestions(ResponseList<User> users) {

    }

    @Override
    public void gotFavorites(ResponseList<Status> statuses) {

    }

    @Override
    public void createdFavorite(Status status) {

    }

    @Override
    public void destroyedFavorite(Status status) {

    }

    @Override
    public void gotUserLists(ResponseList<UserList> userLists) {

    }

    @Override
    public void gotUserListStatuses(ResponseList<Status> statuses) {

    }

    @Override
    public void destroyedUserListMember(UserList userList) {

    }

    @Override
    public void gotUserListMemberships(PagableResponseList<UserList> userLists) {

    }

    @Override
    public void gotUserListSubscribers(PagableResponseList<User> users) {

    }

    @Override
    public void subscribedUserList(UserList userList) {

    }

    @Override
    public void checkedUserListSubscription(User user) {

    }

    @Override
    public void unsubscribedUserList(UserList userList) {

    }

    @Override
    public void createdUserListMembers(UserList userList) {

    }

    @Override
    public void checkedUserListMembership(User users) {

    }

    @Override
    public void createdUserListMember(UserList userList) {

    }

    @Override
    public void destroyedUserList(UserList userList) {

    }

    @Override
    public void updatedUserList(UserList userList) {

    }

    @Override
    public void createdUserList(UserList userList) {

    }

    @Override
    public void gotShowUserList(UserList userList) {

    }

    @Override
    public void gotUserListSubscriptions(PagableResponseList<UserList> userLists) {

    }

    @Override
    public void gotUserListMembers(PagableResponseList<User> users) {

    }

    @Override
    public void gotSavedSearches(ResponseList<SavedSearch> savedSearches) {

    }

    @Override
    public void gotSavedSearch(SavedSearch savedSearch) {

    }

    @Override
    public void createdSavedSearch(SavedSearch savedSearch) {

    }

    @Override
    public void destroyedSavedSearch(SavedSearch savedSearch) {

    }

    @Override
    public void gotGeoDetails(Place place) {

    }

    @Override
    public void gotReverseGeoCode(ResponseList<Place> places) {

    }

    @Override
    public void searchedPlaces(ResponseList<Place> places) {

    }

    @Override
    public void gotSimilarPlaces(ResponseList<Place> places) {

    }

    @Override
    public void gotPlaceTrends(Trends trends) {

    }

    @Override
    public void gotAvailableTrends(ResponseList<twitter4j.Location> locations) {

    }

    @Override
    public void gotClosestTrends(ResponseList<twitter4j.Location> locations) {

    }

    @Override
    public void reportedSpam(User reportedSpammer) {

    }

    @Override
    public void gotOAuthRequestToken(RequestToken token) {

    }

    @Override
    public void gotOAuthAccessToken(AccessToken token) {

    }

    @Override
    public void gotOAuth2Token(OAuth2Token token) {

    }

    @Override
    public void gotAPIConfiguration(TwitterAPIConfiguration conf) {

    }

    @Override
    public void gotLanguages(ResponseList<HelpResources.Language> languages) {

    }

    @Override
    public void gotPrivacyPolicy(String privacyPolicy) {

    }

    @Override
    public void gotTermsOfService(String tof) {

    }

    @Override
    public void gotRateLimitStatus(Map<String, RateLimitStatus> rateLimitStatus) {

    }

    @Override
    public void onException(TwitterException te, TwitterMethod method) {

    }
    //endregion

    //region Group: GoogleApiClient Connection Callbacks
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    private void handleNewLocation(android.location.Location location) {
        Log.d(TAG, location.toString());
        mLocation = location;
        showFloatingActionButton();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }
    //endregion

    //region Group: LocationListener
    @SuppressLint("NewApi")
    @Override
    public void onLocationChanged(android.location.Location location) {
        handleNewLocation(location);
    }

    //endregion
}