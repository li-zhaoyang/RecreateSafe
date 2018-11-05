package com.example.android.recreatesafe;

//import android.support.v7.app.AlertController;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.example.android.recreatesafe.adapters.AuthenticationAdapter;
import com.example.android.recreatesafe.adapters.MyAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class DetailActivity extends AppCompatActivity implements ValueEventListener, FirebaseAuth.AuthStateListener {


    TextView tv_Address;
    TextView tv_ParkName;
    TextView tv_Type;
    TextView tv_City;
    TextView tv_State;
    TextView tv_Zip;
    TextView tv_Rating;
    TextView tv_Phone;
    TextView tv_Website;
    TextView tv_Tips;
    // RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    RecyclerView.Adapter MyAdapter;
    RecyclerView mRecyclerView;
    // Button b;
    private String parkid;
    private String user_age_sex;
    private String user_age;
    private String user_sex;
    private HashMap mParkInfoMap;
    private HashMap mCrimeMap;
    private HashMap mComment;
    private Button b_submitcomment;
    private EditText et_comment;
    private String userName;
    private String comment;
    private Button b_map;

    private DatabaseReference myRefParkInfo;
    private DatabaseReference myRefCrime;
    private DatabaseReference myRefUserComment;
    private DatabaseReference myRefReadComment;
    private String userId;
    private String CommentId;
    private ArrayList<String> CommentList;
    static public String latitude;
    static public String longitude;
    String latitude_string;
    String longitude_string;
    String parkname_string;
    String user_name_string;

    private AuthenticationAdapter mAuthenticationAdapter;

    private static final int RC_USER_SETTINGS = 233;

    // private ListView listViewBasic =null;
//    private String[] listViewData = new String[]{
//            "test1","test2","test3",
//            "test4","test5","test6",
//            "test7","test8","test9",
//            "test10","test11","test12"
//    };
    //private ListView listView;
    //private MyAdapter adapter;
    //private List<String> data;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuthenticationAdapter = new AuthenticationAdapter(this,this);
//        TODO


        parkid = getIntent().getStringExtra(getResources().getString(R.string.extra_key_park_id));
        userId = getIntent().getStringExtra("USERID");
        //userName="wlj";


//        int age = Integer.valueOf(user_age).intValue();
//        if(age>=0 && age<=12){user_age_sex = "0";}
//        if(age>=13 && age<=21){user_age_sex = "1";}
//        if(age>=22 && age<=39){user_age_sex = "2";}
//        if(age>=40 && age<=64){user_age_sex = "3";}
//        if(age>=65){user_age_sex = "4";}
//
//        if(user_sex.equals("f")){
//
//            user_age_sex += "0";
//        }
//        if(user_sex.equals("m")){
//
//            user_age_sex += "1";
//        }
//        System.out.println("user_age_sex:"+user_age_sex);



//        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        MyAdapter = new MyAdapter(getData());



        //myRef.setValue("Hello, World!");
        //  b = (Button) findViewById(R.id.button2);
        tv_Address = (TextView) findViewById(R.id.tv_Address);
        tv_ParkName = (TextView) findViewById(R.id.tv_name);
        tv_Type = (TextView) findViewById(R.id.tv_Type);
//        tv_City = (TextView) findViewById(R.id.tv_City);
//        tv_State = (TextView) findViewById(R.id.tv_State);
//        tv_Zip = (TextView) findViewById(R.id.tv_Zip);
        tv_Rating = (TextView) findViewById(R.id.tv_Rating);
        tv_Phone = (TextView) findViewById(R.id.tv_Phone);
        tv_Website = (TextView) findViewById(R.id.tv_Website);
        tv_Tips = (TextView) findViewById(R.id.tv_Tips);

        b_submitcomment = (Button) findViewById(R.id.b_submitcomment);
        et_comment = (EditText) findViewById(R.id.et_comment);
        b_map = (Button) findViewById(R.id.b_map);





        //________________recyclerview

        b_submitcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment = et_comment.getText().toString();
                myRefUserComment = FirebaseDatabase.getInstance().getReference("Comment");
                Map<String, String> post = new HashMap<String, String>();
                post.put("UserId",userId);
                post.put("Comment", comment);
                post.put("parkId",parkid);
                post.put("UserName",user_name_string);

                myRefUserComment.push().setValue(post);
                et_comment.setText("");
                //refresh();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0) ;


            }
        });




        //-----------------------------

        myRefParkInfo = FirebaseDatabase.getInstance().getReference();
        myRefParkInfo.addValueEventListener(this);

        myRefParkInfo.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                // List<String> value = (List<String>) dataSnapshot.child(parkid).getValue();
                DataSnapshot dataSnapshot1=dataSnapshot.child("Park");
                mParkInfoMap = (HashMap<String, List>) dataSnapshot1.child(parkid).getValue();
                System.out.println("the value is :"+mParkInfoMap);


                myRefCrime = FirebaseDatabase.getInstance().getReference("Query/User_Crime_Fit");
                myRefCrime.addValueEventListener(new ValueEventListener() {
                    @Override

                    public void onDataChange(DataSnapshot dataSnapshot) {


                        mCrimeMap = (HashMap<String, List>) dataSnapshot.child(parkid).getValue();
                        System.out.println("the value is :"+mCrimeMap);
//----------------------------------------------------------------------------------------------------------------------------
                        if(mCrimeMap != null ){
                            Iterator it = mCrimeMap.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                System.out.println(pair.getKey()+":"+pair.getValue());
                                String eachValue = pair.getValue().toString();
                                String eachKey = pair.getKey().toString();

                                if (eachKey.equals(user_age_sex)){

                                    String[] s = eachValue.split(" ");
                                    String time = s[0].substring(0,s[0].length()-1);
                                    String percentage = s[1].substring(0,s[1].length()-1);

                                    double p = Double.valueOf(percentage).doubleValue();

                                    if(p<1){
                                        tv_Tips.setText("SAFE!"+"\n");
                                        tv_Tips.append("Crime Peak Time: "+time+"\n");
                                        tv_Tips.append("Percentage of Dangerous: "+percentage+"%\n");
                                        tv_Tips.append("Have Fun & Relax!");
                                    }

                                    if(p>=1 && p<9){
                                        tv_Tips.setText(" "+"\n");
                                        tv_Tips.append("Crime Peak Time: "+time+"\n");
                                        tv_Tips.append("Percentage of Dangerous: "+percentage+"%\n");
                                        tv_Tips.append("Have Fun & Be Careful!");
                                    }

                                    if(p>=9){
                                        tv_Tips.setText("DANGER!"+"\n");
                                        tv_Tips.append("Crime Peak Time: "+time+"\n");
                                        tv_Tips.append("Percentage of Dangerous: "+percentage+"%\n");
                                        tv_Tips.append("Avoid Go Alone!");
                                    }

                                    System.out.println("time"+time);
                                    System.out.println("percentage"+s[1]);




                                }

                                it.remove(); // avoids a ConcurrentModificationException
                            }
                        }

//--------------------------------------------------------------------------------------------------------------------------


                    }



                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mParkInfoMap != null ){
//                    Iterator it = mParkInfoMap.entrySet().iterator();
//                    while (it.hasNext()) {
//                        Map.Entry pair = (Map.Entry)it.next();
//                        System.out.println(pair.getKey()+":"+pair.getValue());
//                        String eachValue = pair.getValue().toString();
//                        String eachKey = pair.getKey().toString();
//
//                        if (eachKey.equals("Address")){
//                        tv.setText(eachValue);}
//
//
//
//
//                        it.remove(); // avoids a ConcurrentModificationException
//                    }
//                }
//
//            }
//        });
//-----------------------------------------------------------------------------------------------------------------------------

                if(mParkInfoMap != null ){
                    Iterator it = mParkInfoMap.entrySet().iterator();
                    String city_string="";
                    String state_string="";
                    String zip_string="";
                    String address_string="";
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        System.out.println(pair.getKey()+":"+pair.getValue());
                        String eachValue = pair.getValue().toString();
                        String eachKey = pair.getKey().toString();


                        if (eachKey.equals("Name")){
                            tv_ParkName.setText(eachValue);}
                        if (eachKey.equals("Address")){
                            address_string=eachValue;
                            //tv_Address.setText(eachKey+":  "+eachValue);
                        }
                        if (eachKey.equals("Type")){
                            tv_Type.setText(eachKey+":  "+eachValue);}
                        if (eachKey.equals("City")){
                            city_string = eachValue;
                            // tv_City.setText(eachKey+":  "+eachValue);
//                            tv_Address.append(", "+eachValue);
                        }
                        if (eachKey.equals("State")){
                            state_string = eachValue;
                            //tv_State.setText(eachKey+":  "+eachValue);
//                            tv_Address.append(", "+eachValue);
                        }
                        if (eachKey.equals("Zip")){
                            zip_string = eachValue;
                            //tv_Zip.setText(eachKey+":  "+eachValue);
//                            tv_Address.append(", "+eachValue);
                        }
                        if (eachKey.equals("Rating")){
                            tv_Rating.setText(eachKey+":  "+eachValue);}
                        if (eachKey.equals("Phone")){
                            tv_Phone.setText(eachKey+":  "+eachValue);}
                        if (eachKey.equals("Website")){
                            tv_Website.setText(eachKey+":  "+eachValue);
                            if (eachKey.equals("Latitude")){
                                latitude=eachValue;
                                System.out.println("latitude"+latitude);
                            }
                            if (eachKey.equals("Longitude")){
                                longitude=eachValue;
                                System.out.println("longitude"+longitude);
                            }


//                    String WebLink ="<a href='"+eachValue+"'>"+eachValue+"</a>";
//                    tv_Website.setText(Html.fromHtml(WebLink));
//
//                    tv_Website.setMovementMethod(ScrollingMovementMethod.getInstance());
                        }




                        it.remove(); // avoids a ConcurrentModificationException
                    }

                    tv_Address.setText("Address: "+address_string+", "+city_string+", "+state_string+" "+zip_string);
                }




