package com.tech.sungkim.bemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tech.sungkim.model.SupportTeam;

public class ScoreCardActivity extends AppCompatActivity
{

    private WebView mWebView;
    private Button mButton;
    private FirebaseAuth mAuth;
    private int score;
    private DatabaseReference pathFirebase;
    private SupportTeam supportTeam;
    private TextView score_text1;
    private String score_mild=" Your score indicates that you are feeling a few symptoms of anxiety";
    private String score_moderate=" Your score indicates that you are feeling many symptoms of anxiety.";
    private String score_severe=" Your score indicates that you are feeling many symptoms of anxiety.";
    private String TAG = ScoreCardActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_card);
        TextView view = (TextView) findViewById(R.id.score_val);
        //ImageView score_text=(ImageView)findViewById(R.id.score_text);
        score = getIntent().getExtras().getInt("result");
        view.setText(String.valueOf(score));





        // now working on firebase user  if the user has logged in
        // then user name will appear else not in the  score text
        mAuth=FirebaseAuth.getInstance();
        score_text1=(TextView)findViewById(R.id.text1);
        if(mAuth.getCurrentUser()!= null) {
            if (mAuth.getCurrentUser().getDisplayName() != null) {


                score_mild = "Hi " + mAuth.getCurrentUser().getDisplayName().concat(",").concat(score_mild);
                score_moderate = "Hi " + mAuth.getCurrentUser().getDisplayName().concat(score_moderate);
                score_severe += "Hi " + mAuth.getCurrentUser().getDisplayName().concat(score_severe);

            }
        }
        else
        {
            //do nothing to the text which is to be displayed
        }
        //now displaying text according to score
        if(score > 0 && score < 9)
        {
            score_text1.setText(score_mild);
        }
        else if(score > 9 && score < 14 )
        {
            score_text1.setText(score_moderate);
        }
        else if(score > 14 )
        {
            score_text1.setText(score_severe);
        }

        pathFirebase = FirebaseDatabase.getInstance().getReference();

      if(mAuth.getCurrentUser()!=null) {
            if(mAuth.getCurrentUser().getUid()!=null) {


                //attaching user id under the Participants node under  episode id
                pathFirebase.child("users").child(mAuth.getCurrentUser().getUid()).child("episodes").child("0").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String episodeId = dataSnapshot.getValue().toString();
                        pathFirebase.child("episodes").child(episodeId).child("Participants").child("User_id").setValue(mAuth.getCurrentUser().getUid());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.e(TAG, "Firebase user not attached: ");
                    }
                });

            }
        }


        SpannableString s = new SpannableString("My score");
        s.setSpan(new TypefaceSpan("sans-serif-light"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);

        mButton = (Button) findViewById(R.id.trial_btn);

        mButton.setOnClickListener(new View.OnClickListener()
        {
                    @Override
                    public void onClick(View view)
                    {
                        startActivity(new Intent(ScoreCardActivity.this,PlanActivity.class));

                    }
        });
    }





    }




