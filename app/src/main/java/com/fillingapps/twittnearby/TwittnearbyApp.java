package com.fillingapps.twittnearby;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;

public class TwittnearbyApp extends Application {

    // Lo guardaos como referencia débil para evitar ciclos "strong"
    private static WeakReference<Context> context;

    public static Context getAppContext() {
        return context.get();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // El contexto de una aplicacion es unico. Lo vamos a meter en una variable de la clase Application
        // para que podamos acceder a el en las clases que no lo tengas a mano (p.e. los Fragments)
        // "final" porque no va a variar (como los let de swift)
        final Context c = getApplicationContext();
        context = new WeakReference<Context>(c);
        Log.d(TwittnearbyApp.class.getCanonicalName(), getString(R.string.log_twittnearby_starting));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        // Envío una notificación a las clases que puedan estar activas
        Log.d(TwittnearbyApp.class.getCanonicalName(), getString(R.string.log_twittnearby_low_memory));
    }

}
