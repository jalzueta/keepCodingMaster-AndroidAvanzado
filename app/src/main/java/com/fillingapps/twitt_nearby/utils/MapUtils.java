package com.fillingapps.twitt_nearby.utils;

import android.content.Context;

import com.fillingapps.twitt_nearby.R;
import com.fillingapps.twitt_nearby.network.ImageDownloader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapUtils {

    public static void centerMap(GoogleMap map, double latitude, double longitude, int zoomLevel) {

        LatLng coordinate = new LatLng(latitude, longitude);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(coordinate).zoom(zoomLevel)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public static void addMarker(Context context, GoogleMap map, double latitude, double longitude, String title, String snippet, String userImageUrl) {
        LatLng coordinate = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(coordinate).title(title).snippet(snippet);
        // Icono por defecto
        //marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.default_user_icon));
        ImageDownloader imageDownloader = new ImageDownloader(context, marker, R.drawable.default_user_icon);
        map.addMarker(marker);
    }

}
