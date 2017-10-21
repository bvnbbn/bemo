package com.tech.sungkim.bemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tech.sungkim.model.Provider;
import com.tech.sungkim.util.Config;
import com.tech.sungkim.util.PathFirebase;
import com.tech.sungkim.util.StaticMethods;

import java.util.Date;

import static android.R.attr.path;

public class SplashActivity extends AppCompatActivity
{

    private static final String TAG = "bemo";
    private FirebaseAuth mAuth;
    private DatabaseReference pathFirebase;
    private FirebaseUser user_fb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pathFirebase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

  //     displayFirebaseRegId();

        //checking if the internet is available or not
        if(StaticMethods.isInternetAvailable(this)){
            loadApp();
        }else{
            new AlertDialog.Builder(this).setIcon(R.drawable.support_icon)
                    .setMessage("Sorry, You have to be connected to the internet!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })

                    .show();
        }
    }


    public void loadApp(){
        //checking if the user is null or not
        if (mAuth.getCurrentUser() == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            }, 2000);
        }else{
            user_fb = FirebaseAuth.getInstance().getCurrentUser();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot userSnap) {
                    if(userSnap.exists()){
                        pathFirebase.child("episodes")
                                .child(userSnap.child("episodes").child("0").getValue().toString())

                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.child("selected").exists()){

                                            if(!((Boolean)dataSnapshot.child("paid").getValue())) {
                                                startActivity(new Intent(SplashActivity.this, TherapistActivity.class)
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
                                                                                        startActivity(new Intent(SplashActivity.this, ChatView.class)
                                                                                                .putExtra("provider", provider)
                                                                                                .putExtra("conversation" , dataSnapshot.child("conversation_id").getValue().toString())
                                                                                                .putExtra("session_ended",true));
                                                                                    }else{
                                                                                        startActivity(new Intent(SplashActivity.this, ChatView.class)
                                                                                                .putExtra("provider", provider)
                                                                                                .putExtra("conversation" , dataSnapshot.child("conversation_id").getValue().toString())
                                                                                                .putExtra("session_ended",false));
                                                                                    }
                                                                                }
                                                                            }
                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError)
                                                                            {

                                                                            }
                                                                        });
                                                                }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError)
                                                            {

                                                            }
                                                        });
                                            }
                                        }else
                                            {
                                            long score = (Long) userSnap.child("score").getValue();
                                            startActivity(new Intent(SplashActivity.this, ScoreCardActivity.class)
                                                    .putExtra("result", (int)score));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                     {

                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            };

            pathFirebase.child(PathFirebase.PATH_USER)
                    .child(user_fb.getUid())
                    .addListenerForSingleValueEvent(listener);

        }
    }


}
