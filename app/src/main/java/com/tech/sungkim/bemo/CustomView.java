package com.tech.sungkim.bemo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by vikas on 16/6/17.
 */

/* Custom View for the text to be displayed in the grid view which will get highlighted
 * when the user touches it */

public class CustomView extends FrameLayout
{
    TextView textView;
    public CustomView(Context context)
    {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.text,this);
        textView = (TextView)getRootView().findViewById(R.id.textView);

    }


    public void display(String text,boolean isSelected)
    {
        textView.setText(text);
        display(isSelected);
    }

    public void display(boolean isSelecetd)
    {
        textView.setBackgroundColor(isSelecetd? Color.RED:Color.WHITE);
    }

}
