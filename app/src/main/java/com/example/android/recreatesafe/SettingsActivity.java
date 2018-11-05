package com.example.android.recreatesafe;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.android.recreatesafe.adapters.AuthenticationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private TextView mHelloTextView;
    private NumberPicker np_age;
    private Button b_submit;
    private RadioGroup rg_sex;
    private EditText et_username;
    private DatabaseReference myRefUser;
    private String userId;
    private String age;
    private String sex;
    private String username;

    private AuthenticationAdapter mAuthenticationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userId = getIntent().getStringExtra(getResources().getString(R.string.extra_key_user_id));
        sex = "M";



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuthenticationAdapter = new AuthenticationAdapter(this, this);

        mHelloTextView = (TextView) findViewById(R.id.tv_hello);
        np_age = (NumberPicker) findViewById(R.id.np_age);
        b_submit = (Button) findViewById(R.id.b_submit);
        rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
        et_username = (EditText) findViewById(R.id.et_username);

        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(radioButtonId);
                sex = rb.getText().toString();
                if(sex.equals("Female")){sex="F";}
                if(sex.equals("Male")){sex="M";}


            }
        });

        String[] values = new String[102];
        for (int i =0;i<102;i++)
        {
            String si = String.valueOf(i);
            values[i]=si;
            if(i==101){

                values[i]=">100";
            }

        }


        np_age.setMaxValue(values.length-1);
        np_age.setMinValue(0);
        np_age.setDisplayedValues(values);
        np_age.setFocusable(true);
        np_age.setFocusableInTouchMode(true);
        np_age.setValue(20);

        b_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer number;
                number = np_age.getValue();
                age = number.toString();

                username = et_username.getText().toString();

                System.out.println("username:"+username);
                System.out.println("age: "+age);
                System.out.println("sex: "+sex);

                myRefUser = FirebaseDatabase.getInstance().getReference("User");
                Map<String, String> post = new HashMap<String, String>();
                post.put("Username",username);
                post.put("Sex", sex);
                post.put("Age", age);
                myRefUser.child(userId).setValue(post);
                setResult(Activity.RESULT_OK);

                finish();

            }
        });



//        myRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });



    }


    @Override
    protected void onStart() {
        super.onStart();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:{
                finish();
                return true;
            }
            case R.id.action_logout:{
                mAuthenticationAdapter.logout(this);

                return true;
            }




        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser() == null){
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
