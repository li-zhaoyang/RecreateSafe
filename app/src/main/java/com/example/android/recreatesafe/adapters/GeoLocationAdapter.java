package com.example.android.recreatesafe.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.util.Log;
import com.example.android.recreatesafe.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * Created by lizha on 10/27/2017.
 */

public class GeoLocationAdapter implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    LocationTextLoaderAdapter mLocationTextLoaderAdapter;
    LoaderManager mLoaderManager;

    GoogleApiClient mGoogleApiClient;
    Context mContext;
    Location mLastLocation, mCurrentLocation;
//    private String mLastUpdateTime = null;

//    TextView mGeoCoordinateTextView, mLastUpdateTimeTextView, mLocationTextView;

    LocationRequest mLocationRequest;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;

    private final String TAG = "RecreateSafe";

    private final String LOCATION_LATITUDE_EXTRA;
    private final String LOCATION_LONGITUDE_EXTRA ;

    private static final int LOCATION_SEARCH_LOADER = 22;

    final private GeoLocationAcquiredListener mGeoListener;
    final private LocationTextLoaderAdapter.LocationTextAcquiredListener mLocationTextListener;

    public interface GeoLocationAcquiredListener{
        void onAcquiredLocation(Location location);
    }



    public GeoLocationAdapter(Context context, LoaderManager loaderManager, GeoLocationAcquiredListener geoListener, LocationTextLoaderAdapter.LocationTextAcquiredListener locationTextListner){
        mContext = context;
        mLoaderManager = loaderManager;
//        mGeoCoordinateTextView = geoCoordinateTextView;
//        mLastUpdateTimeTextView = lastUpdateTimeTextView;
//        mLocationTextView = locationTextView;
        mGeoListener = geoListener;
        mLocationTextListener = locationTextListner;

        LOCATION_LATITUDE_EXTRA = context.getResources().getString(R.string.exta_key_cur_latitude);
        LOCATION_LONGITUDE_EXTRA = context.getResources().getString(R.string.extra_key_cur_longitude);


        mLocationTextLoaderAdapter = new LocationTextLoaderAdapter(mContext, mLocationTextListener);

        //mLoaderManager.initLoader(LOCATION_SEARCH_LOADER, null , mLocationTextLoaderAdapter);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            System.out.println("Here0!!!");
        }
    }



    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onConnected(Bundle connectionHint) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30000);
        System.out.println("Here1!!!");
        if(ContextCompat.checkSelfPermission(mContext, "android.permission.ACCESS_COARSE_LOCATION") != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{"android.permission.ACCESS_COARSE_LOCATION"},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        System.out.println("Here2!!!");
        if (mLastLocation != null) {
            mCurrentLocation = mLastLocation; //TODO maybe not right
            mGeoListener.onAcquiredLocation(mCurrentLocation);
//            updateUI();
            initiateLoader(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
            System.out.println("Here3!!!"); //Never Appeared
        }else {

        }

        startLocationUpdates();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Google API connection failed!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Google API connection suspended!");

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mGeoListener.onAcquiredLocation(mCurrentLocation);
//        updateUI();
        initiateLoader(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void startLocationUpdates() {
        if(ContextCompat.checkSelfPermission(mContext, "android.permission.ACCESS_COARSE_LOCATION") == PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

//    private void updateUI() {
////        mGeoCoordinateTextView.setText("Your Coordinate:\n"+String.valueOf(mCurrentLocation.getLatitude())+", "+String.valueOf(mCurrentLocation.getLongitude()));
//////        mLongitudeTextView.setText("");
////        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
////        mLastUpdateTimeTextView.setText("Updated: "+ mLastUpdateTime);
//
//
//        System.out.println("Here33!!!");
//    }


    public void connectGoogleApiClient(){
        mGoogleApiClient.connect();
    }

    public void disconnectGoogleApiClient(){
        mGoogleApiClient.disconnect();
    }

    private void initiateLoader(double latitude, double longitude){
        Bundle queryBundle = new Bundle();
        queryBundle.putDouble(LOCATION_LATITUDE_EXTRA, latitude);
        queryBundle.putDouble(LOCATION_LONGITUDE_EXTRA, longitude);
        Loader<String> locationSearchLoader = mLoaderManager.getLoader(LOCATION_SEARCH_LOADER);
        if (locationSearchLoader == null) {
            mLoaderManager.initLoader(LOCATION_SEARCH_LOADER, queryBundle, mLocationTextLoaderAdapter);
        } else {
            mLoaderManager.restartLoader(LOCATION_SEARCH_LOADER, queryBundle, mLocationTextLoaderAdapter);
        }
    }




}
