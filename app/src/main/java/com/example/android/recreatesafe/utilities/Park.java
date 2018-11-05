package com.example.android.recreatesafe.utilities;

/**
 * Created by lizha on 10/28/2017.
 */

public class Park implements java.io.Serializable {
    private String mID;
    private String mName;
    private String mType;
    private double mLatitude;
    private double mLongitude;
    private String mLocationShortText;
    private String mSafetyRating;
    private String mUserRating;

    public Park(String id, String name, String typeIndex, double latitude, double longitude, String locationShortText, String safetyRating, String userRating){
        mID = id;
        mName = name;
        mType = typeIndex;
        mLatitude = latitude;
        mLongitude = longitude;
        mLocationShortText = locationShortText;
        mSafetyRating = safetyRating;
        mUserRating = userRating;
    }

    public void printf(){
        System.out.println(mName +" "+ mType +" "+ mLatitude +" "+ mLongitude +" "+ mLocationShortText);
    }

    public boolean equals(Object o){
        return (o instanceof Park) && ( ((Park)o).getName()).equals(this.getName());
    }

    public int hashCode() {
        return mName.hashCode();
    }

    public String getID(){return mID;}
    public String getName(){
        return mName;
    }

    public String getType(){
        return mType;
    }

    public double getLatitude(){
        return mLatitude;
    }

    public double getLongitude(){
        return mLongitude;
    }

    public String getLocationShortText(){
        return mLocationShortText;
    }

    public String getUserRating() { return mUserRating; }

    public String getSafetyRating() { return mSafetyRating;}
}
