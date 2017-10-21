package com.tech.sungkim.bemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tech.sungkim.bus.BusProvider;
import com.tech.sungkim.model.Provider;
import com.tech.sungkim.util.DialogStatic;
import com.tech.sungkim.util.PathFirebase;
import com.tech.sungkim.util.UserDataHolder;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vikas on 6/6/17.
 */

public class Login_google_fb  extends AppCompatActivity implements View.OnClickListener
{

    private static final String TAG = "bemouser";
    private static final int RC_SIGN_IN = 1;

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
    private Button GooglesignInButton;
    private Button FbsignInButton;
    private FirebaseUser currentUser;
    private FirebaseUser user_fb;
    private String googleUser_Id;
    private int RC_SIGN_IN_LINK = 2;
    private AuthCredential credentialToLink;



    @Override
    protected  void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user);

        SpannableString s = new SpannableString("Log in");
        s.setSpan(new TypefaceSpan("sans-serif-light"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance

        getSupportActionBar().setTitle(s);
       // LoginManager.getInstance().logOut();


        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null)
                {
                   startActivity(new Intent(Login_google_fb.this,Precursor.class));

                }
            }
        };
        BusProvider.getInstance().register(this);
        initializationView();

        GooglesignInButton=(Button)findViewById(R.id.google_sign_in_button);
        //GooglesignInButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener()
                {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
                    {
                        Toast.makeText(Login_google_fb.this,"Google Sign in Failed",Toast.LENGTH_LONG).show();

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        // btnCreateAccount.setOnClickListener(this);
        GooglesignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signIn();
            }
        });

        FbsignInButton = (Button)findViewById(R.id.facebook_sign_in);
        //FbsignInButton.setOnClickListener(this);


        //Facebook login

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        Toast.makeText(Login_google_fb.this, "Authentication successful of facebook.",
                                Toast.LENGTH_SHORT).show();
                        Log.d("Success", "Login");
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        //handleFacebookAccessToken(loginResult.getAccessToken());
                        if (loginResult.getAccessToken() != null)
                        {
                            Log.v(TAG,loginResult.getAccessToken().toString());
                            handleFacebookAccessToken(loginResult.getAccessToken());

                        }

                    }

                    @Override
                    public void onCancel()
                    {
                        Toast.makeText(Login_google_fb.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        Toast.makeText(Login_google_fb.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        Button fbLoginButton = (Button) findViewById(R.id.facebook_sign_in);
        fbLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LoginManager.getInstance().logInWithReadPermissions(Login_google_fb.this, Arrays.asList("email","public_profile", "user_friends"));
            }
        });


    }

    //google sign in started from this method
    private void signIn()
    {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Toast.makeText(Login_google_fb.this, "Authentication in progresss in google sign in .",
                Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);
       // Toast.makeText(Login_google_fb.this, "Authentication in progress.",
         //       Toast.LENGTH_SHORT).show();

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            Toast.makeText(Login_google_fb.this, "Authentication in progress.",
                    Toast.LENGTH_SHORT).show();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
            {
                Toast.makeText(Login_google_fb.this, "Authentication in progress.",
                        Toast.LENGTH_SHORT).show();


                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else
            {
                Toast.makeText(Login_google_fb.this, "Authentication failed in ActivityResult.",
                        Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
        //facebook sign in
        if(requestCode == RC_SIGN_IN_LINK)
        {
            Toast.makeText(Login_google_fb.this, "Authentication in activity result of facebook.",
                    Toast.LENGTH_SHORT).show();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
            {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount account = result.getSignInAccount();
                final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                        {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task)
                                    {
                                        if (!task.isSuccessful())
                                        {

                                            Toast.makeText(Login_google_fb.this, "Authentication of Facebook  failed in ActivityResult.",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                        else
                                        {
                                            auth.getCurrentUser()
                                                    .linkWithCredential(credentialToLink)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                                                    {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {
                                                                user_fb = task.getResult().getUser();
                                                                loginToBemoDb();
                                                            }
                                                        }
                                                    });

                                        }
                                    }
                                });
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account)
    {
       // progressBar.setVisibility(View.VISIBLE);

        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            // updateUI(user);
                            //handleFirebaseAuthResult(user);
                            Toast.makeText(Login_google_fb.this, "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();
                            loginToBemoDb();

                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login_google_fb.this, "Authentication failed in Firebase.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            //handleFirebaseAuthResult(null);

                        }

                        // ...
                    }
                });

    }


    private void handleFacebookAccessToken(AccessToken token)
    {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                Log.d(TAG, "onComplete: facebook" + task);
                                if (!task.isSuccessful())
                                {
                                    credentialToLink = credential;
                                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                                    startActivityForResult(signInIntent, RC_SIGN_IN_LINK);
                                    Toast.makeText(Login_google_fb.this, "Authentication failed in Firebase.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    user_fb = task.getResult().getUser();
                                    loginToBemoDb();
                                    startActivity(new Intent(Login_google_fb.this,Precursor.class));

                                    Toast.makeText(Login_google_fb.this, "Authentication successful in Firebase.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );


    }


    public void loginToBemoDb()
    {
        dialogStatic.show();
        if (user_fb != null)
        {
            dialogStatic.show();
            pathFirebase.child(PathFirebase.PATH_USER).child(user_fb.getUid()).child("score")
                    .addListenerForSingleValueEvent(valueEventListener);
        }
    }


    /*private void handleFirebaseAuthResult(FirebaseUser user)
    {
       // progressBar.setVisibility(View.GONE);

        if(user!=null)
        {
            Log.d(TAG,"handleFirebaseAuthResult:Success");
            Map<String,Object> user_details= new HashMap<>();
            user_details.put("displayName",user.getDisplayName()!=null? user.getDisplayName():"Anonymous");
            user_details.put("imageurl",user.getPhotoUrl()!=null? user.getPhotoUrl().toString():"null");
            googleUser_Id=user.getUid();
            if( googleUser_Id!=null)
            {
                mFirebaseDatabase.child( googleUser_Id).setValue(user_details);

            }

        }
        else
        {
            Toast.makeText(Login_google_fb.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }



    }*/



    private void initializationView()
    {
        final SpannableStringBuilder sb = new SpannableStringBuilder("By continuing, you agree to Terms & Conditions");
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        dialogStatic = new DialogStatic(this, "", "Please wait!");
        ClickableSpan clickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(Login_google_fb.this, "Terms and conditions", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds)
            {// override updateDrawState
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




    @Override
    public void onClick(View view)
    {

    }


    @Override
    protected void onStart()
    {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }
    @Override
    public void onStop()
    {
        super.onStop();
        if (authListener != null)
        {
            auth.removeAuthStateListener(authListener);
        }
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    public void signOut()
    {
        auth.addAuthStateListener(authListener);
        auth.signOut();
    }


    private ValueEventListener valueEventListener = new ValueEventListener()
    {
        @Override
        public void onDataChange(final DataSnapshot userData)
        {

            if (userData.exists() && ((Long)userData.getValue()) != 500){
                // login

                ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dialogStatic != null)dialogStatic.dissmis();
                            Log.d(TAG, "onDataChange: " + "inside episode value listener " + dataSnapshot.getValue().toString());

                            pathFirebase.child("episodes")
                                    .child(dataSnapshot.getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.child("selected").exists()){

                                                if(!((Boolean)dataSnapshot.child("paid").getValue())) {
                                                    startActivity(new Intent(Login_google_fb.this, TherapistActivity.class)
                                                            .putExtra("counsellor_id", dataSnapshot.child("selected").getValue().toString()));
                                                }else{
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
                                                                                    if(conversationSnapShot.exists()){
                                                                                        long sessionTime = (long)conversationSnapShot.getValue();
                                                                                        if(new Date().getTime() > sessionTime){
                                                                                            startActivity(new Intent(Login_google_fb.this, ChatView.class)
                                                                                                    .putExtra("provider", provider)
                                                                                                    .putExtra("conversation" , dataSnapshot.child("conversation_id").getValue().toString())
                                                                                                    .putExtra("session_ended",true));
                                                                                        }else{
                                                                                            startActivity(new Intent(Login_google_fb.this, ChatView.class)
                                                                                                    .putExtra("provider", provider)
                                                                                                    .putExtra("conversation" , dataSnapshot.child("conversation_id").getValue().toString())
                                                                                                    .putExtra("session_ended",false));
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
                                            }else{
                                                long score = (Long) userData.getValue();
                                                startActivity(new Intent(Login_google_fb.this, ScoreCardActivity.class)
                                                        .putExtra("result", (int)score));
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

                    }
                };
                pathFirebase.child(PathFirebase.PATH_USER)
                        .child(user_fb.getUid()).child("episodes").child("0").addValueEventListener(listener);

            }
            else
            {
                if(dialogStatic != null)dialogStatic.dissmis();
                // this is a new user!
                String username = user_fb.getDisplayName();
                String email = user_fb.getEmail();
                String uIdString = user_fb.getUid();
                UserDataHolder holder = UserDataHolder.getInstance();
                holder.setmName(username);
                holder.setmEmail(email);
                holder.setmUserId(uIdString);
                DatabaseReference user = pathFirebase.child("users").child(uIdString);
                user.child("counsellor").setValue(false);
                startActivity(new Intent(Login_google_fb.this, Precursor.class));
            }



        }

        @Override
        public void onCancelled(DatabaseError databaseError)
        {
            if (dialogStatic!=null) dialogStatic.dissmis();
            Toast.makeText(Login_google_fb.this, "Failed", Toast.LENGTH_SHORT).show();
            signOut();
        }
    };
}
