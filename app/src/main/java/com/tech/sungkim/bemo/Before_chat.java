package com.tech.sungkim.bemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tech.sungkim.adapters.QuestionAdapter;
import com.tech.sungkim.model.Question;
import com.tech.sungkim.util.DialogStatic;
import com.tech.sungkim.util.PathFirebase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vikas on 18/7/17.
 */

public class Before_chat extends AppCompatActivity{


    public static Context context;
    private BeforeChat.MyAdapter mAdapter;
    private static DialogStatic mDialogStatic;
    private static int ProgressStatus = 0;
    private static String TAG = Before_chat.class.getSimpleName();
    private ProgressBar mProgressBar;
    private EditText name;
    private EditText age;
    private int Age=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question7_template);
        name=(EditText)findViewById(R.id.name);
        age=(EditText)findViewById(R.id.age);


        mDialogStatic = new DialogStatic(this, "Finding Bemo", "Loading");
        SpannableString s = new SpannableString("Finding bemo");
        s.setSpan(new TypefaceSpan("sans-serif-light"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listview = (ListView)findViewById(R.id.answer_list);
        List<String> options = null;
        options = Arrays.asList(getResources().getStringArray(R.array.Gender));
        QuestionAdapter adapter = new QuestionAdapter(options,getApplicationContext());
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String Name = name.getText().toString().trim();
                String AGE = age.getText().toString().trim();


                if(age.getText().toString().isEmpty())
                {
                    age.setError("Please enter your age");

                }
                if(Name.isEmpty())
                {
                    name.setError("Please Enter your name");

                }
                else if(!TextUtils.isDigitsOnly(age.getText().toString().trim()))
                {
                    age.setError("Please Enter the age in digits");

                }
                else if(!Name.isEmpty() && !AGE.isEmpty())
                {
                    Age = Integer.parseInt(age.getText().toString().trim());
                    if (Age < 0 || Age == 0)
                    {
                        age.setError("Please Enter the correct age");

                    }
                    else
                    {
                        HashMap<String, Object> answer_of_question7 = new HashMap();
                        answer_of_question7.put("Name", Name);
                        answer_of_question7.put("Age", Age);
                        answer_of_question7.put("Gender", i);
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
                        dbReference.child("users").child(user.getUid()).child("Question7").setValue(answer_of_question7);
                        mDialogStatic.show();
                        attachCounsellorId();
                    }
                }

            }
        });

    }

    public void attachCounsellorId()
    {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId != null)
        {

            final DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
            dbReference.child("users").child(userId).child("episodes").child("0")
                    .addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.getValue() != null)
                            {
                                String episodeId = (String) dataSnapshot.getValue();
                                if (!episodeId.equals("empty"))
                                {
                                    PathFirebase.setEpisodeKey(episodeId);
                                    dbReference.child("episodes").child(episodeId).child("questions_over").setValue(true);
                                    dbReference.child("episodes").child(episodeId).child("selected")
                                            .addValueEventListener(new ValueEventListener()
                                            {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot)
                                                {
                                                    String counsellorId = (String) dataSnapshot.getValue();
                                                    if (counsellorId != null && !counsellorId.equals(""))
                                                    {
                                                        mDialogStatic.dissmis();
                                                        startActivity(new Intent(getApplicationContext(), TherapistActivity.class)
                                                                .putExtra("counsellor_id", counsellorId));
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError)
                                                {

                                                }
                                            });
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });
        }
    }



}

