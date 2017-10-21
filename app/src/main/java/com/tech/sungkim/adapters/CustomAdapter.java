package com.tech.sungkim.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tech.sungkim.bemo.BeforeChat;
import com.tech.sungkim.bemo.BeforeChat.ThirdQuestion;
import com.tech.sungkim.bemo.CustomView;
import com.tech.sungkim.bemo.R;

import java.util.ArrayList;
import java.util.List;

import static com.tech.sungkim.bemo.BeforeChat.*;

/**
 * Created by vikas on 16/6/17.
 */




/*Custom adapter for the grid view where multiple choices are to be selected *
Problem is we cannot pass context of the Fragment class which is declared within the BeforeChat class
because it is a nested class
 */

public class CustomAdapter extends ArrayAdapter<String>
{
    private String[] strings;
    private List<String> options = new ArrayList<>();
    List<Integer> selectedPositions = new ArrayList<>();

    public CustomAdapter(List<String> options,Context context)
    {
        super(context,0,options);
        this.options=options;

    }

    @Override
    public int getCount()
    {
        return 0;
    }


    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View thisView = view;

        if(thisView == null)
            thisView = LayoutInflater.from(getContext()).inflate(R.layout.answer_list_item_view,viewGroup,false);
        String answerInfo = getItem(i);
        TextView view1 = (TextView) thisView.findViewById(R.id.answer);
        view1.setText(answerInfo);
        return thisView;




      /*  CustomView customView;
        if (view == null)
        {
            customView = new CustomView(BeforeChat.context);
        }
        else customView = (CustomView) view;
        customView.display(options.get(i), selectedPositions.contains(i));
        return customView;*/

    }
}