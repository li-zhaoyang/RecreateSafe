package com.example.android.recreatesafe.utilities;


import java.util.Collections;
import java.util.List;

/**
 * Created by lizha on 11/25/2017.
 */

public class MySorter {
    public List<Double> disListofSortedBy(String sortMethod, List<Park> parkList , Double latitude, double longitude){
        GeoUtility gU = new GeoUtility();
        switch (sortMethod){//
            case "Distance":{        //sort by distance
                Collections.sort(parkList, new ParkDistanceComparator(gU.computeDisMap(latitude, longitude, parkList)));
                break;
            }
            case "Name":{        //sort by name
                Collections.sort(parkList, new ParkComparator(1));
                break;
            }
            case "Type": {       //sort by type
                Collections.sort(parkList, new ParkComparator(2));
                break;
            }
            case "Rating": {       //sort by rating
                Collections.sort(parkList, new ParkComparator(3));
                break;
            }
            case "Address":{        //sort by address
                Collections.sort(parkList, new ParkComparator(4));
                break;
            }
            default:break;
        }
        return gU.computeDisList(latitude, longitude, parkList);

    }
}
