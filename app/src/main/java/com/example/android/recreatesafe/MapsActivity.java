package com.example.android.recreatesafe;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.recreatesafe.utilities.Park;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback ,GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    private ArrayList mParkList;
    private LatLng mLatLng;

    private String mUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserID = getIntent().getStringExtra("USERID");

        mParkList = getIntent().getBundleExtra(getResources().getString(R.string.extra_key_parks_List)).getParcelableArrayList(getResources().getString(R.string.extra_key_parks_List));
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mLatLng = new LatLng(getIntent().getDoubleExtra(getResources().getString(R.string.exta_key_cur_latitude), Double.parseDouble(getResources().getString(R.string.la_city_center_latitude))), getIntent().getDoubleExtra(getResources().getString(R.string.extra_key_cur_longitude), Double.parseDouble(getResources().getString(R.string.la_city_center_longitude))));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {


                for(int i = 0 ; i < mParkList.size(); i++){
                    Park thisPark = (Park) mParkList.get(i);
                    double thisLat = thisPark.getLatitude();
                    double thisLon = thisPark.getLongitude();
                    if(thisLat > 40 || thisLat<30 || thisLon > -110 || thisLon< - 125)  continue;
                    MarkerOptions thisMarkerOptions = new MarkerOptions().position(new LatLng(thisLat, thisLon)).title(thisPark.getName()).snippet(thisPark.getType());
                    Marker thisMarker = mMap.addMarker( thisMarkerOptions );
                    thisMarker.setTag(thisPark);
                    builder.include(thisMarker.getPosition());
                }

                if(mParkList.size()>0){
                    LatLngBounds bounds = builder.build();
                    int padding = 300; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.moveCamera(cu);
                }else{
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
                }
                //Your code where exception occurs goes here...
            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
        mMap.setOnInfoWindowClickListener(this) ;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

//        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//        Toast.makeText(this, ((Park)marker.getTag()).getName() + " clicked", Toast.LENGTH_SHORT).show();
        Park thisPark = (Park) marker.getTag();
        Intent openDetailIntent = new Intent(MapsActivity.this, DetailActivity.class);
        openDetailIntent.putExtra(getResources().getString(R.string.extra_key_park_id), thisPark.getID());
        openDetailIntent.putExtra("USERID", mUserID);
        startActivity(openDetailIntent);
        //TODO start detail activity in map
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:{
                finish();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
