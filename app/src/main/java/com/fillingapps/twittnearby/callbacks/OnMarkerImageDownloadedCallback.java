package com.fillingapps.twittnearby.callbacks;

import com.fillingapps.twittnearby.model.Tweet;
import com.google.android.gms.maps.model.BitmapDescriptor;

public interface OnMarkerImageDownloadedCallback {
    void onMarkerImageDownloaded(BitmapDescriptor bitmapDescriptor, Tweet tweet);
}
