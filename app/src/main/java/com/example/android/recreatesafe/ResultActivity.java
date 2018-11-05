package com.example.android.recreatesafe;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.recreatesafe.adapters.AuthenticationAdapter;
import com.example.android.recreatesafe.adapters.ResultListAdapter;
import com.example.android.recreatesafe.adapters.ResultListFirebaseAdapter;
import com.example.android.recreatesafe.utilities.MyFilter;
import com.example.android.recreatesafe.utilities.MySorter;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;



public class ResultActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TypeFilterDialogFragment.TypeFilterDialogListener , FirebaseAuth.AuthStateListener, ResultListAdapter.ListItemClickListener, ResultListFirebaseAdapter.ParkListAcquiredListener, OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener {
    private static final int NUM_LIST_ITEMS = 100;
    private ResultListFirebaseAdapter mResultListFirebaseAdapter;
    private ResultListAdapter mResultListRecyclerViewAdapter;
    private RecyclerView mParksListRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private Button mButton;
    private Spinner mSpinner;

    private AuthenticationAdapter mAuthenticationAdapter;
    private FirebaseUser  mUser;
    private String mUserID;

    private Double mLatitude;
    private Double mLongitude;

    private List<String> mSelectedTypeList;
    private String[] mTypeArray;
    private String[] mSortMethodsArray;


//    private ResultListLoaderAdapter mResultListLoaderAdapter;


    private Toast mToast;

    private String mSearchName;
    private String mSearchType;

    private ArrayList mParkList;
    private ArrayList mThisParkList;

    private GoogleMap mMap;

    private LatLng mLatLng;



    private static final int RC_USER_SETTINGS = 233;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTypeArray = getResources().getStringArray(R.array.types_array);
        mSortMethodsArray = getResources().getStringArray(R.array.sort_methods_array);

        mButton = (Button) findViewById(R.id.bt_type_filter);
        mSpinner = (Spinner) findViewById(R.id.sp_sort_by);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_methods_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TypeFilterDialogFragment mDialog = new TypeFilterDialogFragment();
                mDialog.show(getSupportFragmentManager(), getResources().getString(R.string.dialog_type_filter_title));
            }
        });

        String EXTRA_KEY_SEARCH_NAME = getResources().getString(R.string.extra_key_search_name);
        String EXTRA_KEY_SEARCH_TYPE = getResources().getString(R.string.extra_key_search_type);
        String EXTRA_KEY_CUR_LATITUDE = getResources().getString(R.string.exta_key_cur_latitude);
        String EXTRA_KEY_CUR_LONGITUDE = getResources().getString(R.string.extra_key_cur_longitude);


        if(mSearchName == null) mSearchName = getIntent().getStringExtra(EXTRA_KEY_SEARCH_NAME);
        if(mSearchType == null) mSearchType = getIntent().getStringExtra(EXTRA_KEY_SEARCH_TYPE);
        if(mLatitude == null) mLatitude = getIntent().getDoubleExtra(EXTRA_KEY_CUR_LATITUDE, Double.parseDouble( getResources().getString(R.string.la_city_center_latitude) ));
        if(mLongitude == null) mLongitude = getIntent().getDoubleExtra(EXTRA_KEY_CUR_LONGITUDE, Double.parseDouble( getResources().getString(R.string.la_city_center_longitude) ));

        mUserID = getIntent().getStringExtra("USERID");

        mSelectedTypeList = new ArrayList<String>();
        mSelectedTypeList.add(mSearchType);

        mLatLng = new LatLng(mLatitude, mLongitude);

        mAuthenticationAdapter = new AuthenticationAdapter(ResultActivity.this, this);



        Bundle queryBundle = new Bundle();
        queryBundle.putString(EXTRA_KEY_SEARCH_NAME, mSearchName);
        queryBundle.putString(EXTRA_KEY_SEARCH_TYPE, mSearchType);
        if(mResultListRecyclerViewAdapter == null) mResultListFirebaseAdapter = new ResultListFirebaseAdapter(this, queryBundle, this);
        //System.out.println(mSearchName + " " + mSearchType);

