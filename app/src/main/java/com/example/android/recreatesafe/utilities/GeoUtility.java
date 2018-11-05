package com.example.android.recreatesafe.utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.android.recreatesafe.utilities.Network.getResponseFromHttpUrl;

/**
 * Created by lizha on 10/23/2017.
 */

public class GeoUtility {
    public String getLocationFromEncoding(double latitude, double longitude) {
        URL url = null;
        String locJson = null;
        try {
            url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng="
                    + latitude + "," + longitude
                    +"&key=AIzaSyBl-gbqjDgjTnnGmGAZ8sxbQrBWUhWW1mc");
            // TODO modify api key here

        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        if(url != null){
            try{
                locJson = getResponseFromHttpUrl(url);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        if(locJson == null){
            return "Problem occurred fetching location.";
        }
        return getLocationDescFromLocJson(locJson);

    }

    public String getLocationDescFromLocJson(String locJson){
        String formattedAddr = "Problem occurred fetching location.";
        try {
            JSONObject obj = new JSONObject(locJson);
            formattedAddr = obj.getJSONArray("results").getJSONObject(0).getString("formatted_address");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return formattedAddr;
    }

    public double getDis(double lat1, double lon1, double lat2, double lon2){
        double p = 0.017453292519943295;    // Math.PI / 180
        double a = 0.5 - Math.cos((lat2 - lat1) * p)/2 +
                Math.cos(lat1 * p) * Math.cos(lat2 * p) *
                        (1 - Math.cos((lon2 - lon1) * p))/2;

        return 12742 * Math.asin(Math.sqrt(a));
    }

    public HashMap<Park, Double> computeDisMap(double latitude, double longitude, List<Park> parkList){
        HashMap<Park, Double> disMap = new HashMap<Park, Double>();
        for(int i = 0 ; i < parkList.size(); i++){
            Park thisPark = parkList.get(i);
            double thisDis = getDis(latitude,longitude, thisPark.getLatitude(), thisPark.getLongitude());
            disMap.put(thisPark, thisDis);
        }
        return disMap;
    }

    public List<Double> computeDisList(double latitude, double longitude, List<Park> parkList){
        List<Double> disList = new ArrayList<Double>();
        for(int i = 0 ; i < parkList.size(); i++){
            Park thisPark = parkList.get(i);
            double thisDis = getDis(latitude,longitude, thisPark.getLatitude(), thisPark.getLongitude());
            disList.add(thisDis);
        }
        return disList;
    }



}
