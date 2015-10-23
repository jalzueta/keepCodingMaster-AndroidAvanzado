package com.fillingapps.twitt_nearby;

import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import android.util.Log;

import com.fillingapps.twitt_nearby.providers.TwittnearbyProvider;

public class TwittnearbyProviderTests extends ProviderTestCase2<TwittnearbyProvider> {

    private static final String TAG = TwittnearbyProviderTests.class.getName();

    private static MockContentResolver resolve; // in the test case scenario, we use the MockContentResolver to make queries


    /**
     * Constructor.
     *
     * @param providerClass     The class name of the provider under test
     * @param providerAuthority The provider's authority string
     */
    public TwittnearbyProviderTests(Class<TwittnearbyProvider> providerClass, String providerAuthority) {
        super(providerClass, providerAuthority);
    }

    public TwittnearbyProviderTests() {
        super(TwittnearbyProvider.class, "com.fillingapps.twitt_nearby");
    }

    @Override
    public void setUp() {
        try {
            Log.i(TAG, "Entered Setup");
            super.setUp();
            resolve = this.getMockContentResolver();
        }
        catch(Exception e) {


        }
    }

    @Override
    public void tearDown() {
        try{
            super.tearDown();
        }
        catch(Exception e) {


        }
    }

    public void testCase() {
        Log.i(TAG,"Basic Insert Test");
    }

//    public void testPreconditions() {
//        // using this test to check data already inside my asana profile
//
//        Log.i(TAG,"Test Preconstructed Database");
//        String[] projection = {"workspace_id","name"};
//        String selection = null;
//        String[] selectionArgs = null;
//        String sortOrder = null;
//        Cursor result = resolve.query(Uri.parse("content://com.example.myapp.MyContentProvider/workspace"), projection, selection, selectionArgs, sortOrder);
//
//        assertEquals(result.getCount(), 3); //check number of returned rows
//        assertEquals(result.getColumnCount(), 2); //check number of returned columns
//
//        result.moveToNext();
//
//        for(int i = 0; i < result.getCount(); i++) {
//            String id = result.getString(0);
//            String name = result.getString(1);
//            Log.i(TAG,id + " : " + name);
//            result.moveToNext();
//        }
//    }

}