//        mNumbersList = (RecyclerView) findViewById(R.id.rv_numbers);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        mNumbersList.setLayoutManager(layoutManager);
//        mNumbersList.setHasFixedSize(true);
//
//        mResultListRecyclerViewAdapter = new ResultListAdapter(this,getSupportLoaderManager(),queryBundle,NUM_LIST_ITEMS, this);
//        mNumbersList.setAdapter(mResultListRecyclerViewAdapter);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.result_list_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:{
                finish();
                return true;
            }
            case R.id.action_logout:{
                mAuthenticationAdapter.logout(this);
                return true;
            }

            case R.id.action_open_map:{
                Intent openMapIntent = new Intent(this,MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(getResources().getString(R.string.extra_key_parks_List), mThisParkList);
                openMapIntent.putExtra(getResources().getString(R.string.extra_key_parks_List),bundle);
                openMapIntent.putExtra(getResources().getString(R.string.exta_key_cur_latitude), mLatitude);
                openMapIntent.putExtra(getResources().getString(R.string.extra_key_cur_longitude), mLongitude);
                openMapIntent.putExtra("USERID", mUserID);
                startActivityForResult(openMapIntent, 233);
                return true;
            }
            case R.id.action_settings:{
                jumpToUserSetting(mUserID);
                return true;
            }


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int parkIndex) {

        Park clickedPark = (Park) mThisParkList.get(parkIndex);

        if(mToast != null){
            mToast.cancel();
        }
        String toastMessage = "Item #" + clickedPark.getName() +"clicked pos: "+parkIndex+ " clicked. ID: " + clickedPark.getID();
        mToast = Toast.makeText(this, toastMessage,Toast.LENGTH_LONG);
//        mToast.show();
        Intent openDetailIntent = new Intent(ResultActivity.this, DetailActivity.class);
        openDetailIntent.putExtra(getResources().getString(R.string.extra_key_park_id), clickedPark.getID());
        openDetailIntent.putExtra("USERID", mUserID);
        startActivity(openDetailIntent);

        //TODO start detail information

    }

    @Override
    public void onAcquiredParkList(ArrayList<Park> parkList) {
        mParkList = parkList;
//        GeoUtility gU = new GeoUtility();
//        HashMap<Park, Double> disMap = gU.computeDisMap(mLatitude, mLongitude, parkList);
//        ParkDistanceComparator mComparator = new ParkDistanceComparator(disMap);
//        Collections.sort(mParkList, mComparator);
//        List<Double> disList = gU.computeDisList(mLatitude,mLongitude, parkList);

        mParksListRecyclerView = (RecyclerView) findViewById(R.id.rv_parks);
        mLayoutManager = new LinearLayoutManager(this);
        mParksListRecyclerView.setLayoutManager(mLayoutManager);
        mParksListRecyclerView.setHasFixedSize(true);


        setAdapterWithFiltedAndSortedList(mSelectedTypeList, "Distance");


        findViewById(R.id.loadingPanel).setVisibility(View.GONE);


        if(mParkList.size() == 0){
            Toast.makeText(this, "No result! Please return and search again!", Toast.LENGTH_LONG);
            findViewById(R.id.tv_no_result_message).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.resultListAndMap).setVisibility(View.VISIBLE);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    public void setAdapterWithFiltedAndSortedList(List<String> filterList, String sortMethod){
        if(mParkList == null)   return;
        mThisParkList = new ArrayList<Park>(mParkList);
        (new MyFilter()).filter(mThisParkList, filterList);
        List<Double> disList = (new MySorter()).disListofSortedBy(sortMethod, mThisParkList, mLatitude,mLongitude);
        mResultListRecyclerViewAdapter = new ResultListAdapter(this,getSupportLoaderManager(),mThisParkList, disList, this);
        mParksListRecyclerView.setAdapter(mResultListRecyclerViewAdapter);
        if(mMap != null){
            mMap.clear();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(int i = 0 ; i < mThisParkList.size(); i++){
                Park thisPark = (Park) mThisParkList.get(i);
                double thisLat = thisPark.getLatitude();
                double thisLon = thisPark.getLongitude();
                if(thisLat > 40 || thisLat<30 || thisLon > -110 || thisLon< - 125)  continue;
                MarkerOptions thisMarkerOptions = new MarkerOptions().position(new LatLng(thisLat, thisLon)).title(thisPark.getName()).snippet(thisPark.getType());
                Marker thisMarker = mMap.addMarker( thisMarkerOptions );
                thisMarker.setTag(thisPark);
                builder.include(thisMarker.getPosition());
            }

            if(mThisParkList.size()>0){
                LatLngBounds bounds = builder.build();
                int padding = 300; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10);
                mMap.moveCamera(cu);
            }else{
                mMap.moveCamera(CameraUpdateFactory.zoomTo(11));
            }
        }



    }


    @Override
    protected void onResume() {
        mAuthenticationAdapter.addListener();
        //mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        super.onResume();

    }

    @Override
    protected void onPause() {
        mAuthenticationAdapter.removeListener();
//        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        super.onPause();

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////        mAuthenticationAdapter.onActivityResult(requestCode,resultCode);
//    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//        Toast.makeText(this, ((Park)marker.getTag()).getName() + " clicked", Toast.LENGTH_SHORT).show();
        Park thisPark = (Park) marker.getTag();
        Intent openDetailIntent = new Intent(ResultActivity.this, DetailActivity.class);
        openDetailIntent.putExtra(getResources().getString(R.string.extra_key_park_id), thisPark.getID());
        openDetailIntent.putExtra("USERID", mUserID);
        startActivity(openDetailIntent);
        //TODO start detail activity in map
    }

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
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10);
                    mMap.moveCamera(cu);
                }else{
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(11));
                }

                //Your code where exception occurs goes here...
            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
        mMap.setOnInfoWindowClickListener(this) ;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }
    }

    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
        if(firebaseAuth.getCurrentUser() == null){
            finish();
        }else{
            mUser = firebaseAuth.getCurrentUser();
            System.out.println("User: " + mUser.getUid());
        }

    }

    @Override
    public void onSelectedTypeFilter(List<Integer> mSelectedTypes) {
        mSelectedTypeList.clear();
        for(int i= 0; i < mSelectedTypes.size(); i++){
            mSelectedTypeList.add(mTypeArray[mSelectedTypes.get(i)]);
        }




        setAdapterWithFiltedAndSortedList(mSelectedTypeList, mSpinner.getSelectedItem().toString());
    }



    @Override
    public void onTypeFilterCancelled() {
//        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setAdapterWithFiltedAndSortedList(mSelectedTypeList, mSortMethodsArray[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void jumpToUserSetting(String userID){
        Intent userSettingIntent = new Intent(ResultActivity.this, SettingsActivity.class);
        userSettingIntent.putExtra(getResources().getString(R.string.extra_key_user_id), userID);
        startActivityForResult(userSettingIntent, RC_USER_SETTINGS);
        //TO-DO start setting activity for result
    }



}
