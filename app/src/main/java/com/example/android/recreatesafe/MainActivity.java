package com.example.android.recreatesafe;


import android.content.Intent;

import android.location.Location;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.recreatesafe.adapters.AuthenticationAdapter;
import com.example.android.recreatesafe.adapters.GeoLocationAdapter;
import com.example.android.recreatesafe.adapters.LocationTextLoaderAdapter;
import com.firebase.ui.auth.AuthUI;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, LocationTextLoaderAdapter.LocationTextAcquiredListener,GeoLocationAdapter.GeoLocationAcquiredListener{

    private static final String LOCATION_LATITUDE_EXTRA = "latitude";
    private static final String LOCATION_LONGITUDE_EXTRA = "longitude";

    private static final int LOCATION_SEARCH_LOADER = 22;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
    private final String TAG = "RecreateSafe";


    private TextView mGreetingsTextView;
    private TextView mLocationTextView;
    private TextView mGeoCoordinateTextView;
    private TextView mLastUpdateTimeTextView;
    private EditText mSearchKeyWordEditText;
    private Spinner mSpinner;
    private Button mSubmitButton;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location mCurrentLocation;
    private String mLastUpdateTime = null;
    private boolean mRequestingLocationUpdates;
    private LocationRequest mLocationRequest;


    private AuthenticationAdapter mAuthenticationAdapter;
    private LocationTextLoaderAdapter mLocationTextLoaderAdapter;
    private GeoLocationAdapter mGeoLocationAdapter;
    private String mUsername;
    private String mUserEmail;
    private String mUserID;
    private boolean mLogInState;
    private static final String ANONYMOUS = "Nobody";
    private static final int RC_SIGN_IN = 250;
    private static final int RC_USER_SETTINGS = 233;

    private String mSearchName;
    private String mSearchType;
    private String[] mStringArrayInSpinner;

    private DatabaseReference mFirebaseUserRef;


    //    private static final int RC_SIGN_IN = 250;
//    private FirebaseAuth mFirebaseAuth;
//    private FirebaseAuth.AuthStateListener mAuthStateListener;
//    private static final String ANONYMOUS = "Nobody";
//    private String mUsername ;

    //start activities override
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Location
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//            System.out.println("Here0!!!");
//        }



        mGreetingsTextView = (TextView) findViewById(R.id.tv_greetings);
        mGeoCoordinateTextView =(TextView) findViewById(R.id.tv_latitude);
//        mLongitudeTextView = (TextView) findViewById(R.id.tv_longitude);
        mLocationTextView = (TextView) findViewById(R.id.tv_location_text);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.tv_last_update_time);
        mSearchKeyWordEditText = (EditText) findViewById(R.id.et_search_key_word);
        mSpinner = (Spinner) findViewById(R.id.sp_select_type);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_array, android.R.layout.simple_spinner_item);

//        mStringArrayInSpinner = getResources().getStringArray(R.array.types_array);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        mSpinner.setAdapter(adapter);
        mSubmitButton = (Button) findViewById(R.id.bt_submit_query);
        mAuthenticationAdapter = new AuthenticationAdapter(MainActivity.this, this);
        mGeoLocationAdapter = new GeoLocationAdapter(this,getSupportLoaderManager(),this,this);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSearchName = mSearchKeyWordEditText.getText().toString();
                mSearchType = mSpinner.getSelectedItem().toString();
                Intent openResultIntent = new Intent(MainActivity.this,ResultActivity.class);
                String EXTRA_KEY_SEARCH_NAME = getResources().getString(R.string.extra_key_search_name);
                String EXTRA_KEY_SEARCH_TYPE = getResources().getString(R.string.extra_key_search_type);
                String EXTRA_KEY_CUR_LATITUDE = getResources().getString(R.string.exta_key_cur_latitude);
                String EXTRA_KEY_CUR_LONGITUDE = getResources().getString(R.string.extra_key_cur_longitude);
                openResultIntent.putExtra("USERID", mUserID);
                openResultIntent.putExtra(EXTRA_KEY_SEARCH_NAME, mSearchName);
                openResultIntent.putExtra(EXTRA_KEY_SEARCH_TYPE, mSearchType);
                if(mCurrentLocation != null){
                    openResultIntent.putExtra(EXTRA_KEY_CUR_LATITUDE, mCurrentLocation.getLatitude() );
                    openResultIntent.putExtra(EXTRA_KEY_CUR_LONGITUDE, mCurrentLocation.getLongitude());
                }else{
                    openResultIntent.putExtra(EXTRA_KEY_CUR_LATITUDE, Double.parseDouble( getResources().getString(R.string.la_city_center_latitude) ) );
                    openResultIntent.putExtra(EXTRA_KEY_CUR_LONGITUDE, Double.parseDouble( getResources().getString(R.string.la_city_center_longitude) ) );

                }

                startActivity(openResultIntent);
