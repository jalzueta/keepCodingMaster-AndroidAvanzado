package com.fillingapps.twitt_nearby.utils;

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

    public static void addMarker(GoogleMap map, double latitude, double longitude, String title, String snippet, BitmapDescriptor icon) {
        LatLng coordinate = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(coordinate).title(title).snippet(snippet);
        // Color del icono por defecto
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

        // Icono personalizado
        //BitmapDescriptorFactory.fromResource(R.drawable.note)
        //marker.icon(icon);
        map.addMarker(marker);
    }

}
