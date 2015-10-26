package com.fillingapps.twittnearby.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fillingapps.twittnearby.callbacks.OnMarkerImageDownloadedCallback;
import com.fillingapps.twittnearby.model.Tweet;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class ImageDownloader extends AsyncTask<ImageDownloader.ImageDownloaderParams, Integer, BitmapDescriptor>{

    private WeakReference<OnMarkerImageDownloadedCallback> mOnMarkerImageDownloadedCallbackWeakReference;
    private Tweet mTweet;
    private int mDefaultImageResId;
    private Context mContext;

    public ImageDownloader(Context context, int defaultImageResId, Tweet tweet, OnMarkerImageDownloadedCallback onMarkerImageDownloadedCallback) {
        mContext = context;
        mDefaultImageResId = defaultImageResId;
        mTweet = tweet;
        mOnMarkerImageDownloadedCallbackWeakReference = new WeakReference<>(onMarkerImageDownloadedCallback);
    }

    @Override
    protected BitmapDescriptor doInBackground(ImageDownloaderParams... params) {

        // La imagen no existe, la descargamos
        InputStream inputStream = null;
        try {
            inputStream = new java.net.URL(params[0].getImageUrl()).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            return BitmapDescriptorFactory.fromBitmap(bitmap);
        } catch (Exception ex) {

            Log.e(ImageDownloader.class.getSimpleName(), "Error downloading image", ex);
            // Devolmenos la imagen por defecto
            return BitmapDescriptorFactory.fromResource(mDefaultImageResId);

        }finally {
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
        if (mOnMarkerImageDownloadedCallbackWeakReference != null && mOnMarkerImageDownloadedCallbackWeakReference.get() != null) {
            mOnMarkerImageDownloadedCallbackWeakReference.get().onMarkerImageDownloaded(bitmapDescriptor, mTweet);
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
