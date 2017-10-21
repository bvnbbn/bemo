package com.tech.sungkim.bemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eftimoff.viewpagertransformers.RotateDownTransformer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.tech.sungkim.adapters.QuestionAdapter;
import com.tech.sungkim.model.Question;
import com.tech.sungkim.util.NonSwipeableViewPager;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.x;


public class QuestionActivity extends AppCompatActivity implements View.OnClickListener
{

    static final int NUM_ITEMS = 7;
    //static final String TAG = "bemo";
    MyAdapter mAdapter;
    static NonSwipeableViewPager mPager;
    private Button mNext;
    private static final Question[] questions = new Question[8];
    private static int result;
    private static Map<String, Integer> tempMap;
    private static boolean[] resultcheck;
    public static TextView mTextview;
    public static ViewGroup containerView;
    private int ProgressStatus=0;
    private  ProgressBar progress;
    private FirebaseUser user_fb;
    private int progress_value=0;

    private DatabaseReference pathFirebase;
    private  static String TAG=Precursor.class.getSimpleName();
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_holder);
        boolean flag = getIntent().getExtras().getBoolean("previous",false);



        SpannableString s = new SpannableString("Assessment");
        s.setSpan(new TypefaceSpan("sans-serif-light"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new MyAdapter(getSupportFragmentManager());
        result = 0;
        mPager = (NonSwipeableViewPager)findViewById(R.id.qanda_pager);
        mPager.setOffscreenPageLimit(10);
        mPager.setActivity(this);

        progress =(ProgressBar)findViewById(R.id.progress);
        progress.setProgressTintList(ColorStateList.valueOf(R.color.progress_bar_color));

        mPager.setPageTransformer(true,new RotateDownTransformer());
        questions[0] = new Question("Feeling nervous, anxious or on edge?");
        questions[1] = new Question("Not being able to stop or control worrying?");
        questions[2] = new Question("Worrying too much about different things?");
        questions[3] = new Question("Trouble relaxing?");
        questions[4] = new Question("Being so restless that it is hard to sit still?");
        questions[5] = new Question("Becoming easily annoyed or irritable?");
        questions[6] = new Question("Feeling afraid as if something\nawful might happen?");

        resultcheck = new boolean[7];
        Arrays.fill(resultcheck, false);
        tempMap = new HashMap<>();





        if(flag)
        {
            for(int i=0;i<NUM_ITEMS;i++)
            {
                SharedPreferences sharedPref = getSharedPreferences("questions",Context.MODE_PRIVATE);
                int selectedValue = sharedPref.getInt(("question"+(i+1)),500);
                if(selectedValue != 500)
                {
                    resultcheck[i] = true;
                    result += selectedValue;
                }

            }
            mPager.setCurrentItem(NUM_ITEMS - 1);

        }


        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {


            }

            @Override
            public void onPageSelected(int position)
            {

                progress_value+=15;
                position+=15;
                Log.e(TAG,String.valueOf(position));
                progress.setProgress(position);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {


            }
        });
        mPager.setAdapter(mAdapter);

    }






    public static boolean areAllTrue(boolean[] array)
    {
        for(boolean b : array) if(!b) return false;
        return true;
    }

    @Override
    public void onClick(View view)
    {


    }

    public static class MyAdapter extends FragmentStatePagerAdapter
    {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }



        @Override
        public int getCount() {
            return NUM_ITEMS;
        }





        @Override
        public Fragment getItem(int position)
        {
            Log.d(TAG, "getItem: ");

            return QuestionFragment.newInstance(questions[position].getmQuestionText(),position);
        }

        public void addThatText()
        {

        }


    }

    public static class QuestionFragment extends Fragment
    {


        String mQuestionHolderText;
        int questionNo;
        int answerChoice;
        boolean alreadyAnswered;
        boolean check;
        int previousAnswer = 500;
        ListView mAnswerListView;
        private SharedPreferences result_pref;


        static QuestionFragment newInstance(String question,int position)
        {
            Log.d(TAG, "newInstance: ");
            QuestionFragment f = new QuestionFragment();
            Bundle args = new Bundle();
            args.putString("question", question);
            args.putInt("question_no",position+1);
          //  args.putInt("progress",);

            f.setArguments(args);
            return f;
        }

        @Override
        public void onSaveInstanceState(Bundle outState)
        {
            super.onSaveInstanceState(outState);
            outState.putInt("answer", answerChoice);
            outState.putBoolean("isAnswered", alreadyAnswered);

        }


        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            mQuestionHolderText = getArguments().getString("question");
            questionNo = getArguments().getInt("question_no");
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {

            /*result_pref = getActivity().getSharedPreferences("result",Context.MODE_PRIVATE);
          //  result_pref = getSharedPreferences("result",Context.MODE_PRIVATE);
            int result1 = result_pref.getInt("result",0);

            if(result1 >0)
            {
                Log.e(TAG,"Entered in the");

                startActivity(new Intent(getActivity(), FinalQuestionActivity.class)
                        .putExtra("result", result));

            }*/





            View v = inflater.inflate(R.layout.question_screen, container, false);
            Log.d(TAG, "onCreateView: ");
            TextView tv = (TextView) v.findViewById(R.id.question_text);

            tv.setText(mQuestionHolderText);

            mAnswerListView = (ListView)v.findViewById(R.id.answer_list);
            List<String> options = null;

            options = Arrays.asList(getResources().getStringArray(R.array.entries));
            QuestionAdapter adapter = new QuestionAdapter(options, getActivity());
            mAnswerListView.setAdapter(adapter);
            SharedPreferences sharedPref = getActivity().getSharedPreferences("questions", Context.MODE_PRIVATE);
            int prevValue = sharedPref.getInt("question"+questionNo,500);
            if(prevValue != 500)
            {
                mAnswerListView.setSelection(prevValue);
            }
            mAnswerListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                                                       @Override
                                                       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                                                       {
                                                           addToResult(i);
                                                           SharedPreferences sharedPref = getActivity().getSharedPreferences("questions",Context.MODE_PRIVATE);
                                                           SharedPreferences.Editor editor = sharedPref.edit();
                                                           editor.putInt("question"+questionNo, i);
                                                           editor.commit();
                                                           alreadyAnswered = true;
                                                           answerChoice = i;
                                                           if(mPager.getCurrentItem() == NUM_ITEMS - 1)
                                                           {
                                                             if (areAllTrue(resultcheck))
                                                             {


                                                                 SharedPreferences  result_pref = getActivity().getSharedPreferences("result",Context.MODE_PRIVATE);
                                                                 SharedPreferences.Editor editor1 = result_pref.edit();
                                                                 editor1.putInt("result",result);

                                                                 startActivity(new Intent(getActivity(), FinalQuestionActivity.class)
                                                                       .putExtra("result", result));


                                                             }
                                                             else
                                                             {

                                                             }
                                                           }
                                                           if (mPager.getCurrentItem() < 7)
                                                           {
                                                               mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                                                           }


                                                       }
                                                   });








            if(savedInstanceState != null)
            {
                previousAnswer = savedInstanceState.getInt("answer");
                mAnswerListView.setSelection(x);
                if(savedInstanceState.getBoolean("isAnswered"))
                {
                    check = true;
                }

            }

            ///  rg = (RadioGroup) v.findViewById(R.id.answer);
           /* rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    addToResult();
                }
            });
*/
            return v;
        }

        public void addToResult(int position)
        {

               if(tempMap.containsKey(mQuestionHolderText))
               {
                    int toSub = tempMap.get(mQuestionHolderText);
                    result = result - toSub;
                }
                switch (position)
                {
                    case 0:
                        result += 0;
                        tempMap.put(mQuestionHolderText,0);
                        break;
                    case 1:
                        result += 1;
                        tempMap.put(mQuestionHolderText,1);
                        break;
                    case 2:
                        result += 2;
                        tempMap.put(mQuestionHolderText,2);
                        break;
                    case 3:
                        result += 3;
                        tempMap.put(mQuestionHolderText,3);
                        break;
                }
            resultcheck[mPager.getCurrentItem()] = true;

        }

        @Override
        public void onDetach()
        {
            super.onDetach();

        }
    }

    @Override
    public void onBackPressed()
    {

      //  progress_value-=12;
       // progress =(ProgressBar)findViewById(R.id.progress);
      //  progress.setProgress(progress_value);
        mPager.setCurrentItem(mPager.getCurrentItem() - 1);

    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if(mPager.getCurrentItem() == 0)
                {
                    startActivity(new Intent(this, Precursor.class));
                }
                else
                {
                    progress_value-=15;
                   // progress =(ProgressBar)findViewById(R.id.progress);
                    progress.setProgress(progress_value);
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
