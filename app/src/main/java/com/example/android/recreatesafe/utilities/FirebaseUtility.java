package com.example.android.recreatesafe.utilities;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * Created by lizha on 10/28/2017.
 */

public class FirebaseUtility {
    private DatabaseReference mDatabaseRef;
    private static DataSnapshot mDataSnapshot;

    public FirebaseUtility(String referencePath){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(referencePath);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<Long> longs = (List<Long>) dataSnapshot.getValue();
//                for(int i = 0 ; i < longs.size(); i++){
//                    System.out.println(longs.get(i) + "");
//                }
                System.out.println("went through heree!!!");
                mDataSnapshot = dataSnapshot;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabaseRef.addListenerForSingleValueEvent(postListener);
    }

    public DataSnapshot getDataSnapshot(){
        return mDataSnapshot;
    }






}
