package com.example.android.recreatesafe.utilities;


import java.util.List;

/**
 * Created by lizha on 11/25/2017.
 */

public class MyFilter {

    public void filter(List<Park> parkList, List<String> typeArray){
        if(typeArray.contains("All")){
            return;
        }
        for(int i = 0 ; i < parkList.size(); i ++){
            if(i == parkList.size()) break;
            Park thisPark = parkList.get(i);
            if( !typeArray.contains(thisPark.getType())){
                parkList.remove(thisPark);
                i--;
            }
        }

    }
}
