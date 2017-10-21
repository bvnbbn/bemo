package com.tech.sungkim.bemo;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class Precursor extends AppCompatActivity {


    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precursor);
        SpannableString s = new SpannableString("Assessment");
        s.setSpan(new TypefaceSpan("sans-serif-light"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance

        getSupportActionBar().setTitle(s);
        handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                startActivity(new Intent(Precursor.this, QuestionActivity.class)
                    .putExtra("previous", false));

            }

        },900);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacksAndMessages(null);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                handler.removeCallbacksAndMessages(null);
                startActivity(new Intent(this, MainActivity.class));
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    FirebaseAuth.getInstance().signOut();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
