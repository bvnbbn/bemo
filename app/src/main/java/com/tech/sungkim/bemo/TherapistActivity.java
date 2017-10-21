package com.tech.sungkim.bemo;


import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tech.sungkim.model.Provider;
import com.tech.sungkim.model.User;
import com.tech.sungkim.util.DialogStatic;
import com.tech.sungkim.util.PathFirebase;


import static android.R.id.text1;
import static com.tech.sungkim.bemo.R.id.imageView;
import static com.tech.sungkim.bemo.R.id.textView;

public class TherapistActivity extends AppCompatActivity
{

    private TextView mTextView;
    private Button mPayButton;
    private String conversationStarterId;
    private static final String TAG = "bemo";
    private DatabaseReference fbDatabaseReference;
    private String mCounsellorId;
    private Provider mProvider;
    private DialogStatic mDialogStatic;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapist);
        fbDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mCounsellorId = getIntent().getExtras().getString("counsellor_id");
        mDialogStatic = new DialogStatic(this, "", "getting info");
        mDialogStatic.show();
        mPayButton = (Button)findViewById(R.id.pay_button);
        mPayButton.setEnabled(false);
        fbDatabaseReference.child("users").child(mCounsellorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                mProvider = dataSnapshot.getValue(Provider.class);
                Log.i(TAG, "onDataChange: " + "provider added!");
                String text = mProvider.getWelcomeText();

                Log.d(TAG,"url of the image"+ mProvider.getWelcomeText());

                mTextView = (TextView)findViewById(R.id.therapist_intro_text);
                String text1 = text;
                int lastIndex= text1.length();
                String addToText = text1 + " " + "LEARN MORE";
                final SpannableStringBuilder sb = new SpannableStringBuilder(addToText);
                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);

                ClickableSpan clickableSpan = new ClickableSpan()
                {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(TherapistActivity.this, TherapistDetail.class)
                                .putExtra("counsellor_id", mCounsellorId));
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {// override updateDrawState
                        ds.setUnderlineText(false); // set to false to remove underline
                    }
                };
                sb.setSpan(bss, (lastIndex + 1), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sb.setSpan(clickableSpan, (lastIndex + 1), sb.length()
                        , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(TherapistActivity.this, R.color.teal)), (lastIndex + 1), sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTextView.setText(sb);
                mTextView.setMovementMethod(LinkMovementMethod.getInstance());
                ImageView imageView = (ImageView) findViewById(R.id.therapist_image);

                Log.d(TAG,"url of the image"+ mProvider.getimage());

                Glide.with(TherapistActivity.this)
                        .load(mProvider.getimage())
                        .centerCrop()
                        .into(imageView);
                TextView textview = (TextView) findViewById(R.id.side_note_text);
                String xString = textview.getText().toString();
                String xName = xString.split(" ")[0];
                String completeText = xString.replaceAll(xName, mProvider.getFullName().split(" ")[0]);
                textview.setText(completeText);
                String therapistName = mProvider.getFullName();
                TextView textView = (TextView) findViewById(R.id.therapist_name);
                textView.setText(therapistName);
                mDialogStatic.dissmis();
                mPayButton.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
        SpannableString s = new SpannableString("Bemo Therapist");
        s.setSpan(new TypefaceSpan("sans-serif-light"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mPayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                connectToCounsellor();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void expandTextView(TextView tv)
    {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", tv.getLineCount());
        animation.setDuration(200).start();
    }

    private void collapseTextView(TextView tv, int numLines)
    {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", numLines);
        animation.setDuration(200).start();
    }
    public void connectToCounsellor()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                        public void onDataChange(DataSnapshot snapshot)
                        {
                            System.out.println(snapshot.getValue());
                            if((snapshot.getValue() != null)&&!(snapshot.getValue()).equals(""))
                            {
                                conversationStarterId = snapshot.getValue().toString();
                                Log.i(TAG, "onDataChange: " + conversationStarterId);
                                startActivity(new Intent(TherapistActivity.this, ChatView.class)
                                        .putExtra("conversation", conversationStarterId)
                                        .putExtra("provider", mProvider)
                                        .putExtra("session_expired", false));

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
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
}