package com.tech.sungkim.bemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.koushikdutta.async.parser.JSONObjectParser;
import com.squareup.otto.Subscribe;
import com.tech.sungkim.bus.BusProvider;
import com.tech.sungkim.bus.CloseActivity;
import com.tech.sungkim.dataManager.UserManager;
import com.tech.sungkim.model.Provider;
import com.tech.sungkim.model.SupportTeam;
import com.tech.sungkim.model.User;
import com.tech.sungkim.util.Config;
import com.tech.sungkim.util.DialogStatic;
import com.tech.sungkim.util.PathFirebase;
import com.tech.sungkim.util.StaticMethods;
import com.tech.sungkim.util.UserDataHolder;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.R.attr.data;
import static android.R.attr.dial;
import static android.R.attr.logoDescription;
import static android.R.attr.start;
import static com.facebook.internal.CallbackManagerImpl.RequestCodeOffset.Login;
import static com.google.android.gms.tasks.Tasks.await;
import static com.tech.sungkim.bemo.R.id.image;
import static com.tech.sungkim.bemo.R.id.result;

/**
 * Created by jacob on 23/11/2016.
 */

public class LoginUserAct extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "bemouser";


    public LinearLayout layout_login_user;
    public TextView txt_haveaccount;

    private FirebaseAuth auth;
    DialogStatic dialogStatic;
    private DatabaseReference pathFirebase;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuth;

    private CallbackManager mCallbackManager;
    private String conversationId;
    private GoogleApiClient mGoogleApiClient;
    private Button signInButton;
    private FirebaseUser currentUser;
    private FirebaseUser user_fb;
    private int RC_SIGN_IN_LINK = 1501;
    private AuthCredential credentialToLink;
    private SupportTeam supportTeam;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_user);
        SpannableString s = new SpannableString("Log in");
        s.setSpan(new TypefaceSpan("sans-serif-light"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance

        getSupportActionBar().setTitle(s);
       // LoginManager.getInstance().logOut();
        auth = FirebaseAuth.getInstance();
        pathFirebase = FirebaseDatabase.getInstance().getReference();

        BusProvider.getInstance().register(this);
        initializationView();
         /*LoginManager.getInstance().logOut();*/
        signInButton = (Button) findViewById(R.id.google_sign_in_button);


        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();





        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        Toast.makeText(LoginUserAct.this, " Fb Authentication in progress.",
                                Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        if (loginResult.getAccessToken() != null) {
                            Log.v(TAG, loginResult.getAccessToken().toString());
                            handleFacebookAccessToken(loginResult.getAccessToken());

                        }

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginUserAct.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginUserAct.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        Button fbLoginButton = (Button) findViewById(R.id.facebook_sign_in);
        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginUserAct.this, Arrays.asList("email", "public_profile", "user_friends"));
            }
        });
        signInButton.setOnClickListener(this);
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //startActivity(new Intent(LoginUserAct.this,Precursor.class));

                }
            }
        };

    }

    private void initializationView() {
        final SpannableStringBuilder sb = new SpannableStringBuilder("By continuing, you agree to Terms & Conditions");
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        dialogStatic = new DialogStatic(this, "", "Please wait!");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginUserAct.this, "Terms and conditions", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {// override updateDrawState
                ds.setUnderlineText(false); // set to false to remove underline
            }
        };
        sb.setSpan(bss, 28, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(clickableSpan, 28, sb.length()
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = (TextView) findViewById(R.id.textView11);
        textView.setText(sb);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }



    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "onComplete: facebook" + task);
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginUserAct.this, " Fb Authentication Failed." +task.getException(),
                                    Toast.LENGTH_LONG).show();
                            credentialToLink = credential;
                            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                            startActivityForResult(signInIntent, RC_SIGN_IN_LINK);
                        } else {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginUserAct.this, " Fb Authentication successful.",
                                        Toast.LENGTH_SHORT).show();
                                user_fb = task.getResult().getUser();
                                UpdateUI();

                              /*  auth.getCurrentUser().linkWithCredential(credential)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {

                                                }
                                                else
                                                {
                                                    Toast.makeText(LoginUserAct.this, " Fb Link Credit  unsuccessful.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });*/
                                //   user_fb = task.getResult().getUser();
                                // UpdateUI();
                            }
                        }
                    }
                });
    }


    /*private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            Toast.makeText(LoginUserAct.this,"Entered in the handle sign result",Toast.LENGTH_LONG).show();
            // Signed in successfully
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        }

        else
        {
            Toast.makeText(LoginUserAct.this," google Login failed",Toast.LENGTH_LONG).show();
            Log.e(TAG,"Login failed");
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
       /* if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                final GoogleSignInAccount account = result.getSignInAccount();
                final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginUserAct.this,"failed authentication"+ task.getException(),Toast.LENGTH_SHORT);

                                } else {
                                    auth.getCurrentUser().linkWithCredential(credential)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        user_fb = task.getResult().getUser();
                                                        UpdateUI();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(LoginUserAct.this,"link credetial failed authentication"+ task.getException(),Toast.LENGTH_SHORT);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        }*/
          if (requestCode == RC_SIGN_IN_LINK) {
            GoogleSignInResult result1 = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result1.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount account = result1.getSignInAccount();
                final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {

                                } else {
                                    user_fb = task.getResult().getUser();
                                    UpdateUI();
                                   /* auth.getCurrentUser().linkWithCredential(credentialToLink)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        user_fb = task.getResult().getUser();
                                                        UpdateUI();
                                                    }
                                                }
                                            });*/
                                }
                            }
                        });
            }
        }
        else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }








    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());



        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        credentialToLink = credential;
        Log.e(TAG,credential.toString());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            user_fb = task.getResult().getUser();
                            UpdateUI();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Log.e(TAG,user.getDisplayName());
                            auth.getCurrentUser().linkWithCredential(credential)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginUserAct.this,"Credit to link of google successful",Toast.LENGTH_LONG).show();
                                                user_fb = task.getResult().getUser();
                                                UpdateUI();
                                            }
                                            else if(!task.isSuccessful())
                                            {
                                                Toast.makeText(LoginUserAct.this,"Credit to link of google failed"+task.getException(),Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });




                           // UpdateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "GooglesignIn:failure", task.getException());
                            Toast.makeText(LoginUserAct.this, "Google Authentication failed."+task.getException(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                }
                );

    }

   
    public void linkAccount(final AuthCredential credential) {

        auth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (credential.getProvider().equals("facebook")) {
                                Log.d(TAG, "onComplete: " + "it's facebook!");
                                pathFirebase.child(PathFirebase.PATH_USER).child(user_fb.getUid()).child("name")
                                        .setValue(task.getResult().getUser().getDisplayName());
                                pathFirebase.child(PathFirebase.PATH_USER).child(user_fb.getUid()).child("photo")
                                        .setValue(task.getResult().getUser().getPhotoUrl());
                                user_fb = task.getResult().getUser();
                            } else {
                                Log.w(TAG, "linkWithCredential:failure", task.getException());
                                Toast.makeText(LoginUserAct.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                user_fb = auth.getCurrentUser();
                            }
                            UpdateUI();
                        } else {
                            Log.d(TAG, "onComplete: " + "task is " + task.isSuccessful() + "because " + task.getException().getMessage());
                            user_fb = auth.getCurrentUser();
                            UpdateUI();
                        }
                    }
                });

    }

    public void UpdateUI() {
        dialogStatic.show();
        pathFirebase.child(PathFirebase.PATH_USER).child(user_fb.getUid()).child("score")
                .addListenerForSingleValueEvent(valueEventListener);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                signIn();

        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void FinishActivity(CloseActivity close) {
        if (close.close) LoginUserAct.this.finish();
    }


    public void signOut() {
        auth.addAuthStateListener(authListener);
        auth.signOut();
    }



    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }



    //if user already entered into the app and also logged in and if user has reached
    //chat view then user will be directly taken to the chat view and if user has reached score activity
    //then the user will be taken to the score activity.
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot userData) {

            if (userData.exists() && ((Long) userData.getValue()) != 500) {
                // login
                ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dialogStatic != null) dialogStatic.dissmis();
                            Log.d(TAG, "onDataChange: " + "inside episode value listener " + dataSnapshot.getValue().toString());

                            pathFirebase.child("episodes")
                                    .child(dataSnapshot.getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("selected").exists()) {

                                                if (!((Boolean) dataSnapshot.child("paid").getValue())) {
                                                    startActivity(new Intent(LoginUserAct.this, TherapistActivity.class)
                                                            .putExtra("counsellor_id", dataSnapshot.child("selected").getValue().toString()));
                                                } else {
                                                    pathFirebase.child("users").child(dataSnapshot.child("selected").getValue().toString())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot snapShot) {
                                                                    final Provider provider = snapShot.getValue(Provider.class);
                                                                    pathFirebase.child("conversation")
                                                                            .child(dataSnapshot.child("conversation_id").getValue().toString())
                                                                            .child("end_time")
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot conversationSnapShot) {
                                                                                    if (conversationSnapShot.exists()) {
                                                                                        long sessionTime = (long) conversationSnapShot.getValue();
                                                                                        if (new Date().getTime() > sessionTime) {
                                                                                            Log.d(TAG,"end time greater");
                                                                                            startActivity(new Intent(LoginUserAct.this, ChatView.class)
                                                                                                    .putExtra("provider", provider)
                                                                                                    .putExtra("conversation", dataSnapshot.child("conversation_id").getValue().toString())
                                                                                                    .putExtra("session_ended", true));
                                                                                        } else {
                                                                                            Log.d(TAG,"end time not greater");
                                                                                            startActivity(new Intent(LoginUserAct.this, ChatView.class)
                                                                                                    .putExtra("provider", provider)
                                                                                                    .putExtra("conversation", dataSnapshot.child("conversation_id").getValue().toString())
                                                                                                    .putExtra("session_ended", false));
                                                                                        }
                                                                                    }
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
                                            } else {
                                                long score = (Long) userData.getValue();
                                                startActivity(new Intent(LoginUserAct.this, ScoreCardActivity.class)
                                                        .putExtra("result", (int) score));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.e(TAG,"Error occured while taking data from database");

                    }
                };
                //if the user has uninstalled the app and again installed it so at the time of login new
                //fcm_id will be updated in the database.
                displayFirebaseRegId();

                pathFirebase.child(PathFirebase.PATH_USER)
                        .child(user_fb.getUid()).child("episodes").child("0").addValueEventListener(listener);

            }
            // if there is new user
            else
            {
                if (dialogStatic != null) dialogStatic.dissmis();
                // this is a new user!
                String username = user_fb.getDisplayName() != null ? user_fb.getDisplayName() : "Anonymous";
                String email = user_fb.getEmail();
                String uIdString = user_fb.getUid();
                String image_url = String.valueOf(user_fb.getPhotoUrl() != null ? user_fb.getPhotoUrl().toString() : null);
                Log.e(TAG,image_url);

                //adding FCM_id to the database
                displayFirebaseRegId();

                //connect user to the support team member id
                //  connectUsertoSupportTeam();

                UserDataHolder holder = UserDataHolder.getInstance();
                holder.setmName(username);
                holder.setmEmail(email);
                holder.setmUserId(uIdString);
                DatabaseReference user = pathFirebase.child("users").child(uIdString);
                user.child("counsellor").setValue(false);
                user.child("Support").setValue(false);
                user.child("Name").setValue(username);
                user.child("Image_Url").setValue(image_url);
                startActivity(new Intent(LoginUserAct.this, Precursor.class));

            }
        }

        // to register FCM Id to databse
        private void displayFirebaseRegId()
        {
            SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
            String regId = pref.getString("regId", null);


            Log.e(TAG, "Firebase reg id: " + regId);

            if (!TextUtils.isEmpty(regId) && user_fb != null) {

                Log.e(TAG, "Firebase database updated reg id: " + regId);

                pathFirebase.child("users").child(user_fb.getUid()).child("Fcm_id").setValue(regId);

            } else {
                Log.e(TAG, "Firebase database not updated reg id: " + regId);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            if (dialogStatic != null) dialogStatic.dissmis();
            Toast.makeText(LoginUserAct.this, "Failed", Toast.LENGTH_SHORT).show();
            signOut();
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(LoginUserAct.this,"Unable to connect",Toast.LENGTH_SHORT).show();

    }
}
