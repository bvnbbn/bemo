package com.tech.sungkim.bemo;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.viewpagerindicator.CirclePageIndicator;

import me.relex.circleindicator.CircleIndicator;

import static com.tech.sungkim.bemo.R.id.indicator;


public class MainActivity extends FragmentActivity
{

private static final int NUM_PAGES = 2;


    private ViewPager mPager;


    private PagerAdapter mPagerAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPager = (ViewPager) findViewById(R.id.pager);
        mAuth = FirebaseAuth.getInstance();
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        Button b1 = (Button) findViewById(R.id.get_started);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(mPagerAdapter);
        indicator.setViewPager(mPager);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser() != null)
                        mAuth.signOut();
                startActivity(new Intent(MainActivity.this, Precursor.class));

            }
        });
        ClickableSpan spanClick2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(MainActivity.this, LoginUserAct.class);
                intent.putExtra("goLogin", true);
                startActivity(intent);
                finish();
            }
        };

       SpannableString ss = new SpannableString("Already Signed Up? Login");

        ss.setSpan(spanClick2, 19, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = (TextView) findViewById(R.id.al_sign_in_id);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(ContextCompat.getColor(this, R.color.get_started_btn_color));
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }




private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
   public ScreenSlidePagerAdapter(FragmentManager fm) {
       super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
        return new FragA();
        else
        return new FragB();
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}


public static class FragA extends Fragment {
    View view_a;

    public void FragA() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view_a = inflater.inflate(R.layout.splash_loading, container, false);

        return view_a;
    }
}

public static class FragB extends Fragment {
    View view_b;

    public void FragB() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view_b = inflater.inflate(R.layout.activity_info_screen, container, false);

        return view_b;
    }
}
}




