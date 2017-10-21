package com.tech.sungkim.bemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tech.sungkim.adapters.QuestionAdapter;
import com.tech.sungkim.model.SupportTeam;
import com.tech.sungkim.util.UserDataHolder;

import java.util.Arrays;
import java.util.List;

import static com.tech.sungkim.bemo.R.id.result;

public class FinalQuestionActivity extends AppCompatActivity
{

    private String mQuestionHolderText;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private  int ProgressStatus=100;
    private DatabaseReference pathFirebase;
    private SupportTeam supportTeam;
    private String TAG = FinalQuestionActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_template);
        SpannableString s = new SpannableString("Assessment");
        s.setSpan(new TypefaceSpan("sans-serif-light"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        //Attaching support member to every chat and user id in Participants node in episode id
        mAuth = FirebaseAuth.getInstance();
        pathFirebase = FirebaseDatabase.getInstance().getReference();

       /* if(mAuth.getCurrentUser()!=null) {
            pathFirebase.child("users").child(mAuth.getCurrentUser().getUid()).child("image").setValue(mAuth.getCurrentUser().getPhotoUrl());
            if(mAuth.getCurrentUser().getUid()!=null) {

                //  pathFirebase.child("users").child(mAuth.getCurrentUser().getUid()).child("image").setValue(mAuth.getCurrentUser().getPhotoUrl());
                connectUsertoSupportTeam();
                connectUsertoEpisode();

            }
        }*/


        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mQuestionHolderText = "Based on your selections, \n" +
                "how difficult have these made it for you to do your work, take care of things at home, or get along with other people?";
        final int result = getIntent().getExtras().getInt("result");
        TextView tv = (TextView) findViewById(R.id.question_text);
        ListView listview = (ListView) findViewById(R.id.answer_list);
        tv.setText(mQuestionHolderText);

        //progress bar
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setProgress(ProgressStatus);


        listview = (ListView)findViewById(R.id.answer_list);
        List<String> options = null;

        options = Arrays.asList(getResources().getStringArray(R.array.final_answer_options));
        QuestionAdapter adapter = new QuestionAdapter(options, this);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                SharedPreferences sharedPref = getSharedPreferences("questions", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                for(int j=1;j<8;j++)
                {
                    editor.putInt("question" + j, 500);
                }
                editor.commit();
                if(mAuth.getCurrentUser() == null)
                {
                    startActivity(new Intent(FinalQuestionActivity.this, ResultActivity.class)
                            .putExtra("result", result)
                            .putExtra("last_question_ans", i));
                }
                else
                {

                    DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("users")
                                .child(mAuth.getCurrentUser().getUid());
                    user.child("score").setValue(result);
                    user.child("AggregateQuestion").setValue(i);
                    user.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if(((long)dataSnapshot.child("score").getValue()) != 500 && dataSnapshot.child("AggregateQuestion").exists())
                            startActivity(new Intent(FinalQuestionActivity.this, ScoreCardActivity.class)
                                    .putExtra("result", result));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });

                }
            }
        });
    }











    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                startActivity(new Intent(this, QuestionActivity.class)
                        .putExtra("previous", true));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
