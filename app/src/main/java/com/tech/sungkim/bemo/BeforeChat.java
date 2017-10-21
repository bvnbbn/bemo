package com.tech.sungkim.bemo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eftimoff.viewpagertransformers.RotateDownTransformer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tech.sungkim.adapters.CustomAdapter;
import com.tech.sungkim.adapters.QuestionAdapter;
import com.tech.sungkim.model.Question;
import com.tech.sungkim.util.DialogStatic;
import com.tech.sungkim.util.NonSwipeableViewPager;
import com.tech.sungkim.util.PathFirebase;
import com.tech.sungkim.util.UserDataHolder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.tech.sungkim.bemo.QuestionActivity.NUM_ITEMS;
import static com.tech.sungkim.bemo.QuestionActivity.mPager;
import static com.tech.sungkim.bemo.R.array.goals;
import static com.tech.sungkim.bemo.R.id.answer;
import static com.tech.sungkim.bemo.R.id.container;
import static com.tech.sungkim.bemo.R.id.progress;

public class BeforeChat extends AppCompatActivity
{
    public static Context context;
    public static int NUM_ITEMS = 7;
    private MyAdapter mAdapter;
    private int result;
    private static String results[];
    private static Question[] questions;
    static ViewPager mPager;
    private static DialogStatic mDialogStatic;
    private static int ProgressStatus = 0;
    private static String TAG = BeforeChat.class.getSimpleName();
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_before_chat);
        context=getApplicationContext();
        mDialogStatic = new DialogStatic(this, "Finding Bemo", "Loading");
        questions = new Question[8];
        SpannableString s = new SpannableString("Finding bemo");
        s.setSpan(new TypefaceSpan("sans-serif-light"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAdapter = new MyAdapter(getSupportFragmentManager());
        results = new String[8];
        mPager = (NonSwipeableViewPager) findViewById(R.id.view_pager);
        mPager.setOffscreenPageLimit(7);
        mPager.setAdapter(mAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        questions[0] = new Question("I spend most of my day at");
        questions[1] = new Question("I find some free time for myself");
        questions[2] = new Question("Select 3 most important areas where you would want to see most growth in life? ");
        questions[3] = new Question("Pick the traits that best describe your personality");
        questions[4] = new Question("Select your favourite hobbies from the list below");
        questions[5] = new Question("What do you identify with more?");
        questions[6] = new Question("One last step! Some basic details");





       /* mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {


            }

            @Override
            public void onPageSelected(int position)
            {
                position+=33;
                ProgressBar progress =(ProgressBar)findViewById(R.id.progress);
                progress.setProgress(position);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {


            }
        });*/
    }


    public static class MyAdapter extends FragmentStatePagerAdapter
    {


        public MyAdapter(FragmentManager fm)
        {
            super(fm);
        }


        @Override
        public int getCount()
        {
            return NUM_ITEMS;
        }


        @Override
        public Fragment getItem(int position)
        {
            if (position == 0)
            {
                return FirstQuestion.newInstance(questions[0].getmQuestionText());
            }

            else if (position == 1)
            {
                return SecondQuestion.newInstance(questions[1].getmQuestionText());
            }
            else if (position == 2)
            {
                return ThirdQuestion.newInstance(questions[2].getmQuestionText());
            }
            else if (position == 3)
            {
                return FourthQuestion.newInstance(questions[3].getmQuestionText());
            }
            else if (position == 4)
            {
                return FifthQuestion.newInstance(questions[4].getmQuestionText());
            }
            else if (position == 5)
            {
                return SixthQuestion.newInstance(questions[5].getmQuestionText());
            }
            else
            {
                return SeventhQuestion.newInstance(questions[6].getmQuestionText());
            }
        }
    }

    public static View populateView(LayoutInflater inflater, ViewGroup container, String question)
    {
        int progress_status = 0;
        View v = inflater.inflate(R.layout.question_template, container, false);
        ProgressBar progress = (ProgressBar) v.findViewById(R.id.progress_bar);
        progress.setProgress(progress_status);
        TextView tv = (TextView) v.findViewById(R.id.question_text);
        tv.setText(question);
        return v;
    }

    public static View populateView_question6(LayoutInflater inflater, ViewGroup container, String question)
    {
        int progress_status = 0;
        View v = inflater.inflate(R.layout.question6_template, container, false);
        ProgressBar progress = (ProgressBar) v.findViewById(R.id.progress_bar);
        progress.setProgress(progress_status);
        TextView tv = (TextView) v.findViewById(R.id.question_text);
        tv.setText(question);
        return v;
    }
    public static View populateView_question7(LayoutInflater inflater, ViewGroup container, String question)
    {
        int progress_status = 0;
        View v = inflater.inflate(R.layout.question7_template, container, false);
        ProgressBar progress = (ProgressBar) v.findViewById(R.id.progress_bar);
        progress.setProgress(progress_status);
        TextView tv = (TextView) v.findViewById(R.id.question_text);
        tv.setText(question);
        return v;
    }

    public static class FirstQuestion extends Fragment
    {
        ProgressBar mProgressBar;
        int mPosition;
        boolean mReady = false;
        boolean mQuiting = false;
        private ListView mAnswerListView;

        static Fragment newInstance(String question)
        {
            FirstQuestion f = new FirstQuestion();
            Bundle args = new Bundle();
            args.putString("question", question);
            f.setArguments(args);
            return f;

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
        {

            super.onCreateView(inflater, container, savedInstanceState);
            String mQuestionHolderText = getArguments().getString("question");
            View v = populateView(inflater, container, mQuestionHolderText);
            mAnswerListView = (ListView) v.findViewById(R.id.answer_list);
            List<String> options = null;
            options = Arrays.asList(getResources().getStringArray(R.array.places));
            QuestionAdapter adapter = new QuestionAdapter(options, getActivity());
            mAnswerListView.setAdapter(adapter);
            mAnswerListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
                    dbReference.child("users").child(user.getUid()).child("Question1").setValue(i);
                    mPager.setCurrentItem(1);
                    Log.d(TAG, "1st Que");
                }
            });
            return v;
        }

    }

    public static class SecondQuestion extends Fragment
    {
        FirstQuestion first_question;
        ProgressBar mProgressBar;

        private ListView mAnswerListView;
        private DatabaseReference dbReference;

        static Fragment newInstance(String question)
        {
            SecondQuestion f = new SecondQuestion();
            Bundle args = new Bundle();
            args.putString("question", question);
            f.setArguments(args);
            return f;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
        {
            super.onCreateView(inflater, container, savedInstanceState);
            String mQuestionHolderText = getArguments().getString("question");
            View v = populateView(inflater, container, mQuestionHolderText);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
            mProgressBar.setProgress(45);
            ListView listview = (ListView) v.findViewById(R.id.answer_list);
            List<String> options = null;
            options = Arrays.asList(getResources().getStringArray(R.array.time_of_day));
            QuestionAdapter adapter = new QuestionAdapter(options, getActivity());
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
                    dbReference.child("users").child(user.getUid()).child("Question2").setValue(i);
                    Log.d(TAG, "2st Que");
                    mPager.setCurrentItem(2);


                }
            });
            return v;
        }
    }


    public static class ThirdQuestion extends Fragment
    {
        ProgressBar mProgressBar;
        private GridView mAnswerListView;
        private String a;
        private int count =0;
        private HashMap<String,Object> options_for_third_questions = new HashMap<>();
        List<Integer> answers;
        private SparseBooleanArray checkedItemPositions;



        static Fragment newInstance(String question)
        {
            ThirdQuestion f = new ThirdQuestion();
            Bundle args = new Bundle();
            args.putString("question", question);
            f.setArguments(args);
            return f;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
        {
            super.onCreateView(inflater, container, savedInstanceState);
            String mQuestionHolderText = getArguments().getString("question");
            View v = populateView(inflater, container, mQuestionHolderText);
            mAnswerListView = (GridView) v.findViewById(R.id.grid_answer_list);
            mAnswerListView.setNumColumns(3);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
            mProgressBar.setProgress(60);
            List<String> options = null;


            options = Arrays.asList(getResources().getStringArray(goals));
            final QuestionAdapter adapter = new QuestionAdapter(options, getActivity());
            final CustomAdapter custom_adapter  = new CustomAdapter(options,getActivity());
            mAnswerListView.setAdapter(adapter);
            final List<String> finalOptions = options;
            mAnswerListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    answers=new ArrayList<Integer>();
                    count++;
                    //checkedItemPositions = new SparseBooleanArray();
                    checkedItemPositions= mAnswerListView.getCheckedItemPositions();
                    Log.d("Values",answers.toString());
                    Log.d("Items",checkedItemPositions.toString());
                    for(int k=0;k<checkedItemPositions.size();k++)
                    {
                        if(checkedItemPositions.valueAt(k))
                        {
                            answers.add(checkedItemPositions.keyAt(k));
                        }
                    }
                    Log.d("Values",answers.toString());
                    if (count == 3)
                    {
                        store_answers_of_third_questions(answers);
                        answers.clear();

                        Log.d("Values",answers.toString());

                    }

                    Log.d("count value", String.valueOf(count));
                    Log.d("Items",checkedItemPositions.toString());
                }
            });

            return v;
        }






        public void store_answers_of_third_questions(List<Integer>answer)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
            dbReference.child("users").child(user.getUid()).child("Question3").setValue(answer);
            mPager.setCurrentItem(3);
            Log.d(TAG, "3rd Que");
            count=0;

            /**
            *deleting all stored values in the sparse boolean array after selecting three
            values one time so that user can select another three values or choose different values if
            he wanted to
            **/
            for(int i=0;i<checkedItemPositions.size();i++)
            {
                checkedItemPositions.delete(i);
            }
            answers.clear();
            //answers= new ArrayList<>();
           // checkedItemPositions=new SparseBooleanArray();
            checkedItemPositions.clear();
           // Log.d("list size", String.valueOf(answers.size()));
            //Log.d("Items",checkedItemPositions.toString());

        }


    }

    public static class FourthQuestion extends Fragment
    {
        FirstQuestion first_question;
        ProgressBar mProgressBar;

        private ListView mAnswerListView;
        private DatabaseReference dbReference;

        static Fragment newInstance(String question)
        {
            FourthQuestion f = new FourthQuestion();
            Bundle args = new Bundle();
            args.putString("question", question);
            f.setArguments(args);
            return f;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
        {
            super.onCreateView(inflater, container, savedInstanceState);
            String mQuestionHolderText = getArguments().getString("question");
            View v = populateView(inflater, container, mQuestionHolderText);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
            mProgressBar.setProgress(80);
            ListView listview = (ListView) v.findViewById(R.id.answer_list);
            List<String> options = null;
            options = Arrays.asList(getResources().getStringArray(R.array.traits));
            QuestionAdapter adapter = new QuestionAdapter(options, getActivity());
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
                    dbReference.child("users").child(user.getUid()).child("Question4").setValue(i);

                    mPager.setCurrentItem(4);
                    Log.d(TAG, "4th Que");

                }
            });
            return v;
        }

    }

    public static class FifthQuestion extends Fragment
    {

        ProgressBar mProgressBar;

        private ListView mAnswerListView;
        private DatabaseReference dbReference;

        static Fragment newInstance(String question)
        {
            FifthQuestion f = new FifthQuestion();
            Bundle args = new Bundle();
            args.putString("question", question);
            f.setArguments(args);
            return f;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
        {
            super.onCreateView(inflater, container, savedInstanceState);
            String mQuestionHolderText = getArguments().getString("question");
            View v = populateView(inflater, container, mQuestionHolderText);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
            mProgressBar.setProgress(80);
            ListView listview = (ListView) v.findViewById(R.id.answer_list);
            List<String> options = null;
            options = Arrays.asList(getResources().getStringArray(R.array.sports));
            QuestionAdapter adapter = new QuestionAdapter(options, getActivity());
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
                    dbReference.child("users").child(user.getUid()).child("Question5").setValue(i);
                    Log.d(TAG, "5th Que");
                    mPager.setCurrentItem(5);


                }
            });
            return v;
        }

    }

    public static class SixthQuestion extends Fragment
    {

        ProgressBar mProgressBar;
        private TextView mbutton1;
        private TextView mbutton2;

       // private ListView mAnswerListView;
     //   private DatabaseReference dbReference;

        static Fragment newInstance(String question)
        {
            SixthQuestion f = new SixthQuestion();
            Bundle args = new Bundle();
            args.putString("question", question);
            f.setArguments(args);
            return f;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
        {
            super.onCreateView(inflater, container, savedInstanceState);
            String mQuestionHolderText = getArguments().getString("question");
            View v = populateView_question6(inflater, container, mQuestionHolderText);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
            mProgressBar.setProgress(90);

            mbutton1=(TextView) v.findViewById(R.id.identify_text1);
            mbutton2=(TextView) v.findViewById(R.id.identify_text2);
            mbutton1.setText("\n      I feel more myself-\n" +
                     "      “I am able to express my personality\n"+
                    "        without fear of judgement\" \n\n");

            mbutton2.setText( "\n      I am more outgoing-\n" +
                    "      “I feel less hesitation to make plans\n" +
                    "       with people and to strike random\n" +
                    "       conversations with strangers\" \n \n");

            mbutton1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
                    dbReference.child("users").child(user.getUid()).child("Question6").setValue(1);
                    mPager.setCurrentItem(6);

                }
            });
            mbutton2.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
                    dbReference.child("users").child(user.getUid()).child("Question6").setValue(2);
                    mPager.setCurrentItem(6);

                }
            });
            return v;
        }
    }


    public static class SeventhQuestion extends Fragment
    {
        ProgressBar mProgressBar;
        private EditText name;
        private EditText age;
        private int Age=0;

        static Fragment newInstance(String question)
        {
            SeventhQuestion f = new SeventhQuestion();
            Bundle args = new Bundle();
            args.putString("question", question);
            f.setArguments(args);
            return f;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
        {
            super.onCreateView(inflater, container, savedInstanceState);
            String mQuestionHolderText = getArguments().getString("question");
            View v = populateView_question7(inflater, container, mQuestionHolderText);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
            mProgressBar.setProgress(100);
            name=(EditText) v.findViewById(R.id.name);
            age=(EditText)v.findViewById(R.id.age);
            ListView listview = (ListView) v.findViewById(R.id.answer_list);
            List<String> options = null;
            options = Arrays.asList(getResources().getStringArray(R.array.Gender));
            QuestionAdapter adapter = new QuestionAdapter(options, getActivity());
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
            return v;
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
                                                            startActivity(new Intent(getContext(), TherapistActivity.class)
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



    @Override
    public void onBackPressed()
    {

        mPager.setCurrentItem(mPager.getCurrentItem() - 1);

    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if(mPager.getCurrentItem() == 0)
                {
                    startActivity(new Intent(this, Get_Details.class));
                }
                else
                {
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1);

                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

