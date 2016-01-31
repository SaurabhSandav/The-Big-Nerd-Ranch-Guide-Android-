package com.bignerdranch.android.geoquiz;

/**
 * Created by saurabh on 16/1/16.
 */
public class Question {

    private int textResId;
    private boolean answerTrue;

    private boolean cheated;

    public Question(int textResId, boolean answerTrue) {
        this.textResId = textResId;
        this.answerTrue = answerTrue;
    }

    public int getTextResId() {
        return textResId;
    }

    public boolean isAnswerTrue() {
        return answerTrue;
    }

    public boolean isCheated() {
        return cheated;
    }

    public void setCheated(boolean cheated) {
        this.cheated = cheated;
    }
}
