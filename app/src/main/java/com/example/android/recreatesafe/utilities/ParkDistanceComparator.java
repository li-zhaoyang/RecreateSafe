package com.example.android.recreatesafe.utilities;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by lizha on 11/21/2017.
 */

public class ParkDistanceComparator implements Comparator<Park> {


    HashMap<Park, Double> mDisMap;

    public ParkDistanceComparator(HashMap<Park, Double> map) {
        mDisMap = map;
    }

    @Override
    public int compare(Park o1, Park o2) {
        return Double.compare(mDisMap.get(o1), mDisMap.get(o2));
    }
}

