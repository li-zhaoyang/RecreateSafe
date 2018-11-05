package com.example.android.recreatesafe.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;



import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


/**
 * Created by lizha on 10/26/2017.
 */

public class AuthenticationAdapter{
    private static final int RC_SIGN_IN = 250;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String ANONYMOUS = "Nobody";
//    private String mUsername ;
    //private Activity mActivity;
//    private boolean mLogInState;
    private Context mContext;
//    private TextView mGreetingsTextView;

    public interface noUserListener{
        void startLogIn();
    }
    public AuthenticationAdapter(final Context context, FirebaseAuth.AuthStateListener listener){
        mContext = context;
        //mActivity = activity;
        mFirebaseAuth = FirebaseAuth.getInstance();
//        mGreetingsTextView = (TextView) ((Activity)context).findViewById(R.id.tv_greetings);
        mAuthStateListener = listener;
//
//        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = mFirebaseAuth.getCurrentUser();
//                if(user != null){
//                    onSignedInInitialize(user.getDisplayName());
//                    System.out.println("here555"+mUsername);
//                }else {
//                    //onSignedOutCleanUp();
//
//                    mUsername = ANONYMOUS;
//                    mLogInState = false;
//
//                    ((Activity)mContext).startActivityForResult(
//                            AuthUI.getInstance()
//                                    .createSignInIntentBuilder()
//                                    .setIsSmartLockEnabled(false)
//                                    .setAvailableProviders(
//                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
//                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
//                                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
//                                    .build(),
//                            RC_SIGN_IN);
//                }
//
//            }
//        };
    }

    public void onCreateMainActivity(){

    }

//    public void onSignedInInitialize(String username) {
//        // TODO check whether user setting exists
//        mLogInState = true;
//        mUsername = username;
//
//    }
//
//
//    public void onSignedOutCleanUp(){
//        mUsername = ANONYMOUS;
//        mLogInState = false;
//    }

    public void logout(final FragmentActivity fregmentActivity){
//        onSignedOutCleanUp();
        AuthUI.getInstance()
                .signOut(fregmentActivity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
//                        context.startActivity(new Intent(context, MainActivity.class));
                        // TODO is this right?
                        //((Activity) mContext).finish();
                    }
                });
    }

//    public void onActivityResult(int requestCode, int resultCode){
//        if(requestCode == RC_SIGN_IN){
//            if(resultCode == RESULT_OK){
//                Toast.makeText(((Activity)mContext), "You've signed in!",Toast.LENGTH_SHORT).show();
//                mContext.startActivity(new Intent(mContext, MainActivity.class));
//            }else if(resultCode == RESULT_CANCELED){
//                Toast.makeText(((Activity)mContext), "Sign in cancelled!", Toast.LENGTH_SHORT).show();
//                ((Activity)mContext).finish();
//            }
//        }
//
//    }

    public void addListener(){
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public void removeListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }





}
