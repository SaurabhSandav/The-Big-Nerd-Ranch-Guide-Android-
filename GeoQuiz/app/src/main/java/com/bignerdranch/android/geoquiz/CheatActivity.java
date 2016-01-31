package com.bignerdranch.android.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown";
    private static final String KEY_ANSWER_SHOWN = "answer_shown";
    private boolean answerIsTrue;
    private boolean answerShown;
    private TextView answerTextView;
    private Button showAnswer;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, boolean answerShown) {
        Intent i = new Intent(packageContext, CheatActivity.class);
        i.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        i.putExtra(EXTRA_ANSWER_SHOWN, answerShown);
        return i;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        answerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        answerShown = getIntent().getBooleanExtra(EXTRA_ANSWER_SHOWN, false);

        answerTextView = (TextView) findViewById(R.id.answerTextView);
        TextView apiLevelTextView = (TextView) findViewById(R.id.apiLevelTextView);

        apiLevelTextView.setText("API level " + Build.VERSION.SDK_INT);

        showAnswer = (Button) findViewById(R.id.showAnswerButton);
        showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerTextView.setText(answerIsTrue ? R.string.true_button : R.string.false_button);
                answerShown = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    int cx = showAnswer.getWidth() / 2;
                    int cy = showAnswer.getHeight() / 2;
                    float radius = showAnswer.getWidth();
                    Animator anim = ViewAnimationUtils
                            .createCircularReveal(showAnswer, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            hideButton();
                        }
                    });
                    anim.start();
                } else {
                    hideButton();
                }
            }
        });

        if (savedInstanceState != null)
            answerShown = savedInstanceState.getBoolean(KEY_ANSWER_SHOWN);

        if (answerShown) {
            answerTextView.setText(answerIsTrue ? R.string.true_button : R.string.false_button);
            hideButton();
        }
    }

    private void hideButton() {
        showAnswer.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_ANSWER_SHOWN, answerShown);
    }

    @Override
    public void finish() {
        setAnswerShownResult(answerShown);
        super.finish();
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

}
