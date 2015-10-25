package com.fillingapps.twitt_nearby.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class GeneralUtils {

    public static void hideKeyboard( Context context ) {
        try {
            View view = ( (Activity) context ).getCurrentFocus();
            if ( view != null ) {
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
