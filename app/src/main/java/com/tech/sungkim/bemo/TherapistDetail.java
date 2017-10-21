package com.tech.sungkim.bemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tech.sungkim.model.Provider;
import com.tech.sungkim.util.DialogStatic;
import com.tech.sungkim.util.PathFirebase;
import com.tech.sungkim.util.UserDataHolder;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

import org.w3c.dom.Text;

import static android.R.attr.id;
import static com.tech.sungkim.bemo.R.id.imageView;

public class TherapistDetail extends AppCompatActivity {

    private String conversationStarterId;
    private static final String TAG = "bemo";
    private Button mPayButton;
    private DatabaseReference fbDatabaseReference;
    private String mCounsellorId;
    private Provider mProvider;
    private DialogStatic mDialogStatic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapist_detail);
        fbDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mCounsellorId = getIntent().getExtras().getString("counsellor_id");
        mPayButton = (Button)findViewById(R.id.pay_button);
        mPayButton.setEnabled(false);
        mDialogStatic = new DialogStatic(this, "", "getting info");
        mDialogStatic.show();
        fbDatabaseReference.child("users").child(mCounsellorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProvider = dataSnapshot.getValue(Provider.class);
                ((TextView)findViewById(R.id.full_name)).setText(mProvider.getFullName());
                JustifiedTextView explainerTextView = (JustifiedTextView) findViewById(R.id.score_explained_full);
                explainerTextView.setText(mProvider.getWelcomeText());
                TextView qualifyText = (TextView) findViewById(R.id.therapist_qualify);
                qualifyText.setText(mProvider.getQualification1() + "\n" + mProvider.getQualify_2());
                ImageView imageView = (ImageView) findViewById(R.id.therapist_image);
                Glide.with(TherapistDetail.this)
                        .load(mProvider.getimage())
                        .centerCrop()
                        .into(imageView);
                mPayButton.setEnabled(true);
                mDialogStatic.dissmis();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        SpannableString s = new SpannableString("Bemo Therapist");
        s.setSpan(new TypefaceSpan("sans-serif-light"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPayButton = (Button)findViewById(R.id.pay_button);
        mPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToCounsellor();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void connectToCounsellor(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserDataHolder hold = UserDataHolder.getInstance();
        ref.child("users").child(user.getUid()).child("episodes").child("0").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals("empty")) {
                    PathFirebase.setEpisodeKey((String) dataSnapshot.getValue());
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("episodes").child(PathFirebase.getEpisodeKey()).child("pay").setValue(0);
                    ref.child("episodes").child(PathFirebase.getEpisodeKey()).child("paid").setValue(true);
                    ref.child("episodes").child(PathFirebase.getEpisodeKey()).child("conversation_id").addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            System.out.println(snapshot.getValue());
                            if((snapshot.getValue() != null)&&!(snapshot.getValue()).equals("")) {
                                conversationStarterId = snapshot.getValue().toString();
                                //handleIntent();
                                Log.i(TAG, "onDataChange: " + conversationStarterId);
                                startActivity(new Intent(TherapistDetail.this, ChatView.class)
                                        .putExtra("conversation", conversationStarterId)
                                        .putExtra("provider",mProvider)
                                        .putExtra("session_expired", false));

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i(TAG, "onCancelled: " + databaseError.toString());
                        }


                    });
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, TherapistActivity.class)
                .putExtra("counsellor_id", mCounsellorId));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
