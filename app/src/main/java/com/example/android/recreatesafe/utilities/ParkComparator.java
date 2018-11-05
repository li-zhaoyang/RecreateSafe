package com.example.android.recreatesafe.utilities;

import java.util.Comparator;

/**
 * Created by lizha on 11/25/2017.
 */

public class ParkComparator implements Comparator<Park> {
    int mCompareMethod;
    public ParkComparator(int method){
        mCompareMethod = method;
    }

    @Override
    public int compare(Park o1, Park o2) {
        switch (mCompareMethod){    //sort by name, type, rating, address
            case 1: return o1.getName().compareTo(o2.getName());       //sort by name
            case 2: return o1.getType().compareTo(o2.getType());
            case 3: return o1.getSafetyRating().compareTo(o2.getSafetyRating());
            case 4: return o1.getLocationShortText().compareTo(o2.getLocationShortText());
            default: return o1.getName().compareTo(o2.getName());
        }
    }
}
