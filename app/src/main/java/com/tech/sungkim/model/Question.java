package com.tech.sungkim.model;

/**
 * Created by HP-PC on 30-03-2017.
 */

public class Question {
    private String mQuestionText;
    private int answer;

    public Question(String question){
        mQuestionText = question;
    }

    public String getmQuestionText() {
        return mQuestionText;
    }

    public void setmQuestionText(String mQuestionText) {
        this.mQuestionText = mQuestionText;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }
}
