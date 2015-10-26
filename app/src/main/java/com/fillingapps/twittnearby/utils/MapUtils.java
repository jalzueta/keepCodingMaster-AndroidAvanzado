package com.fillingapps.twittnearby.utils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapUtils {

    public static void centerMap(GoogleMap map, double latitude, double longitude, int zoomLevel) {

        LatLng coordinate = new LatLng(latitude, longitude);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(coordinate).zoom(zoomLevel)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
