package com.example.android.recreatesafe.adapters;

import android.content.Context;
import android.os.Bundle;

import com.example.android.recreatesafe.utilities.Park;
import com.example.android.recreatesafe.R;
import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lizha on 10/29/2017.
 */

public class ResultListFirebaseAdapter{
    private DatabaseReference mDatabaseQueryTypeRef;
    private DatabaseReference mDatabaseQueryNameRef;
    private DatabaseReference mDatabaseParkRef;
    private Set<String> mParkIDSet;
    private List<String> mParkIDList;
    private List<String> mParkIDListofName;
    private List<String> mParkIDListofType;
    private GeoFire mGeoFire;
    private List<Park> mParkList;
    private final Context mContext;
    private final Bundle mArgs;

    private String mSearchType;
    private String mSearchKeyWord;
    private String[] mSearchKeyWordsArray;

    private ParkListAcquiredListener mListener;

    public interface ParkListAcquiredListener{
        void onAcquiredParkList(ArrayList<Park> mParkList);
    }

    public ResultListFirebaseAdapter(Context context, Bundle args, ParkListAcquiredListener listener){

        mListener = listener;
        mContext = context;
        mArgs = args;



        mSearchType = args.getString(context.getResources().getString(R.string.extra_key_search_type));
        mSearchKeyWord = args.getString(context.getResources().getString(R.string.extra_key_search_name));


        mParkIDListofName = new ArrayList<String>();
        mParkIDListofType = new ArrayList<String>();
        mParkIDList = new ArrayList<String>();




        mDatabaseQueryNameRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseQueryNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(mSearchKeyWord == null || mSearchKeyWord.length() == 0){
                    List<String> list = new ArrayList<String>(((HashMap<String,Object>) dataSnapshot.child("Park").getValue()).keySet());
                    if(list != null){
                        for(int i = 0 ; i < list.size() ; i++){
                            mParkIDListofName.add(list.get(i));
                        }
                    }
                }
                else {
                    mSearchKeyWordsArray = mSearchKeyWord.split(" +");
                    for(String searchKeyWord: mSearchKeyWordsArray){

                        if((List<String>) dataSnapshot.child("Query").child("Park_Name").child(searchKeyWord).getValue() != null)
                            mParkIDListofName.addAll( (List<String>) dataSnapshot.child("Query").child("Park_Name").child(searchKeyWord).getValue() );
//                    for(int i = 0 ; i < mParkIDListofName.size(); i++){
//                        System.out.println(mParkIDListofName.get(i));
//                    }
                    }
                }


                mDatabaseQueryTypeRef = FirebaseDatabase.getInstance().getReference(mContext.getResources().getString(R.string.firebese_path_query_type));

                mDatabaseQueryTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(mSearchType.equals("All")){
                            HashMap<String, List> typeMap = (HashMap<String, List>) dataSnapshot.getValue();
                            if(typeMap != null ){
                                Iterator it = typeMap.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry pair = (Map.Entry)it.next();
                                    if(pair.getValue() != null){
                                        mParkIDListofType.addAll( (List<String>) pair.getValue());
                                    }
//                                    System.out.println(pair.getKey() + " = " + pair.getValue().toString());
                                    it.remove(); // avoids a ConcurrentModificationException
                                }
                            }
                        } else{
                            if((List<String>) dataSnapshot.child( mSearchType ).getValue() != null) {

                                mParkIDListofType.addAll((List<String>) dataSnapshot.child( mSearchType ).getValue());
                            }
                        }


                        for(int i = 0 ; i < mParkIDListofName.size(); i++){
                            System.out.println("in name list: "+ mParkIDListofName.get(i));
                        }

                        for(int i = 0 ; i < mParkIDListofType.size(); i++){
                            System.out.println("in type list: "+ mParkIDListofType.get(i));
                        }



                        mParkIDSet = new HashSet<String>(mParkIDListofName);
                        mParkIDListofName = new ArrayList<String>(mParkIDSet);
                        mParkIDListofType = new ArrayList<String>(new HashSet<String>(mParkIDListofType));
                        for(int i = 0 ; i < mParkIDListofName.size(); i++){
                            System.out.println("in name list: "+ mParkIDListofName.get(i));
                        }

                        for(int i = 0 ; i < mParkIDListofType.size(); i++){
                            System.out.println("in type list: "+ mParkIDListofType.get(i));
                        }

                        System.out.println("size of set:" + mParkIDSet.size());
                        mParkIDSet.retainAll( new HashSet<String>(mParkIDListofType) );
                        System.out.println("size of set:" + mParkIDSet.size());
                        System.out.println("size of type list:" + mParkIDListofType.size());
                        System.out.println("size of name list:" + mParkIDListofName.size());



                        mParkIDList = new ArrayList<String>(mParkIDSet);
                        for(int i = 0 ; i < mParkIDList.size(); i++){
                            System.out.println("in name type result: "+mParkIDList.get(i));
                        }
                        mDatabaseParkRef = FirebaseDatabase.getInstance().getReference(mContext.getResources().getString(R.string.firebese_path_parks));
                        mDatabaseParkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<Park> mParkList = new ArrayList<Park>();
                                for(int i = 0 ; i < mParkIDList.size(); i++){
                                    HashMap<String, Object> parkMap = (HashMap<String, Object>) dataSnapshot.child(mParkIDList.get(i)).getValue();
                                    List keyList = new ArrayList(parkMap.keySet());
                                    Park thisPark = new Park(mParkIDList.get(i),(String)parkMap.get("Name"), (String)parkMap.get("Type"),(double) parkMap.get("Latitude"), (double)parkMap.get("Longitude"), (String)parkMap.get("Address"), (String)parkMap.get("Rating"), (String)parkMap.get("Rating"));
                                    thisPark.printf();
                                    mParkList.add(thisPark);
                                }

                                mParkList = new ArrayList<Park>(new HashSet<Park>(mParkList));

                                mListener.onAcquiredParkList(mParkList);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }





}
