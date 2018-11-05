package com.example.android.recreatesafe.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.TextView;

import com.example.android.recreatesafe.R;
import com.example.android.recreatesafe.loaders.LocationTextLoader;

/**
 * Created by lizha on 10/27/2017.
 */

public class LocationTextLoaderAdapter implements LoaderManager.LoaderCallbacks<String>  {
    Context mContext;
    private int LOCATION_SEARCH_LOADER ;
    TextView mLocationTextView;
    LocationTextLoader mLocationTextLoader;
    private LocationTextAcquiredListener mListener;

    public interface LocationTextAcquiredListener{
        void onAcquiredLocationText(String locationText);

    }

    public LocationTextLoaderAdapter(Context context,  LocationTextAcquiredListener listener){

        mContext = context;
        LOCATION_SEARCH_LOADER = Integer.parseInt(mContext.getResources().getString(R.string.loader_index_location_search));
//        mLocationTextView = locationTextView;
        mListener = listener;

    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        //return null;
        return new LocationTextLoader(mContext, args);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (null == data) {
            mListener.onAcquiredLocationText("Failed to get your location, please wait a moment.");
        } else {
            mListener.onAcquiredLocationText(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }



}
