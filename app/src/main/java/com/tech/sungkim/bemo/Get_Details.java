package com.tech.sungkim.bemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by vikas on 12/6/17.
 */

public class Get_Details extends AppCompatActivity implements View.OnClickListener
{
    private Button set_preference_button;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_details);
        SpannableString s = new SpannableString("Finding bemo");
        s.setSpan(new TypefaceSpan("sans-serif-light"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        set_preference_button=(Button)findViewById(R.id.set_preference_button);
        set_preference_button.setOnClickListener(this);
    }


    @Override
    public void onClick(View view)
    {
        int id =view.getId();
        if(id == R.id.set_preference_button)
        {
            startActivity(new Intent(this, Before_chat.class));

        }
    }
    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(Get_Details.this, PlanActivity.class));

    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                startActivity(new Intent(Get_Details.this, PlanActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


}