//                if(mSearchName == null || mSearchName.length()== 0){
//                    Toast.makeText(MainActivity.this, "Please input search keywords!", Toast.LENGTH_SHORT).show();
//                    return;
//                }else{
//
//                    // Code here executes on main thread after user presses button
//                }

            }
        });




        //mAuthenticationAdapter.onCreateMainActivity();

//
//        mFirebaseAuth = FirebaseAuth.getInstance();
//
//
//
//        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if(user != null){
//                    onSignedInInitialize(user.getDisplayName());
//
//                }else {
//                    onSignedOutCleanUp();
//                    startActivityForResult(
//                            AuthUI.getInstance()
//                                    .createSignInIntentBuilder()
//                                    .setIsSmartLockEnabled(false)
//                                    .setAvailableProviders(
//                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
//                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
//                                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
//                                    .build(),
//                            RC_SIGN_IN);
//                }
//
//            }
//        };

//        mLocationTextLoaderAdapter = new LocationTextLoaderAdapter(this, mLocationTextView);
//        getSupportLoaderManager().initLoader(LOCATION_SEARCH_LOADER, null, mLocationTextLoaderAdapter);
    }

    public void jumpToUserSetting(String userID){
        Intent userSettingIntent = new Intent(MainActivity.this, SettingsActivity.class);
        userSettingIntent.putExtra(getResources().getString(R.string.extra_key_user_id), userID);
        startActivityForResult(userSettingIntent, RC_USER_SETTINGS);
        //TO-DO start setting activity for result
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            mUsername = user.getDisplayName();
            if(mGreetingsTextView != null) mGreetingsTextView.setText("Hello, " + mUsername+"!");
            mLogInState = true;
            mUserEmail = user.getEmail();
            mUserID = user.getUid();
//            mAuthenticationAdapter.onSignedInInitialize(user.getDisplayName());
            mFirebaseUserRef = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.firebese_path_user));
            mFirebaseUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if( !dataSnapshot.hasChild(mUserID))
                        jumpToUserSetting(mUserID);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
//            System.out.println("here555"+mUsername);
        }else {
//            mAuthenticationAdapter.onSignedOutCleanUp();
            mUsername = ANONYMOUS;
            mLogInState = false;
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "You've signed in!",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(this, MainActivity.class));
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Sign in cancelled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if(requestCode == RC_USER_SETTINGS){
            if(requestCode == RESULT_OK){
                Toast.makeText(this, "You are all set!",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(this, MainActivity.class));
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Must set before use!",Toast.LENGTH_SHORT).show();
            }
        }
//        mAuthenticationAdapter.onActivityResult(requestCode,resultCode);

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

    @Override
    protected void onStart() {
//        mGoogleApiClient.connect();
        mGeoLocationAdapter.connectGoogleApiClient();
        super.onStart();
    }

    @Override
    protected void onStop() {
//        mGoogleApiClient.disconnect();
        mGeoLocationAdapter.disconnectGoogleApiClient();
        super.onStop();
    }


    //End of activity override

    // Start of GoogleApiClient.ConnectionCallbacks override
