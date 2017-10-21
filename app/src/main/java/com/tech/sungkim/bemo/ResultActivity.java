package com.tech.sungkim.bemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tech.sungkim.model.Provider;
import com.tech.sungkim.util.DialogStatic;
import com.tech.sungkim.util.PathFirebase;
import com.tech.sungkim.util.StaticMethods;
import com.tech.sungkim.util.UserDataHolder;

import java.util.Arrays;

import static com.tech.sungkim.bemo.R.id.result;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 130;
    private static final String TAG = "bemo";
    private TextView mResultHolder;
    private GoogleApiClient mGoogleApiClient;
    private DialogStatic mDialogStatic;
    private String email; //email_register.getText().toString().trim();
    private String password;// password_register.getText().toString().trim();
    private String username;
    private FirebaseAuth auth;
    private DatabaseReference pathFirebase;
    private FirebaseDatabase pathDatabase;
    private Toolbar toolbar;
    private Button mFBSignUpButton;

    private int result;
    private String uIdString;
    private String episodeKey;
    private CallbackManager mCallbackManager;

    private int last_question_ans;
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot userData) {
            if (userData.exists() && ((Long)userData.getValue()) != 500){
                // login

                ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Log.d(TAG, "onDataChange: " + "inside episode value listener " + dataSnapshot.getValue().toString());

                            pathFirebase.child("episodes")
                                    .child(dataSnapshot.getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.child("selected").exists()){

                                                if(!((Boolean)dataSnapshot.child("paid").getValue())) {
                                                    startActivity(new Intent(ResultActivity.this, TherapistActivity.class)
                                                            .putExtra("counsellor_id", dataSnapshot.child("selected").getValue().toString()));
                                                }else{
                                                    pathFirebase.child("users").child(dataSnapshot.child("selected").getValue().toString())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot snapShot) {
                                                                    Provider provider = snapShot.getValue(Provider.class);
                                                                    startActivity(new Intent(ResultActivity.this, ChatView.class)
                                                                            .putExtra("provider",provider)
                                                                            .putExtra("conversation" , dataSnapshot.child("conversation_id").getValue().toString()));
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });


                                                }
                                            }else{
                                                long score = (Long) userData.getValue();
                                                startActivity(new Intent(ResultActivity.this, ScoreCardActivity.class)
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
                        .child(firebaseUser.getUid()).child("episodes").child("0").addValueEventListener(listener);

            }else{
                // this is a new user!
                UserDataHolder holder = UserDataHolder.getInstance();
                holder.setmUserId(firebaseUser.getUid());
                DatabaseReference user = pathFirebase.child("users").child(firebaseUser.getUid());
                user.child("name").setValue(username);
                user.child("counsellor").setValue(false);
                user.child("score").setValue(result);
                user.child("AggregateQuestion").setValue(last_question_ans);
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(((long)dataSnapshot.child("score").getValue()) != 500 && dataSnapshot.child("AggregateQuestion").exists()) {
                            startActivity(new Intent(ResultActivity.this, ScoreCardActivity.class)
                                    .putExtra("result", result));
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
            Toast.makeText(ResultActivity.this, "Please try again", Toast.LENGTH_SHORT).show();

        }
    };
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getIntent().getExtras().getInt("last_question_ans");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString s = new SpannableString("Sign up");
        s.setSpan(new TypefaceSpan("sans-serif-light"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);

        pathDatabase = FirebaseDatabase.getInstance();
        pathFirebase = pathDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        LoginManager.getInstance().logOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_id_token))
                .requestProfile()
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        init();
        result = (int) getIntent().getExtras().get("result");
        mResultHolder.setText(String.valueOf(result));

    }

    public void init() {

        mResultHolder = (TextView) findViewById(R.id.result);
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Log.d("Success", "Login");
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);

                        if (loginResult.getAccessToken() != null) {
                            Log.v(TAG,loginResult.getAccessToken().toString());
                            handleFacebookAccessToken(loginResult.getAccessToken());

                        }

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(ResultActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(ResultActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        Button fbLoginButton = (Button) findViewById(R.id.facebook_sign_in);
        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(ResultActivity.this, Arrays.asList("public_profile", "user_friends"));
            }
        });
        final SpannableStringBuilder sb = new SpannableStringBuilder("By continuing, you agree to Terms & Conditions");
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ResultActivity.this, "Terms and conditions", Toast.LENGTH_SHORT).show();
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
        textView.setHighlightColor(R.color.black);


    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            StaticMethods.showErrorMessageToUser(task.getException().getMessage(),
                                    ResultActivity.this);
                            LoginManager.getInstance().logOut();

                        } else {
                            firebaseUser = task.getResult().getUser();
                            handleSignUporLogin();
                        }

                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }


    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());

                            StaticMethods.showErrorMessageToUser(task.getException().getMessage(),
                                    ResultActivity.this);
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {

                                        }
                                    });
                        } else {
                            firebaseUser = task.getResult().getUser();
                            handleSignUporLogin();
                        }

                    }
                });
    }

        private void handleSignUporLogin(){
            pathFirebase.child(PathFirebase.PATH_USER).child(firebaseUser.getUid()).child("score").addListenerForSingleValueEvent(valueEventListener);


    }

}
