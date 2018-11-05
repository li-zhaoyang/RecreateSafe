package com.example.android.recreatesafe.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.recreatesafe.R;
import com.example.android.recreatesafe.utilities.GeoUtility;


/**
 * Created by lizha on 10/26/2017.
 */

public class LocationTextLoader extends AsyncTaskLoader<String>{
    private String mLocationText;
    private final String LOCATION_LATITUDE_EXTRA ;
    private final String LOCATION_LONGITUDE_EXTRA ;
    private Bundle mArgs;


    public LocationTextLoader(Context context, Bundle args){
        super(context);
        mArgs = args;
        LOCATION_LATITUDE_EXTRA = context.getResources().getString(R.string.exta_key_cur_latitude);
        LOCATION_LONGITUDE_EXTRA = context.getResources().getString(R.string.extra_key_cur_longitude);
    }



    @Override
    protected void onStartLoading() {
        if (mArgs == null) {
            return;
        }
        if (mLocationText != null) {
            deliverResult(mLocationText);
        } else {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        double latitude = mArgs.getDouble(LOCATION_LATITUDE_EXTRA);
        double longitude = mArgs.getDouble(LOCATION_LONGITUDE_EXTRA);
        String locationText = new GeoUtility().getLocationFromEncoding(latitude, longitude);
        return locationText;
    }

    @Override
    public void deliverResult(String locationText) {
        mLocationText = locationText;
        super.deliverResult(locationText);
    }

}