//-----------------------------------------------------------------------------------------------------------------------------

            }

            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



        b_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String mUriStr = "geo:0,0?q="+latitude_string+","+longitude_string+"(Treasure)";

                String mUriStr = "geo:0,0?q="+latitude_string+","+longitude_string;
                String parkname_string_uri = "("+parkname_string+")";
                System.out.println("latitude click:"+latitude);
                System.out.println("URL:"+mUriStr);
                Uri mUri = Uri.parse(mUriStr+Uri.encode(parkname_string_uri));
                System.out.println("mrui:"+parkname_string_uri);
                showMap(mUri);

            }
        });
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        getData();
//        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setAdapter(MyAdapter);
        // mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

    }


    //    private void initView(){
//        listViewBasic = (ListView)super.findViewById(R.id.listViewBasic);
//        listViewBasic.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listViewData));
//    }
    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        MyAdapter = new MyAdapter(getData());
    }

//    private void url() {
//        String mUriStr = "http://www.google.com/maps?q="+longitude+","+latitude;
//        System.out.println("latitude click:"+latitude);
//        System.out.println("URL:"+mUriStr);
//        Uri mUri = Uri.parse(mUriStr);
//        showMap(mUri);
//    }





    private ArrayList<String> getData() {


        myRefUserComment = FirebaseDatabase.getInstance().getReference("Comment");
        myRefUserComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CommentList = new ArrayList<String>();

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Map eachcomment = (Map) dsp.getValue();
                    String eachcomment1 = String.valueOf(eachcomment.get("Comment"));
                    String eachparkid = String.valueOf(eachcomment.get("parkId"));
                    String eachuser = String.valueOf(eachcomment.get("UserName"));
                    System.out.println("eachparkid"+eachparkid);
                    if(eachparkid.equals(parkid)) {
                        CommentList.add(eachuser+" : "+eachcomment1); //add result into array list
                    }
                }
                System.out.println("the comment value is :"+CommentList);

                MyAdapter = new MyAdapter(CommentList);
                mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(MyAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayList<String> data = new ArrayList<>();
        String temp = " item:";
        for(int i = 0; i < 20; i++) {
            data.add(i + temp);
        }
        System.out.println("data:"+data);
        data = CommentList;

        return data;
    }




    public void showMap(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        DataSnapshot dataSnapshot1=dataSnapshot.child("Park");
        Map park_map = (Map)dataSnapshot1.child(parkid).getValue();
        longitude_string = String.valueOf(park_map.get("Longitude"));
        System.out.println("ondatachang"+longitude_string);
        latitude_string = String.valueOf(park_map.get("Latitude"));
        System.out.println("ondatachang"+longitude_string);
        parkname_string = String.valueOf(park_map.get("Name"));

        DataSnapshot dataSnapshot2=dataSnapshot.child("User");
        Map user_map = (Map)dataSnapshot2.child(userId).getValue();
        user_age = String.valueOf(user_map.get("Age"));
        user_sex = String.valueOf(user_map.get("Sex"));
        user_name_string = String.valueOf(user_map.get("Username"));
        System.out.println("user_age_ondatachange:"+user_age);
        System.out.println("user_sex_ondatachange:"+user_sex);

        int age = Integer.valueOf(user_age).intValue();
        System.out.println("age:");
        System.out.println(age);
        if(age>=0 && age<=12){user_age_sex = "0";}
        if(age>=13 && age<=21){user_age_sex = "1";}
        if(age>=22 && age<=39){user_age_sex = "2";}
        if(age>=40 && age<=64){user_age_sex = "3";}
        if(age>=65){user_age_sex = "4";}

        if(user_sex.equals("F")){

            user_age_sex += "0";
        }
        if(user_sex.equals("M")){

            user_age_sex += "1";
        }
        System.out.println("user_age_sex:"+user_age_sex);


    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

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
            jumpToUserSetting(userId);
            return true;
        }else if(itemThatWasClickedId == R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void jumpToUserSetting(String userID){
        Intent userSettingIntent = new Intent(DetailActivity.this, SettingsActivity.class);
        userSettingIntent.putExtra(getResources().getString(R.string.extra_key_user_id), userID);
        startActivityForResult(userSettingIntent, RC_USER_SETTINGS);
        //TO-DO start setting activity for result
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
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser() == null){
            finish();
        }

    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent != null) {
            return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return intent;
    }
}


