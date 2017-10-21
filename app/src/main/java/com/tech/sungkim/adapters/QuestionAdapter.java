package com.tech.sungkim.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tech.sungkim.bemo.R;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.resource;
import static com.razorpay.Segment.context;

/**
 * Created by HP-PC on 19-04-2017.
 */

public class QuestionAdapter extends ArrayAdapter<String>
{
    private final List<String> dataSet;

    public QuestionAdapter(List<String> data, Context context)
    {
        super(context,0, data);
        this.dataSet = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        View thisView = convertView;

        if(thisView == null)
            thisView = LayoutInflater.from(getContext()).inflate(R.layout.answer_list_item_view,parent,false);
        String answerInfo = getItem(position);
        TextView view = (TextView) thisView.findViewById(R.id.answer);
        view.setText(answerInfo);
        return thisView;
    }

}
