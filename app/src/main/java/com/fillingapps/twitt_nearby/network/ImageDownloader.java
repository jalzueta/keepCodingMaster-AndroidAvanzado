package com.fillingapps.twitt_nearby.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class ImageDownloader extends AsyncTask<ImageDownloader.ImageDownloaderParams, Integer, BitmapDescriptor>{

    private  final WeakReference<MarkerOptions> mMarkerWeakReference;
    private int mDefaultImageResId;
    private Context mContext;

    public ImageDownloader(Context context, MarkerOptions marker, int defaultImageResId) {
        mContext = context;
        mMarkerWeakReference = new WeakReference<>(marker);
        mDefaultImageResId = defaultImageResId;

        if (mMarkerWeakReference != null && mMarkerWeakReference.get() != null){
            mMarkerWeakReference.get().icon(BitmapDescriptorFactory.fromResource(mDefaultImageResId));
        }
    }


    @Override
    protected BitmapDescriptor doInBackground(ImageDownloaderParams... params) {

        File imageFile = new File(mContext.getCacheDir(), params[0].getCachedImageName());
        if (imageFile.exists()) {
            // La imagen existe
            return BitmapDescriptorFactory.fromFile(imageFile.getAbsolutePath());
        }

        // La imagen no existe, la descargamos
        InputStream inputStream = null;
        try {
            inputStream = new java.net.URL(params[0].getImageUrl()).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Guardamos la imagen en caché
            FileOutputStream fileOutputStrea = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStrea);

            return BitmapDescriptorFactory.fromBitmap(bitmap);
        } catch (Exception ex) {

            Log.e(ImageDownloader.class.getSimpleName(), "Error downloading image", ex);
            // Devolmenos la imagen por defecto
            return BitmapDescriptorFactory.fromResource(mDefaultImageResId);

        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception ex) {
                Log.e(ImageDownloader.class.getSimpleName(), "Error finalizing image download", ex);
            }
        }
    }

    @Override
    protected void onPostExecute(BitmapDescriptor bitmapDescriptor) {
        if (mMarkerWeakReference != null && mMarkerWeakReference.get() != null){
            mMarkerWeakReference.get().icon(bitmapDescriptor);
        }
    }

    public static class ImageDownloaderParams {

        public String getImageUrl() {
            return mImageUrl;
        }

        public String getCachedImageName() {
            return mCachedImageName;
        }

        String mImageUrl;
        String mCachedImageName;

        public ImageDownloaderParams(String imageUrl, String cachedImageName) {
            this.mImageUrl = imageUrl;
            this.mCachedImageName = cachedImageName;
        }
    }
}