//    @Override
//    @TargetApi(Build.VERSION_CODES.M)
//    public void onConnected(Bundle connectionHint) {
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(30000);
//        System.out.println("Here1!!!");
//        if(ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") != PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{"android.permission.ACCESS_COARSE_LOCATION"},
//                    MY_PERMISSIONS_REQUEST_LOCATION);
//        }
//
//
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        System.out.println("Here2!!!");
//        if (mLastLocation != null) {
//            mCurrentLocation = mLastLocation; //TOdsDO maybe not right
//            updateUI();
//            System.out.println("Here3!!!"); //Never Appeared
//        }else {
//
//        }
//
//        startLocationUpdates();
//
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.i(TAG, "Google API connection failed!");
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.i(TAG, "Google API connection suspended!");
//
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        mCurrentLocation = location;
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        updateUI();
//    }
    // /End of GoogleApiClient.ConnectionCallbacks override
    // start layout parts override

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_logout) {
            mAuthenticationAdapter.logout(this);
            return true;
        }else if(itemThatWasClickedId == R.id.action_settings){
            jumpToUserSetting(mUserID);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAcquiredLocation(Location location) {
        if(location != null){
            mCurrentLocation = location;
            System.out.println("got coordinates:"+location.getLatitude()+", "+location.getLongitude());
            mGeoCoordinateTextView.setText("Your Coordinate:\n"+String.valueOf(location.getLatitude())+", "+String.valueOf(location.getLongitude()));
            mLastUpdateTimeTextView.setText("Updated: " + DateFormat.getTimeInstance().format(new Date()));
        }
    }

    @Override
    public void onAcquiredLocationText(String locationText) {
        System.out.println("got location: "+locationText);
        mLocationTextView.setText("You are near: "+ locationText);
    }


//    @TargetApi(Build.VERSION_CODES.M)
//    protected void startLocationUpdates() {
//        if(checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(
//                    mGoogleApiClient, mLocationRequest, this);
//        }
//    }
//


//    private void updateUI() {
//        mGeoCoordinateTextView.setText("Your Coordinate:\n"+String.valueOf(mCurrentLocation.getLatitude())+", "+String.valueOf(mCurrentLocation.getLongitude()));
////        mLongitudeTextView.setText("");
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        mLastUpdateTimeTextView.setText("Updated: "+ mLastUpdateTime);
//
//        Bundle queryBundle = new Bundle();
//        queryBundle.putDouble(LOCATION_LATITUDE_EXTRA, mCurrentLocation.getLatitude());
//        queryBundle.putDouble(LOCATION_LONGITUDE_EXTRA, mCurrentLocation.getLongitude());
//        LoaderManager loaderManager = getSupportLoaderManager();
//        Loader<String> locationSearchLoader = loaderManager.getLoader(LOCATION_SEARCH_LOADER);
//        if (locationSearchLoader == null) {
//            loaderManager.initLoader(LOCATION_SEARCH_LOADER, queryBundle, mLocationTextLoaderAdapter);
//        } else {
//            loaderManager.restartLoader(LOCATION_SEARCH_LOADER, queryBundle, mLocationTextLoaderAdapter);
//        }
//        System.out.println("Here33!!!");
//    }



    //finish layout parts override
    //Start loader pverride
//    @Override
//    public Loader<String> onCreateLoader(int id, Bundle args) {
//        return new LocationTextLoader(this, args);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<String> loader, String data) {
//        if (null == data) {
//            mLocationTextView.setText("Failed to get your address.");
//        } else {
//            mLocationTextView.setText("You are near:\n" + data);
//
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<String> loader) {
//
//    }
    //finish loader override
    // start other functions
//    void logout(){
//        onSignedOutCleanUp();
//        AuthUI.getInstance()
//                .signOut(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // user is now signed out
//                        //startActivity(new Intent(MainActivity.this, MainActivity.class));
//                        //finish();
//                    }
//                });
//    }

//    private void onSignedInInitialize(String username) {
//        mUsername = username;
//        mGreetingsTextView.setText("Hello, " + username+"!");
//    }
//    private void onSignedOutCleanUp(){
//        mUsername = ANONYMOUS;
//    }



}
